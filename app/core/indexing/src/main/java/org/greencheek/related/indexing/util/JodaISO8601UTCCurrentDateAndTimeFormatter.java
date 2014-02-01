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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.*;


import java.io.IOException;


/**
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
 */
public class JodaISO8601UTCCurrentDateAndTimeFormatter implements ISO8601UTCCurrentDateAndTimeFormatter {
    private final DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
    private final DateTimeFormatter formatterUTCPrinter = ISODateTimeFormat.dateTime().withZoneUTC();

    private final DateTimeParser[] parsers = {
            ISODateTimeFormat.dateTimeParser().getParser(),
            ISODateTimeFormat.basicDateTime().getParser(),
            ISODateTimeFormat.basicDateTimeNoMillis().getParser(),
            ISODateTimeFormat.basicDate().getParser()
            };

    private final DateTimeFormatter formatterUTC = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter().withZoneUTC();


    @Override
    public String getCurrentDay() {

        DateTime dt = new DateTime();
        DateTime utc =dt.withZone(DateTimeZone.UTC);
        StringBuilderWriter b = new StringBuilderWriter(24);
        try {
            formatter.printTo(b,utc);
        } catch (IOException e) {
            // this does not get thrown by the StringBuilder Appendable interface.
        }
        return b.toString();
    }

    @Override
    public String formatToUTC(String day) {
        StringBuilderWriter b = new StringBuilderWriter(24);
        try {
            formatterUTCPrinter.printTo(b,formatterUTC.parseDateTime(day));
        } catch (IOException e) {
            // this does not get thrown by the StringBuilder Appendable interface.
        }
        return b.toString();
    }
}
