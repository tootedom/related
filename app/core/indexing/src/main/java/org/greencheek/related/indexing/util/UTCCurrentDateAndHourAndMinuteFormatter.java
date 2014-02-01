/*
 *
 *  * Licensed to Relateit under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Relateit licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.greencheek.related.indexing.util;


/**
 * Return the current day in UTC
 */
public interface UTCCurrentDateAndHourAndMinuteFormatter {
    /**
     * returns the current day in year, month, day and hour:  yyyy-MM-dd'_'HH:mm ie. 2013-05-30_01:30  (30th May 2013 1:30am)
     * The date returned is in UTC
     */
    String getCurrentDayAndHourAndMinute();

    /**
     * Parses the current date string, into a yyyy-MM-dd'T'HH:mm ie. 2013-05-30_01:30
     * The returned date is in UTC
     */
    String parseToDateAndHourAndMinute(String dateAndOrTime);
}
