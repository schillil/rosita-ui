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

package com.recomdata.grails.rositaui.etl.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.recomdata.grails.rositaui.etl.ValidationError;

public class ValidationErrorBatchPreparedStatementSetter extends EtlBatchPreparedStatementSetter<ValidationError> implements BatchPreparedStatementSetter {

	public ValidationErrorBatchPreparedStatementSetter(Integer threshold) {
		super(threshold);
	}

	@Override
	public void setValues(PreparedStatement ps, int i) throws SQLException {
		ValidationError v = items.get(i);
		ps.setString(1, v.getType());
		ps.setLong(2, v.getLineNumber());
		ps.setString(3, v.getMessage());
		ps.setTimestamp(4, new Timestamp(v.getDate().getTime()));
		ps.setString(5, v.getSchema());
		ps.setString(6, v.getLocation());
		ps.setString(7, v.getSourceType());
		ps.setString(8, v.getFilename());
	}
	
	@Override
	public int getBatchSize() {
		return items.size();
	}

}
