package org.greencheek.relatedproduct.indexing.util;

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
public interface ISO8601UTCCurrentDateAndTimeFormatter {
    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSZZ  (millis and timezone)
     */
    String getCurrentDay();


    String formatToUTC(String day);
}
