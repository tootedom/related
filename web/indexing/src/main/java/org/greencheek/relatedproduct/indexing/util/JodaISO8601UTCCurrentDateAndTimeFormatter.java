package org.greencheek.relatedproduct.indexing.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.*;


import java.io.IOException;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 11/06/2013
 * Time: 22:48
 * To change this template use File | Settings | File Templates.
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

    public static void main(String[] args) {
        System.out.println("2008-02-07'T'09:30:00.000 +11:00");
        System.out.println(new JodaISO8601UTCCurrentDateAndTimeFormatter().formatToUTC("2008-02-07T09:30:00.000+11:00"));
        System.out.println(new JodaISO8601UTCCurrentDateAndTimeFormatter().formatToUTC("2008-02-07T09:30:00.000+09:00"));
        System.out.println(new JodaISO8601UTCCurrentDateAndTimeFormatter().formatToUTC("2008-02-07T09:30:00+09:00"));
        System.out.println(new JodaISO8601UTCCurrentDateAndTimeFormatter().formatToUTC("20080207T093000+0900"));
        System.out.println(new JodaISO8601UTCCurrentDateAndTimeFormatter().formatToUTC("2008-02-07T09:30:00"));
        System.out.println(new JodaISO8601UTCCurrentDateAndTimeFormatter().formatToUTC("2008-02-07"));

        System.out.println(new JodaISO8601UTCCurrentDateAndTimeFormatter().getCurrentDay());
    }





}
