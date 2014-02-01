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

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 22/06/2013
 * Time: 13:32
 * To change this template use File | Settings | File Templates.
 */
public class JodaUTCCurrentDateAndHourFormatterTest {


    private final JodaUTCCurrentDateAndHourFormatter formatter = new JodaUTCCurrentDateAndHourFormatter();


    @Test
    public void testCurrentTimeDayAndHour() {
        String before = getNow();
        String s = formatter.getCurrentDayAndHour();
        String after = getNow();
        checkEquals(s,before,after);
    }

    @Test
    public void testDateHasCurrentHour() {

        String s = formatter.parseToDateAndHour("2008-02-07");

        assertEquals("2008-02-07_00", s);
    }


    @Test
    public void testDateIsOneDayBehind() {
        String s = formatter.parseToDateAndHour("2008-02-07T09:30:00.000+11:00");
        assertEquals("2008-02-06_22",s);
    }

    @Test
    public void testTimeIsAdapted() {
        String s = formatter.parseToDateAndHour("2008-02-07T09:30:00+09:00");
        assertEquals("2008-02-07_00",s);
    }

    @Test
    public void testTimeWithMillisIsAdapted() {
        String s = formatter.parseToDateAndHour("2008-02-07T09:30:00.000+09:00");
        assertEquals("2008-02-07_00",s);
    }

    @Test
    public void testTimeWithNoSeparatorsIsParsed() {
        String s = formatter.parseToDateAndHour("20080207T093000+0900");
        assertEquals("2008-02-07_00",s);
    }


    private void checkEquals(String value, String before, String after) {
        assertTrue(value.equals(before) ? true : value.equals(after));
    }


    private String getNow() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Date now = c.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'_'HH");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(now);
    }

    private String getHour() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Date now = c.getTime();
        SimpleDateFormat format = new SimpleDateFormat("HH");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(now);

    }

}
