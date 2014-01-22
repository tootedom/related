package org.greencheek.related.indexing.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.*;

import java.io.IOException;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 17:59
 * To change this template use File | Settings | File Templates.
 */
public class JodaUTCCurrentDateFormatter implements UTCCurrentDateFormatter {

    private final DateTimeParser[] parsers = {
            ISODateTimeFormat.dateTimeParser().getParser(),
            ISODateTimeFormat.basicDateTime().getParser(),
            ISODateTimeFormat.basicDateTimeNoMillis().getParser(),
            ISODateTimeFormat.basicDate().getParser()
    };
    private final DateTimeFormatter formatterUTC = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter().withZoneUTC();
    private final DateTimeFormatter formatter = ISODateTimeFormat.date();

    @Override
    public String getCurrentDay() {
        DateTime dt = new DateTime();
        DateTime utc =dt.withZone(DateTimeZone.UTC);
        StringBuilderWriter b = new StringBuilderWriter(10);
        try {
             formatter.printTo(b, utc);
        } catch (IOException e) {
            // this does not get thrown by the StringBuilder Appendable interface.
        }
        return b.toString();
    }

    @Override
    public String parseToDate(String dateAndOrTime) {
        StringBuilderWriter b = new StringBuilderWriter(10);
        try {
            formatter.printTo(b,formatterUTC.parseDateTime(dateAndOrTime));
        } catch(IOException e) {
            // this does not get thrown by the StringBuilder Appendable interface.
        }
        return b.toString();
    }

    public static void main(String[] args) {
        System.out.println(new JodaUTCCurrentDateFormatter().getCurrentDay());
    }

}
