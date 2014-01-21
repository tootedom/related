package org.greencheek.related.indexing.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.*;

import javax.inject.Named;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 17:59
 * To change this template use File | Settings | File Templates.
 */
@Named
public class JodaUTCCurrentDateAndHourFormatter implements UTCCurrentDateAndHourFormatter {

    private final DateTimeParser[] parsers = {
            ISODateTimeFormat.dateTimeParser().getParser(),
            ISODateTimeFormat.basicDateTime().getParser(),
            ISODateTimeFormat.basicDateTimeNoMillis().getParser(),
            ISODateTimeFormat.basicDate().getParser()
    };
    private final DateTimeFormatter formatterUTC = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter().withZoneUTC();
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'_'HH");

    @Override
    public String getCurrentDayAndHour() {
        DateTime dt = new DateTime();
        DateTime utc =dt.withZone(DateTimeZone.UTC);
        return formatter.print(utc);
    }

    @Override
    public String parseToDateAndHour(String dateAndOrTime) {
        return formatter.print(formatterUTC.parseDateTime(dateAndOrTime));
    }

}
