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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.recomdata.grails.rositaui.etl.ValidationError;

public class RositaValidationErrorHandler implements ErrorHandler {
	
	Long maxErrors = null;
	Long errors = 0L;
	Long warnings = 0L;
	boolean saveErrors = false;
	String schemaName = "";
	String filename = "";
	ValidationErrorCache cache = null;
	
	List<ValidationError> lastErrors = new ArrayList<ValidationError>();
	
	public RositaValidationErrorHandler(String schemaName, String filename, Long maxErrors, ValidationErrorCache cache, boolean saveErrors) {
		this.schemaName = schemaName;
		this.filename = filename;
		this.cache = cache;
		if (maxErrors > 0) {
			this.maxErrors = maxErrors;
		}
		this.saveErrors = saveErrors;
	}
	
	@Override
	public void warning(SAXParseException exception) throws SAXException {
		warnings++;
		ValidationError ve = getValidationError("WARNING", exception);
		if (saveErrors) {
			cache.add(ve);
		}
		else {
			System.out.println(ve);
		}
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		errors++;
		ValidationError ve = getValidationError("FATAL", exception);
		if (saveErrors) {
			cache.add(ve);
		}
		else {
			System.out.println(ve);
		}
		
		
		if (maxErrors != null && errors > maxErrors) {
			if (saveErrors) {
				cache.saveAll();
			}
			throw new SAXException("Too many errors - " + maxErrors);
		}
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		errors++;
		ValidationError ve = getValidationError("ERROR", exception);
		if (saveErrors) {
			cache.add(ve);
		}
		else {
			System.out.println(ve);
		}
		
		if (maxErrors != null && errors > maxErrors) {
			if (saveErrors) {
				cache.saveAll();
			}
			throw new SAXException("Too many errors - " + maxErrors);
		}
	}
	
	public Long getErrorCount() {
		return errors;
	}
	
	public Long getWarningCount() {
		return warnings;
	}
	
	public ValidationError[] getLastErrors() {
		return lastErrors.toArray(new ValidationError[0]);
	}
	
	private ValidationError getValidationError(String type, SAXParseException e) {
		StackHandler sh = StackHandler.getInstance();
		String location = sh.getCurrentLocation();
		
		ValidationError error = new ValidationError(type,
				new Long(e.getLineNumber()),
						 e.getMessage(),
						 new Date(),
						 schemaName,
						 location,
						 sh.getObject(),
						 filename);
		lastErrors.add(0, error);
		if (lastErrors.size() > 5) {
			lastErrors.remove(5);
		}
		return error;
	}
}
