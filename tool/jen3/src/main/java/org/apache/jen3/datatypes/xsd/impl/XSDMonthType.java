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
 * Type processor for gMonth, most of the machinery is in the base
 * XSDAbstractDateTimeType class.
 */
public class XSDMonthType extends XSDAbstractDateTimeType {

	/**
	 * Constructor
	 */
	public XSDMonthType(String typename) {
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

		// set constants
		date[CY] = YEAR;
		date[D] = DAY;
		int stop = 4;
		date[M] = parseInt(str, 2, stop);

		// REVISIT: allow both --MM and --MM-- now.
		// need to remove the following 4 lines to disallow --MM--
		// when the errata is offically in the rec.
		if (str.length() >= stop + 2 && str.charAt(stop) == '-' && str.charAt(stop + 1) == '-') {
			stop += 2;
		}
		if (stop < len) {
			int sign = findUTCSign(str, stop, len);
			getTimeZone(str, date, sign, len, timeZone);
		}

		if (date[utc] != 0 && date[utc] != 'Z') {
			AbstractDateTime.normalize(date, timeZone);
		}

		return new XSDDateTime(date, MONTH_MASK);
	}

}
