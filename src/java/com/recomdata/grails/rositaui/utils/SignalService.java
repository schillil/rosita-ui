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

package com.recomdata.grails.rositaui.utils;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class SignalService implements InitializingBean {
	
	String dbUrl;
	String dbUsername;
	String dbPassword;
	DataSource ds;
	PreparedStatement ps = null;
	PreparedStatement consolePs = null;
	
	static SignalService me;
	
	public String getDbUrl() {
		return dbUrl;
	}
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	public String getDbUsername() {
		return dbUsername;
	}
	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	
	public SignalService() {}
	
	public static SignalService getInstance() {
		return me;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		ds = new DriverManagerDataSource(dbUrl, dbUsername, dbPassword);
		ps = ds.getConnection().prepareStatement("INSERT INTO cz.cz_workflow_signal(job_id, workflow_step, date, pending, success, message) VALUES (?, ?, ?, ?, ?, ?);");
		consolePs = ds.getConnection().prepareStatement("INSERT INTO cz.cz_console_output(step_id, date, message) VALUES (?,?,?)");
		me = this;
	}
	
	public void sendSignal(Long jobId, Integer workflowStep, boolean success) {
		sendSignal(jobId, workflowStep, success, "");
	}
		
	public void sendSignal(Long jobId, Integer workflowStep, boolean success, String message) {
		try {
			ps.setLong(1, jobId);
			ps.setLong(2, workflowStep);
			ps.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
			ps.setBoolean(4, true);
			ps.setBoolean(5, success);
			ps.setString(6, message);
			ps.execute();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void sendConsole(Long stepId, String message) {
		try {
			consolePs.setLong(1, stepId);
			consolePs.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
			consolePs.setString(3, message);
			consolePs.execute();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
