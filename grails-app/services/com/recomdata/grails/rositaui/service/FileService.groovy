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

import org.codehaus.groovy.grails.commons.ConfigurationHolder;

import com.recomdata.grails.rositaui.utils.GenericFilenameFilter;

class FileService {

    static transactional = true

    def getBrowseFolder() {
		
		String folder = ConfigurationHolder.config.rosita.browse.folder ?: null
		if (!folder.endsWith("/")) {
			folder += "/"
		}
		
		return folder
		
    }
	
	def getFiles(String extension) {
		
		GenericFilenameFilter filter = new GenericFilenameFilter(extension)
		String folderName = ConfigurationHolder.config.rosita.browse.folder ?: null
		if (!folderName) {
			return null;
		}
		
		File folder = new File(folderName);
		if (!folder.isDirectory()) {
			return null
		}
		
		def filenames = []
		File[] files = folder.listFiles(filter);
		for (file in files) {
			filenames.add(file.getName())
		}
		
		return filenames
	}

    def getFlatFileDirs() {

		Properties props = new Properties()
		props.load(new FileInputStream(ConfigurationHolder.config.rosita.jar.path + "/rosita.properties"))
		GenericFilenameFilter filter = new GenericFilenameFilter(props.file_suffix)
        def browseFolder = getBrowseFolder()

        File folder = new File(browseFolder)
        if (!folder.isDirectory()) {
            return null
        }

        def dirnames = []
        File[] files = folder.listFiles()
        for (dir in files) {
            if (dir.isDirectory()) {
                def bFound = false
//                System.out.println("dir name = ${dir.getCanonicalPath()}")
                File[] dirfiles = dir.listFiles(filter)
                if (dirfiles != null && dirfiles.size() != 0) {
                    dirnames.add(dir.getName())
                }
            }
        }
        return dirnames
    }
}
