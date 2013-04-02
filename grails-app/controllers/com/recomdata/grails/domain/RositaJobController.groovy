/*
*   Copyright 2012-2013 The Regents of the University of Colorado
*
*   Licensed under the Apache License, Version 2.0 (the "License")
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

package com.recomdata.grails.domain

import org.codehaus.groovy.grails.commons.ConfigurationHolder;

import com.recomdata.grails.rositaui.service.*;

import grails.converters.JSON

class RositaJobController {

    def lowerCaseService
	def fileService
	def verifyService
	def validatorService
	def truncatorService
	def parserService
	def profilerService
	def publisherService
	def omopProfilerService
	def processorService
	def backupService
	def vocabularyImportService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	
	def index = {
		def browseFolder = fileService.getBrowseFolder()
		def xmlfiles = fileService.getFiles("xml")
        def ffdirs = fileService.getFlatFileDirs()
		
		def jobs = RositaJob.createCriteria().list ([max: 10]){
			order('id', 'desc')
		}
		
		//Set a flag if we have a job currently in progress, and get latest file.
		def latestFilename = "";
		def latestWorkflowStep = null;
		def jobInProgress = false;
		if (jobs) {
			def firstJob = jobs.get(0);
			jobInProgress = firstJob.endDate ? false : true
			latestFilename = firstJob.fileName;
			latestWorkflowStep = firstJob.getLatestWorkflowStep()
		}
		
		response.setHeader('Cache-Control', 'no-cache, no-store, must-revalidate')
		response.setIntHeader('Expires', -1)
		[jobs: jobs, jobInProgress: jobInProgress,
                workflowTitles: WorkflowTitle.listOrderById(),
                latestFilename: latestFilename,
                latestWorkflowStep: latestWorkflowStep,
                browseFolder: browseFolder,
                files: xmlfiles += ffdirs]
	}
	
	def jobs = {
		def offset = (params.int('offset') ?: 0)
		def max = (params.int('max') ?: 50)
		def jobs = RositaJob.createCriteria().list ([max: max, offset: offset]){
			order('id', 'desc')
		}
		def jobInProgress = false;
		if (jobs) {
			def firstJob = jobs.get(0);
			jobInProgress = firstJob.endDate ? false : true
		}
		
		[jobs: jobs, jobCount: jobs.getTotalCount(), jobInProgress: jobInProgress, offset: offset, max: max, workflowTitles: WorkflowTitle.listOrderById()]
	}
	
	def causeAnError = {
		RositaJob.thisMethodDoesNotExist()
	}
	
	def history = {
		def rositaJobInstance = RositaJob.get(params.id)
		if (!rositaJobInstance) {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'rositaJob.label', default: 'RositaJob'), params.id])}"
			redirect(action: "index")
		}
		else {
			def steps = WorkflowStep.createCriteria().list() {
				eq('job', rositaJobInstance);
				order('startDate', 'asc');
			}
			
			[rositaJobInstance: rositaJobInstance, steps: steps, workflowTitles: WorkflowTitle.listOrderById()]
		}
	}
	
    def show = {
        def rositaJobInstance = RositaJob.get(params.id)
        if (!rositaJobInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'rositaJob.label', default: 'RositaJob'), params.id])}"
            redirect(action: "index")
        }
        else {
			def workflowSteps = []
			for (int i = 1; i < 14; i++) {
				def latestSteps = WorkflowStep.createCriteria().list() {
					eq('job', rositaJobInstance);
					eq('workflowStep', i);
					order('startDate', 'desc');
				}
				if (latestSteps) {
					workflowSteps.push(latestSteps[0])
				}
				else {
					workflowSteps.push(null)
				}
			}

			response.setHeader('Cache-Control', 'no-cache, no-store, must-revalidate')
			response.setIntHeader('Expires', -1)
            [rositaJobInstance: rositaJobInstance, workflowSteps: workflowSteps, workflowTitles: WorkflowTitle.listOrderById()]
        }
    }
	
	def start = {
		def rositaJobInstance = RositaJob.get(params.id)
		def wfStep = params.int('step')
		if (!wfStep) {
			wfStep = 1;
		}
		//Update job and workflow step to indicate that we've started
		WorkflowStep wf = new WorkflowStep(startDate: new Date(), workflowStep: wfStep, state: 'running');
		rositaJobInstance.addToWorkflowSteps(wf);
		rositaJobInstance.save(flush: true);
		
		//Now start the given step and confirm
		if (wfStep == 1) {
			rositaJobInstance.startDate = new Date();
			rositaJobInstance.workflowStep = 1;
			rositaJobInstance.save(flush: true);
			verifyService.start(rositaJobInstance, wf)
		}
		else if (wfStep == 2) {
			validatorService.start(rositaJobInstance, wf)
		}
		else if (wfStep == 3) {
			parserService.start(rositaJobInstance, wf)
		}
		else if (wfStep == 4) {
			profilerService.start(rositaJobInstance, wf)
		}
		else if (wfStep == 5) {
			processorService.start(rositaJobInstance, wf)
		}
		else if (wfStep == 9) {
			omopProfilerService.start(rositaJobInstance, wf)
		}
		else if (wfStep == 11) {
			publisherService.start(rositaJobInstance, wf)
		}
		else if (wfStep == 12) {
			backupService.start(rositaJobInstance, wf)
		}
		render "OK";
	}
	
	//Confirm - for steps that need an outside action, but have only one outcome.
	def confirm = {
		def rositaJobInstance = RositaJob.get(params.id)
		def wfStepId = params.long('wfStep')
		def stepNumber = params.long('step')
		//Update the current step to show it was completed
		def oldWfStep = WorkflowStep.get(wfStepId);
		oldWfStep.state = "success"
		oldWfStep.endDate = new Date()
		oldWfStep.save(flush: true);
		
		def newState = 'running';
		if (stepNumber == 5 || stepNumber == 6) { //Validate to Export/Export to Import - immediately pause
			newState = 'paused';
		}
		//Update job and add workflow step
		WorkflowStep wf = new WorkflowStep(startDate: new Date(), workflowStep: stepNumber+1, state: newState);
		rositaJobInstance.addToWorkflowSteps(wf);
		rositaJobInstance.workflowStep = stepNumber+1;
		rositaJobInstance.save(flush: true);
		
		if (stepNumber == 10) { //Validate to Publish
			publisherService.start(rositaJobInstance, wf);
		}
		
		render "OK";
	}
	
	def runImport = {
		def rositaJobInstance = RositaJob.get(params.id)
		def workflowStep = WorkflowStep.get(params.stepId)
		def filename = grailsApplication.config.rosita.browse.folder + "/" + params.filename
		
		if (workflowStep.workflowStep == 7) {
			
			//Update paused workflow step to show running
			workflowStep.state = "running";
			workflowStep.save(flush: true);
			
			//Start the service
			vocabularyImportService.start(rositaJobInstance, workflowStep, filename);
			
			render "OK"
			return
		}
		
		render(status:500, text:"This step is not an import step!")
		
	}
	
	//Skip - for steps that are optional. Mark them specifically as skipped
	def skip = {
		def rositaJobInstance = RositaJob.get(params.id)
		def wfStepId = params.long('wfStep')
		def stepNumber = params.long('step')
		//Update the current step to show it was skipped
		def oldWfStep = WorkflowStep.get(wfStepId);
		oldWfStep.state = "skipped"
		oldWfStep.endDate = new Date()
		oldWfStep.save(flush: true);
		
		def newState = 'running';
		if (stepNumber == 6) { //Want to immediately pause step 7
			newState = 'paused';
		}
		//Update job and add workflow step
		WorkflowStep wf = new WorkflowStep(startDate: new Date(), workflowStep: stepNumber+1, state: newState);
		rositaJobInstance.addToWorkflowSteps(wf);
		rositaJobInstance.workflowStep = stepNumber+1;
		rositaJobInstance.save(flush: true);
		
		if (stepNumber == 7) {
			processorService.start(rositaJobInstance, wf);
		}
		
		render "OK";
	}
	
	def create = {
		
		Properties props = new Properties();
		props.load(new FileInputStream(ConfigurationHolder.config.rosita.jar.path + "/rosita.properties"))
		def filename = params.filename;

        //if filename is a directory then flat files are being processed so run tolowercase.sh to normalize the names
        //File file = new File(filename);
        //if (file.isDirectory()) {
            //lowerCaseService.start(filename);
        //}

		def name = params.name;
		
		def latestSameFile = RositaJob.createCriteria().list() {
			eq('fileName', filename)
			order('startDate', 'desc')
		}
		
		def lastElementCount = 0;
		if (latestSameFile) {
			lastElementCount = latestSameFile[0].totalElements;
		}
		
		
		RositaJob newJob = new RositaJob(schemaName: props.get("schema"), name: name, fileName: filename, workflowStep: 0, totalElements: lastElementCount);
		newJob.save();
		
		WorkflowStep wf = new WorkflowStep(startDate: new Date(), workflowStep: 1, state: 'running');
		newJob.addToWorkflowSteps(wf);
		newJob.startDate = new Date();
		newJob.workflowStep = 1;
		newJob.save(flush: true);
		verifyService.start(newJob, wf)
		
		redirect(action: 'show', id: newJob.id);

	}
	
	def restart = {
		def rositaJobInstance = RositaJob.get(params.id)
		def latestSteps = WorkflowStep.createCriteria().list() {
			eq('job', rositaJobInstance);
			order('startDate', 'desc');
		}
		def wf = null;
		if (latestSteps) {
			wf = latestSteps[0];
		}
		if (!wf || !wf.state.equals('running')) {
			//Copy this job and redirect to the new show page
			RositaJob newJob = new RositaJob(schemaName: rositaJobInstance.schemaName, fileName: rositaJobInstance.fileName, workflowStep: 0, totalElements: rositaJobInstance.totalElements);
			newJob.save(flush: true);
			redirect(action: 'show', id: newJob.id);
		}
		else {
			//TODO We can't restart if a step is already running - ignore.
			redirect(action: 'show', id: params.id);
		}
	}
	
	def cancel = {
		def rositaJobInstance = RositaJob.get(params.id)

		//Force cancel this job
		rositaJobInstance.endDate = new Date();
		rositaJobInstance.save(flush: true);
		redirect(action: 'index');

	}
	
	def status = {
		def rositaJobInstance = RositaJob.get(params.id)
		//Find the workflow step we're on
		def latestSteps = WorkflowStep.createCriteria().list() {
			eq('job', rositaJobInstance);
			order('startDate', 'desc');
		}
		
		//If nothing, the job hasn't started.
		if (!latestSteps) {
			def status = [:]
			status.put("status", "pending")
			render status as JSON;
		}
		else {
			def wf = latestSteps[0];
			def status = [:]
			if (wf.workflowStep == 1) {
				status = verifyService.getStatus();
			}
			else if (wf.workflowStep == 2) {
				status = validatorService.getStatus();
			}
			else if (wf.workflowStep == 3) {
				status = parserService.getStatus();
			}
			else if (wf.workflowStep == 4) {
				status = profilerService.getStatus();
			}
			else if (wf.workflowStep == 5) {
				//No status, outside step
			}
			else if (wf.workflowStep == 6) {
				//Not yet done
			}
			else if (wf.workflowStep == 7) {
				//Not yet done
			}
			else if (wf.workflowStep == 8) {
				status = processorService.getStatus();
			}
			else if (wf.workflowStep == 9) {
				status = omopProfilerService.getStatus();
			}
			else if (wf.workflowStep == 10) {
				//No status, outside step
			}
			else if (wf.workflowStep == 11) {
				status = publisherService.getStatus();
			}
			else if (wf.workflowStep == 12) {
				status = backupService.getStatus();
			}

			status.put('workflowStep', wf.workflowStep)
			status.put('stepId', wf.id);
			status.put('totalElements', rositaJobInstance.totalElements)
			status.put("status", wf.state)

			render status as JSON
		}
	}
	
	def statusverify = {
		render(verifyService.getStatus());
	}
	
	def getConsoleIcon = {
		//Get latest step ID for this job and workflow step
		def latestStep = null
		def latestSteps = WorkflowStep.createCriteria().list() {
			eq('job.id', params.long('jobId'));
			eq('workflowStep', params.int('stepId'));
			order('startDate', 'desc');
		}
		if (latestSteps) {
			latestStep = latestSteps[0].id;
		}
		//Render the template with this ID
		if (latestStep) {
			render (template: 'consoleLink', model: [id: latestStep])
		}
		else {
			render (contentType: 'text/plain', text: " ");
		}
	}
}
