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
import com.recomdata.grails.rositaui.thread.VerifierRunnable
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class VerifyService {

    static transactional = true
	
	static Thread myThread = new Thread()
	static Runnable myRunnable = new VerifierRunnable()
	
	def getStatus = {
		InputStream is = verifyProcess.getInputStream();
	}
	
	def start(RositaJob j, WorkflowStep wf) {
		if (!myThread.isAlive()) {
			
			myRunnable.setSchemaname(j.schemaName)
			myRunnable.setFilename(j.fileName)
			myRunnable.setScriptpath(ConfigurationHolder.config.rosita.jar.path)
			myRunnable.setJobId(j.id);
			myRunnable.setUnix(ConfigurationHolder.config.rosita.unix)
			myRunnable.setStepId(wf.id);
			
			myThread = new Thread(myRunnable)
			myThread.setName("Verifier Thread")
			myThread.start();
			return "Verification started"
		}
		else {
			return "The verifier is already running!"
		}
	}
	
	def cancel() {
		if (myThread.isAlive()) {
			myThread.interrupt();
			return ("Verification cancelled");
		}
		else {
			return "No verifier was running";
		}
	}
	
	def getStatus() {
		//if (myThread.isAlive()) {
			return myRunnable.getStatus()
		//}
		//else {
		//	return [:]
		//}
	}
}
