package org.greencheek.relatedproduct.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.inject.Named;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 17:59
 * To change this template use File | Settings | File Templates.
 */
@Named
public class JodaUTCCurrentDayFormatter implements UTCCurrentDayFormatter {

    DateTimeFormatter formatter = ISODateTimeFormat.date();
    @Override
    public String getCurrentDay() {
        DateTime dt = new DateTime();
        DateTime utc =dt.withZone(DateTimeZone.UTC);
        return formatter.print(utc);
    }
}
