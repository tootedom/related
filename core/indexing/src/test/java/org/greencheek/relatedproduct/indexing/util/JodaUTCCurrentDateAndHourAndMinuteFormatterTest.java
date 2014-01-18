package org.greencheek.relatedproduct.indexing.util;

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
public class JodaUTCCurrentDateAndHourAndMinuteFormatterTest {


    private final JodaUTCCurrentDateAndHourAndMinuteFormatter formatter = new JodaUTCCurrentDateAndHourAndMinuteFormatter();


    @Test
    public void testCurrentTimeDayAndHour() {
        String before = getNow();
        String s = formatter.getCurrentDayAndHourAndMinute();
        String after = getNow();
        checkEquals(s,before,after);
    }

    @Test
    public void testDateHasCurrentHour() {

        String s = formatter.parseToDateAndHourAndMinute("2008-02-07");

        assertEquals("2008-02-07_00:00", s);
    }


    @Test
    public void testDateIsOneDayBehind() {
        String s = formatter.parseToDateAndHourAndMinute("2008-02-07T09:30:00.000+11:00");
        assertEquals("2008-02-06_22:30",s);
    }

    @Test
    public void testTimeIsAdapted() {
        String s = formatter.parseToDateAndHourAndMinute("2008-02-07T09:30:00+09:00");
        assertEquals("2008-02-07_00:30",s);
    }

    @Test
    public void testTimeWithMillisIsAdapted() {
        String s = formatter.parseToDateAndHourAndMinute("2008-02-07T09:30:00.000+09:00");
        assertEquals("2008-02-07_00:30",s);
    }

    @Test
    public void testTimeWithNoSeparatorsIsParsed() {
        String s = formatter.parseToDateAndHourAndMinute("20080207T093000+0900");
        assertEquals("2008-02-07_00:30",s);
    }


    private void checkEquals(String value, String before, String after) {
        assertTrue(value.equals(before) ? true : value.equals(after));
    }


    private String getNow() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Date now = c.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'_'HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(now);
    }


}
