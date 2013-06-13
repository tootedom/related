package org.greencheek.relatedproduct.util;

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
public interface UTCCurrentDateFormatter {
    /**
     * returns the current day in year, month, day:  yyyy-MM-dd ie. 2013-05-30  (30th May 2013)
     * The date returned is in UTC
     */
    String getCurrentDay();

    /**
     * Parses the current date string, into yyyy-MM-dd ie. 2013-05-30
     * The returned date is in UTC
     */
    String parseToDate(String dateAndOrTime);
}
