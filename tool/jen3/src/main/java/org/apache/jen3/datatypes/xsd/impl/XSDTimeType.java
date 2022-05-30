/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jen3.datatypes.xsd.impl;

import org.apache.jen3.datatypes.xsd.AbstractDateTime;
import org.apache.jen3.datatypes.xsd.XSDDateTime;

/**
 * Type processor for time, most of the machinery is in the base
 * XSDAbstractDateTimeType class.
 */
public class XSDTimeType extends XSDAbstractDateTimeType {

	/**
	 * Constructor
	 */
	public XSDTimeType(String typename) {
		super(typename);
	}

	/**
	 * Parse a validated date. This is invoked from
	 * XSDDatatype.convertValidatedDataValue rather then from a local parse method
	 * to make the implementation of XSDGenericType easier.
	 */
	@Override
	public Object parseValidated(String str) {
		int len = str.length();
		int[] date = new int[TOTAL_SIZE];
		int[] timeZone = new int[2];

		// time
		// initialize to default values
		date[CY] = YEAR;
		date[M] = MONTH;
		date[D] = DAY;
		getTime(str, 0, len, date, timeZone);

		if (date[utc] != 0 && date[utc] != 'Z') {
			AbstractDateTime.normalize(date, timeZone);
		}

		return new XSDDateTime(date, TIME_MASK);
	}

	/**
	 * Convert a value of this datatype out to lexical form.
	 */
	@Override
	public String unparse(Object value) {
		if (value instanceof XSDDateTime) {
			return ((XSDDateTime) value).timeLexicalForm();
		} else {
			// Don't think we should ever get here
			return value.toString();
		}
	}

}
