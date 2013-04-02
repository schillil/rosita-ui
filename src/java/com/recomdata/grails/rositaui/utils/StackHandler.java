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

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StackHandler extends DefaultHandler {
	
	private static StackHandler me = null;
	
	public static StackHandler getInstance() {
		if (me == null) {
			me = new StackHandler();
		}
		return me;
	}
		
	private Long elementsValidated = 0L;
	
	private StackHandler() {}
	
	private XPathStackHandler xpathStack = new XPathStackHandler();
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		xpathStack.push(localName);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		xpathStack.pop();
		elementsValidated++;
		if (Thread.interrupted()) {
			throw new SAXException("Thread interrupted!");
		}
	}
	
	public String getCurrentLocation() {
		return xpathStack.getXPath();
	}
	
	public List<XPathStackEntry> getStack() {
		return xpathStack.getStack();
	}
	
	public String getObject() {
		return xpathStack.getLatestObject();
	}
	
	public Long getElementsValidated() {
		return elementsValidated;
	}
	
	public void reset() {
		this.elementsValidated = 0L;
		this.xpathStack = new XPathStackHandler();
	}

}
