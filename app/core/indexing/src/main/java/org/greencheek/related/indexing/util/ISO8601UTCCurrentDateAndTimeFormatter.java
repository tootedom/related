package org.greencheek.related.indexing.util;

/**
 *
 */
public interface ISO8601UTCCurrentDateAndTimeFormatter {
    /**
     *
     * Returns the current day in UTC, for example the date returned is in the
     * following format:
     *
     *  yyyy-MM-dd'T'HH:mm:ss.SSSZZ  (millis and timezone)
     *
     * Example :  2013-12-12T10:48:28.606Z
     *
     *
     */
    String getCurrentDay();

    /**
     *
     * Formats a given range of string dates to a UTC time:
     *
     * <ul>
     *  <li>"2008-02-07T09:30:00.000+11:00"  converts to  "2008-02-06T22:30:00.000Z"</li>
     *  <li>"2008-02-07T09:30:00.000+09:00"  converts to  "2008-02-07T00:30:00.000Z"</li>
     * </ul>
     *
     * Other examples include:
     * <pre>
     * formatter.formatToUTC("20080207T093000+0000")          converts to "2008-02-07T09:30:00.000Z"
     * formatter.formatToUTC("2008-02-07T09:30:00")           converts to "2008-02-07T09:30:00.000Z"
     * formatter.formatToUTC("2008-02-07T09:30:00+00:00")     converts to "2008-02-07T09:30:00.000Z"
     * formatter.formatToUTC("2008-02-07T09:30:00.000+00:00") converts to "2008-02-07T09:30:00.000Z"
     * formatter.formatToUTC("2008-02-07T09:30:00+00:00")     converts to "2008-02-07T09:30:00.000Z"
     * formatter.formatToUTC("2008-02-07")                    converts to "2008-02-07T00:00:00.000Z"
     * </pre>
     *
     * @param day a date as a string.
     * @return
     */
    String formatToUTC(String day);
}
