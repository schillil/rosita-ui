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

package com.recomdata.grails.domain

class ConceptMap {
	
	Long id
	String targetTable
	String targetColumn
	String sourceValue
	String sourceVocabulary
	String sourceDesc
	Long targetConceptId
	String mapped
	String empty
	Long sourceCount

	static mapping = {
		table 'cz.cz_concept_map'
		version false
		columns {
			id column: 'concept_map_id'
			mapped column: 'is_mapped'
			empty column: 'is_empty'
		}
	}
	
	static constraints = {
	}	
}
