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

class RositaJob {
	
	Long id
	Integer workflowStep
	Date startDate
	Date endDate
	String schemaName
	String fileName
	Long totalElements
	String name
	
	static hasMany = [workflowSteps: WorkflowStep, workflowSignals: WorkflowSignal]

	static mapping = {
		table 'cz.cz_rosita_job'
		version false
		columns {
			id column: 'job_id'
		}
	}
	
    static constraints = {
		startDate (nullable: true)
		endDate (nullable: true)
		totalElements (nullable: true)
		name (nullable: true)
    }
	
	static transients = ['shortFilename', 'latestWorkflowStep']
	
	String getShortFilename() {
		if (fileName) {
			int index = fileName.lastIndexOf("/")+1;
			if (index > -1) {
				return fileName.substring(index)
			}
			index = fileName.lastIndexOf("\\")+1;
			if (index > -1) {
				return fileName.substring(index)
			}
			return fileName;
		}
		return "";
	}
	
	WorkflowStep getLatestWorkflowStep() {
		def wfSteps = WorkflowStep.createCriteria().list() {
			eq('job', this)
			order('startDate', 'desc')
		}
		if (wfSteps) {
			return wfSteps[0];
		}
		return null;
	}
}
