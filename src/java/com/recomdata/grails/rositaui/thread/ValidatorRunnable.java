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

public class ValidatorRunnable implements Runnable {

	boolean unix = false;
	String filename = "";
	String schema = "";
	String scriptPath = "";
	Long jobId = 0L;
	Long stepId = 0L;
	Process process = null;
	
	String latestOutput = "";
	int exitCode = -1;
	
	SignalService sig = null;
	
	public ValidatorRunnable() {
		
	}

	@Override
	public void run() {
		try {
			sig = SignalService.getInstance();
			exitCode = -1;
			latestOutput = "";
			String canonicalPath = "";
			if (!scriptPath.endsWith("/")) {
				scriptPath = scriptPath + "/";
			}
			String suffix = (unix ? ".sh" : ".bat");
			String scriptfile;
			//int f1 = filename.lastIndexOf(".xml");
			//int f2 = filename.length();
			if (filename.lastIndexOf(".xml") == filename.length() - 4) {
				scriptfile = "./" + "validatexml" + suffix; //Ensures that a Unix process can start, even if . is not in PATH
			} else {
				scriptfile = "validatecsv" + suffix;
			}
			System.out.println("Validating " + filename);
			ProcessBuilder pb = new ProcessBuilder(scriptPath + scriptfile, filename, String.valueOf(stepId), "forui");
			canonicalPath = new File(scriptPath).getCanonicalPath();
			pb.directory(new File(canonicalPath));
			pb.redirectErrorStream(true);
			process = pb.start();
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
			sig.sendSignal(jobId, 2, exitCode == 0, latestOutput);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		finally {
			process = null;
		}
	}
	
	public void setFilename(String name) {
		this.filename = name;
	}
	
	public void setSchemaname(String name) {
		this.schema = name;
	}
	
	public void setScriptpath(String name) {
		this.scriptPath = name;
	}
	
	public void setJobId(Long id) {
		this.jobId = id;
	}
	
	public void setStepId(Long id) {
		this.stepId = id;
	}
	
	public void setUnix(boolean unix) {
		this.unix = unix;
	}
	
	public Map<String, Object> getStatus() {
		Map<String, Object> status = new HashMap<String, Object>();
		
		if (process == null && exitCode == -1) {
			status.put("status", "failed");
			return status;
		}
		
		status.put("filename", filename);
		status.put("schemaname", schema);
		String[] stats = latestOutput.split("\\|\\|\\|");
		if (stats[0].equals("COMPLETE")) {
			status.put("messageType", stats[0]);
			status.put("status", "success");
			status.put("errors", stats[1]);
			status.put("elements", stats[2]);
		}
		else if (stats[0].equals("STATUS")) { //Normal status message
			status.put("messageType", stats[0]);
			status.put("elements", stats[1]);
			//Reduce location to just the first node
			String location = stats[2];
			int endIndex = location.indexOf("/", 1);
			if (endIndex > -1) {
				String shortLocation = location.substring(0, endIndex);
				status.put("location", shortLocation);
			}
			else {
				status.put("location", "");
			}
			status.put("errors", stats[3]);
		}
		else if (stats[0].equals("ERROR")) {
			status.put("messageType", stats[0]);
			status.put("error", stats[1]);
		}
		else {
			status.put("messageType", "ERROR");
			status.put("error", stats[0]);
		}
		status.put("latestOutput", latestOutput);
		status.put("exitCode", exitCode);
		return status;
	}
	
	public void cancel() {
		if (process != null) {
			process.destroy();
			process = null;
			sig.sendSignal(jobId, 2, false, "CANCELLED");
		}
		
	}

}
