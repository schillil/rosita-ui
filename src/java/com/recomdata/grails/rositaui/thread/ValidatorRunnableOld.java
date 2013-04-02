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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.recomdata.grails.rositaui.utils.RositaValidationErrorHandler;
import com.recomdata.grails.rositaui.utils.StackHandler;
import com.recomdata.grails.rositaui.utils.Stopwatch;
import com.recomdata.grails.rositaui.utils.ValidationErrorCache;

public class ValidatorRunnableOld implements Runnable {

	DataSource ds;
	Integer threshold = 5000;
	boolean saveErrors = false;
	ValidationErrorCache cache = null;
	String filename = "";
	String schema = "";
	Long maxErrors = 0L;
	RositaValidationErrorHandler errorHandler = null;
	Stopwatch stopwatch = null;
	
	public ValidatorRunnableOld() {
		
	}

	@Override
	public void run() {
		try {
			this.cache = new ValidationErrorCache(this.ds, this.threshold);
			File xmlFile = new File(filename);
			if (!xmlFile.exists()) {
				throw new FileNotFoundException("XML file does not exist: " + filename);
			}
			File schemaFile = new File(schema);
			if (!schemaFile.exists()) {
				throw new FileNotFoundException("Schema file does not exist: " + schema);
			}
			this.errorHandler = new RositaValidationErrorHandler(schemaFile.getName(), xmlFile.getName(), maxErrors, cache, saveErrors);
			StackHandler.getInstance().reset();
			
			stopwatch = new Stopwatch();
			System.out.println("Validating with SAX...");
			stopwatch.start();
			SAXParserFactory saxFactory = SAXParserFactory.newInstance();
			saxFactory.setValidating(true);
			saxFactory.setNamespaceAware(true);
			saxFactory.setSchema(SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema").newSchema(schemaFile));
	
			SAXParser parser = saxFactory.newSAXParser();
	
			XMLReader saxReader = parser.getXMLReader();
			saxReader.setErrorHandler(this.errorHandler);
			try {
				saxReader.setContentHandler(StackHandler.getInstance());
				saxReader.parse(new InputSource(new FileReader(xmlFile)));
			}
			catch (SAXException e) {
				System.out.println(e.getMessage());
			}
			
			stopwatch.stop();
			System.out.println("Validation took " + stopwatch.getElapsedTimeSecs() + "s. Errors: " + errorHandler.getErrorCount() + ", warnings: " + errorHandler.getWarningCount());
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}

	public void setSaveErrors(boolean saveErrors) {
		this.saveErrors = saveErrors;
	}

	public void setCache(ValidationErrorCache cache) {
		this.cache = cache;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setMaxErrors(Long maxErrors) {
		this.maxErrors = maxErrors;
	}
	
	public void setDataSource(DataSource ds) {
		this.ds = ds;
	}
	
	public Map<String, Object> getValidationStatus() {
		Map<String, Object> status = new HashMap<String, Object>();
		status.put("filename", filename);
		status.put("schemaname", schema);
		status.put("location", StackHandler.getInstance().getCurrentLocation());
		status.put("elementsValidated", StackHandler.getInstance().getElementsValidated());
		status.put("errors", errorHandler.getErrorCount());
		status.put("lastErrors", errorHandler.getLastErrors());
		status.put("timeStarted", stopwatch.getStartTime());
		status.put("timeElapsed", stopwatch.getElapsedTime());
		return status;
	}

}
