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
public interface UTCCurrentDayFormatter {
    /**
     * returns the current day in year, month, day:  YYYY-MM-DD ie. 2013-05-31  (31st May 2013)
     */
    String getCurrentDay();
}
