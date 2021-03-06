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

package com.recomdata.grails.rositaui.thread;

import java.util.HashMap;
import java.util.Map;

public class VerifierRunnable extends AbstractStepRunnable {

	String filename = "";
	
	public Integer getWorkflowStep() { return 1; }
	public String getCommandName() { return "verify"; }
	
	public void setFilename(String name) {
		this.filename = name;
	}
	
	public String[] getProcessParams() {
		return new String[] {scriptDir+scriptFile, getCommandName(), "file="+filename, "jobid="+jobId.toString(), "stepid="+stepId.toString(), "forui"};
	}
	
	public Map<String, Object> getStepStatus() {
		Map<String, Object> status = new HashMap<String, Object>();
		String[] output = latestOutput.split("\\|\\|\\|");
		//Check to see if this is a status message or exception
		if (output.length > 1) {
			status.put("messageType", output[0]);
			status.put("latestOutput", output[1]);
		}
		else {
			status.put("latestOutput", output);
		}
		status.put("filename", filename);
		return status;
	}

}
