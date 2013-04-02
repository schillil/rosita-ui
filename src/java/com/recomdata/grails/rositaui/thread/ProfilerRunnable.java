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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.recomdata.grails.rositaui.utils.SignalService;

public class ProfilerRunnable implements Runnable {

	boolean unix = false;
	String scriptPath = "";
	Long jobId = 0L;
	Long stepId = 0L;
	
	String latestOutput = "";
	int exitCode = -1;
	
	public ProfilerRunnable() {
		
	}

	@Override
	public void run() {
		try {
			exitCode = -1;
			latestOutput = "";
			String canonicalPath = "";
			SignalService sig = SignalService.getInstance();
			if (!scriptPath.endsWith("/")) {
				scriptPath = scriptPath + "/";
			}
			String suffix = (unix ? ".sh" : ".bat");
			String scriptfile = "./" + "profilesource" + suffix; //Ensures that a Unix process can start, even if . is not in PATH
			ProcessBuilder pb = new ProcessBuilder(scriptPath + scriptfile, jobId.toString(), "forui");
			canonicalPath = new File(scriptPath).getCanonicalPath();
			pb.directory(new File(canonicalPath));
			pb.redirectErrorStream(true);
			Process process = pb.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			System.out.println("Started process and reading...");
			sig.sendConsole(stepId, "START");
			while ((line = r.readLine()) != null) {
				latestOutput = line;
				System.out.println(line);
				sig.sendConsole(stepId, line);
			}
			System.out.println("...Finished reading. Process exit code was " + process.waitFor());
			sig.sendConsole(stepId, "END");
			exitCode = process.exitValue();
			sig.sendSignal(jobId, 4, exitCode == 0);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void setScriptpath(String name) {
		this.scriptPath = name;
	}
	
	public void setJobId(Long id) {
		this.jobId = id;
	}
	
	public void setUnix(boolean unix) {
		this.unix = unix;
	}
	
	public void setStepId(Long id) {
		this.stepId = id;
	}
	
	public Map<String, Object> getStatus() {
		Map<String, Object> status = new HashMap<String, Object>();
		String[] output = latestOutput.split("\\|\\|\\|");
		//Check to see if this is a status message or exception
		if (output[0].equals("STATUS")) {
			status.put("messageType", output[0]);
			status.put("latestOutput", output[1]);
		}
		else {
			status.put("messageType", "ERROR");
			status.put("latestOutput", output[0]);
		}
		status.put("exitCode", exitCode);
		return status;
	}

}
