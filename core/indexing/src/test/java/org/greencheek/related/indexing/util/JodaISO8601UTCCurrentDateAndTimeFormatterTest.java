package org.greencheek.related.indexing.util;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 12/12/2013
 * Time: 10:29
 * To change this template use File | Settings | File Templates.
 */
public class JodaISO8601UTCCurrentDateAndTimeFormatterTest {



    ISO8601UTCCurrentDateAndTimeFormatter formatter = new JodaISO8601UTCCurrentDateAndTimeFormatter();



    @Test
    public void testCurrentDayReturned() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:");
        Date d = new Date();
          System.out.println(formatter.getCurrentDay());
        assertTrue(formatter.getCurrentDay().startsWith(f.format(d)));
    }

    @Test
    public void testParseDatesToUTCTimeGoesBackADay() {
        assertEquals("2008-02-06T22:30:00.000Z",formatter.formatToUTC("2008-02-07T09:30:00.000+11:00"));
    }

    @Test
    public void testParseDatesToUTCTimeGoesBackInCurrentDay() {
        assertEquals("2008-02-07T00:30:00.000Z",formatter.formatToUTC("2008-02-07T09:30:00.000+09:00"));
    }

    @Test
    public void testParseDateToUTCTimeGoesBackInCurrentDayWithNoMillis() {
        assertEquals("2008-02-07T00:30:00.000Z",formatter.formatToUTC("2008-02-07T09:30:00+09:00"));
    }

    @Test
    public void testParseDateToUTCTimeGoesBackInCurrentDayWithNoSeparators() {
        assertEquals("2008-02-07T00:30:00.000Z",formatter.formatToUTC("20080207T093000+0900"));
    }

    @Test
    public void testParseDateToUTCTimeWithNoTimeZoneIsTakenAsUTC() {
        assertEquals("2008-02-07T09:30:00.000Z",formatter.formatToUTC("20080207T093000+0000"));

        assertEquals("2008-02-07T09:30:00.000Z",formatter.formatToUTC("2008-02-07T09:30:00"));

        assertEquals("2008-02-07T09:30:00.000Z",formatter.formatToUTC("2008-02-07T09:30:00+00:00"));

        assertEquals("2008-02-07T09:30:00.000Z",formatter.formatToUTC("2008-02-07T09:30:00.000+00:00"));

        assertEquals("2008-02-07T09:30:00.000Z",formatter.formatToUTC("2008-02-07T09:30:00+00:00"));
    }

    @Test
    public void testParseDateToUTCWithNoTimeInformation() {
        assertEquals("2008-02-07T00:00:00.000Z",formatter.formatToUTC("2008-02-07"));
    }
}
