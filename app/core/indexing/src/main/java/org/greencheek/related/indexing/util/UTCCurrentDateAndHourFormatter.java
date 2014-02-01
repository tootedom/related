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
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 17:58
 * To change this template use File | Settings | File Templates.
 */

/**
 * Return the current day in UTC
 */
public interface UTCCurrentDateAndHourFormatter {
    /**
     * returns the current day in year, month, day and hour:  yyyy-MM-dd'_'HH ie. 2013-05-30_01  (30th May 2013 1am)
     * The date returned is in UTC
     */
    String getCurrentDayAndHour();

    /**
     * Parses the current date string, into a yyyy-MM-dd'_'HH ie. 2013-05-30_01
     * The returned date is in UTC
     */
    String parseToDateAndHour(String dateAndOrTime);
}
