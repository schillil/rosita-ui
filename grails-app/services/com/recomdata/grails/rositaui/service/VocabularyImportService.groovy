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

package com.recomdata.grails.rositaui.service

import com.recomdata.grails.domain.RositaJob;
import com.recomdata.grails.domain.WorkflowStep;
import com.recomdata.grails.rositaui.thread.ImporterRunnable
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class VocabularyImportService {

    static transactional = true
	
	static Thread importer = new Thread()
	static Runnable myRunnable = new ImporterRunnable()
	
	//Start a new importer thread and run it
    def start(RositaJob j, WorkflowStep wf, String filename) {
		if (!importer.isAlive()) {

			myRunnable.setScriptpath(ConfigurationHolder.config.rosita.jar.path)
			myRunnable.setUnix(ConfigurationHolder.config.rosita.unix)
			myRunnable.setJobId(j.id);
			myRunnable.setStepId(wf.id);
			myRunnable.setFilename(filename);
		
			importer = new Thread(myRunnable)
			importer.setName("Vocabulary import thread")
			importer.start();
			return "Importing started"
		}
		else {
			return "The importer is already running!"
		}
    }
	
	def cancelValidation() {
		if (importer.isAlive()) {
			importer.interrupt();
			return ("Importing cancelled");
		}
		else {
			return "No importer was running";
		}
	}
	
	def getStatus() {
		return myRunnable.getStatus()
	}
}
