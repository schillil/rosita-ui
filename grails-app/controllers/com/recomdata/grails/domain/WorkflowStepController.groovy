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

class WorkflowStepController {
	
	def fileService

    def error = {
		def paramMap = params
		def model = sanitizeParams(params);
		if (model.workflowStep.equals('7')) {
			model.put("files", fileService.getFiles(".csv|.txt"))
		}
		render(template: "error", model: model)
	}
	
	def status = {
		def paramMap = params
		def model = sanitizeParams(params);
		model.put("estimateMs", getTimeRemaining(model.stepId))
		render(template: "status", model: model)
	}
	
	def paused = {
		def paramMap = params
		def model = sanitizeParams(params);
		model.put("jasperUrl", grailsApplication.config.rosita.jasper.url)
		
		//For export, check for new values
		if (model.workflowStep.equals('6')) { 
			model.put("hasUnmapped", checkUnmappedValues())
		}
		else if (model.workflowStep.equals('7')) {
			model.put("files", fileService.getFiles(".csv|.txt"))
		}
		render(template: "paused", model: model)
	}
	
	def sanitizeParams(prms) {
		def sanitizedParams = [:]
		for (param in prms) {
			String key = param.key
			String value = param.value
			if (key.startsWith("params[")) {
				sanitizedParams.put(key.substring(7, key.length()-1), value)
			}
			else {
				sanitizedParams.put(key, value)
			}
		}
		
		return sanitizedParams
	}
	
	def getTimeRemaining(wfStepId) {
		
		//Get this step and the latest step of its type
		WorkflowStep wfStep = WorkflowStep.get(wfStepId)
		if (!wfStep) { return null; }
		
		def oldWfSteps = WorkflowStep.createCriteria().list([max: 1]) {
			eq('workflowStep', wfStep.workflowStep)
			eq('state', 'success')
			order('startDate', 'desc')
		}
		if (!oldWfSteps) { return null; }
		def oldWfStep = oldWfSteps[0];
		
		Date lastRunStart = oldWfStep.startDate;
		Date lastRunEnd = oldWfStep.endDate;
		Long targetTimeMs = lastRunEnd.getTime() - lastRunStart.getTime();
		//println("Got old wf step: " + lastRunStart + " " + lastRunEnd + ", ran for " + targetTimeMs);
		
		Date thisRunStart = wfStep.startDate;
		Date now = new Date();
		Long elapsedMs = now.getTime() - thisRunStart.getTime();
		//println("This step started at " + thisRunStart + ", now = " + now + ", been running for " + elapsedMs)
		
		Long timeRemaining = targetTimeMs - elapsedMs
		//println(timeRemaining + " remaining")
		return timeRemaining
	}
	
	boolean checkUnmappedValues() {
		def mappings = ConceptMap.findAllByMapped('N');
		return mappings
	}
}
