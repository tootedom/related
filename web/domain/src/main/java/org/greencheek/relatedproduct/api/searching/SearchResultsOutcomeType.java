package org.greencheek.relatedproduct.api.searching;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 30/06/2013
 * Time: 21:40
 * To change this template use File | Settings | File Templates.
 */
public enum SearchResultsOutcomeType {
    HAS_RESULTS(0),
    REQUEST_TIMEOUT(1),
    EMPTY_RESULTS(2),
    FAILED_REQUEST(3);

    private final int ordinalIndex;

    private SearchResultsOutcomeType(int code) {
        this.ordinalIndex = code;
    }

    public int getIndex() {
        return ordinalIndex;
    }
}
