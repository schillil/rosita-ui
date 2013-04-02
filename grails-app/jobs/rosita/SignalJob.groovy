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

package rosita

import com.recomdata.grails.domain.RositaJob;
import com.recomdata.grails.domain.WorkflowSignal;
import com.recomdata.grails.domain.WorkflowStep;
import com.recomdata.grails.rositaui.service.ValidatorService;
import com.recomdata.grails.rositaui.service.TruncatorService;
import com.recomdata.grails.rositaui.service.ParserService;
import com.recomdata.grails.rositaui.service.ProfilerService;

class SignalJob {
	ValidatorService validatorService
	TruncatorService truncatorService
	ParserService parserService
	ProfilerService profilerService
	def publisherService
	def omopProfilerService
	def processorService
	def backupService

	Boolean status = true
	def concurrent = false
	
    static triggers = {
		
		def startDelay = 10000
		def repeatInterval = 10000
			simple name: 'signalTrigger',
				   startDelay: startDelay,
				   repeatInterval: repeatInterval
	}
	

    def execute() {
		
		def signals = WorkflowSignal.createCriteria().list() {
			eq('pending', true)
			order('date', 'asc');
		}
		
		if (signals) {
			def signal = signals[0];
			RositaJob job = signal.job
			println("Received a signal, acting on it")
			//Act on signal - if success, start a new workflow step for the job. If not, leave it alone.
			def currentWfList = WorkflowStep.createCriteria().list() {
				eq('job', job)
				eq('workflowStep', signal.workflowStep)
				order('startDate', 'desc')
			}
			
			def currentWf
			if (currentWfList) {
				currentWf = currentWfList[0]
			}
			
			signal.pending = false
			
			if (!currentWf || !currentWf.state.equals('running')) {
				signal.save(flush: true)
				println("Workflow step no longer existed, ignoring")
				return; //Do nothing if this signal has no workflow step
			}
			
			currentWf.state = signal.success ? 'success' : 'failed'
			currentWf.endDate = new Date()
			currentWf.message = signal.message
            println("signal.message = " + signal.message);
			currentWf.save(flush:true)
			
			println("Saved workflow state")
			//Any special updates to the job for this message
			if (signal.workflowStep == 2) {
				job.totalElements = Long.parseLong(signal.message.split("\\|\\|\\|")[2])
			}
			
			
			//If successful, advance workflow to the next step
			if (signal.success) {
				println("Signal indicated success, starting new workflow step")
				String state = 'running'
				
				//If originating workflow step is before one that needs to pause, set state
				if (signal.workflowStep == 4) {
					println("Inserting paused record for workflow step 5 (validate)")
					state = 'paused'
				}
				if (signal.workflowStep == 5) {
					println("Inserting paused record for workflow step 6 (export)")
					state = 'paused'
				}
				if (signal.workflowStep == 6) {
					println("Inserting paused record for workflow step 7 (import)")
					state = 'paused'
				}
				if (signal.workflowStep == 9) {
					println("Inserting paused record for workflow step 10 (validate OMOP)")
					state = 'paused'
				}
				if (signal.workflowStep == 12) {
					println("Inserting completed record for workflow step 13 (complete)")
					state = 'completed'
					job.endDate = new Date()
				}
				
				WorkflowStep newWf = new WorkflowStep(job: currentWf.job, workflowStep: currentWf.workflowStep+1, startDate: new Date(), state: state)
				job.workflowStep = currentWf.workflowStep+1;
				job.save();
				newWf.save(flush:true)
				
				//Special actions for starting a new step here
				if (newWf.workflowStep == 2) {
					validatorService.start(currentWf.job, newWf)
				}
				else if (newWf.workflowStep == 3) {
					parserService.start(currentWf.job, newWf)
				}
				else if (newWf.workflowStep == 4) {
					profilerService.start(currentWf.job, newWf)
				}
				else if (newWf.workflowStep == 8) {
					processorService.start(currentWf.job, newWf)
				}
				else if (newWf.workflowStep == 9) {
					omopProfilerService.start(currentWf.job, newWf)
				}
				else if (newWf.workflowStep == 11) {
					publisherService.start(currentWf.job, newWf)
				}
				else if (newWf.workflowStep == 12) {
					backupService.start(currentWf.job, newWf)
				}
			}
		}
    }
}
