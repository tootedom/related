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
 *
 */
public interface ISO8601UTCCurrentDateAndTimeFormatter {
    /**
     *
     * Returns the current day in UTC, for example the date returned is in the
     * following format:
     *
     *  yyyy-MM-dd'T'HH:mm:ss.SSSZZ  (millis and timezone)
     *
     * Example :  2013-12-12T10:48:28.606Z
     *
     *
     */
    String getCurrentDay();

    /**
     *
     * Formats a given range of string dates to a UTC time:
     *
     * <ul>
     *  <li>"2008-02-07T09:30:00.000+11:00"  converts to  "2008-02-06T22:30:00.000Z"</li>
     *  <li>"2008-02-07T09:30:00.000+09:00"  converts to  "2008-02-07T00:30:00.000Z"</li>
     * </ul>
     *
     * Other examples include:
     * <pre>
     * formatter.formatToUTC("20080207T093000+0000")          converts to "2008-02-07T09:30:00.000Z"
     * formatter.formatToUTC("2008-02-07T09:30:00")           converts to "2008-02-07T09:30:00.000Z"
     * formatter.formatToUTC("2008-02-07T09:30:00+00:00")     converts to "2008-02-07T09:30:00.000Z"
     * formatter.formatToUTC("2008-02-07T09:30:00.000+00:00") converts to "2008-02-07T09:30:00.000Z"
     * formatter.formatToUTC("2008-02-07T09:30:00+00:00")     converts to "2008-02-07T09:30:00.000Z"
     * formatter.formatToUTC("2008-02-07")                    converts to "2008-02-07T00:00:00.000Z"
     * </pre>
     *
     * @param day a date as a string.
     * @return
     */
    String formatToUTC(String day);
}
