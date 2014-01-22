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
public interface UTCCurrentDateAndHourAndMinuteFormatter {
    /**
     * returns the current day in year, month, day and hour:  yyyy-MM-dd'_'HH:mm ie. 2013-05-30_01:30  (30th May 2013 1:30am)
     * The date returned is in UTC
     */
    String getCurrentDayAndHourAndMinute();

    /**
     * Parses the current date string, into a yyyy-MM-dd'T'HH:mm ie. 2013-05-30_01:30
     * The returned date is in UTC
     */
    String parseToDateAndHourAndMinute(String dateAndOrTime);
}
