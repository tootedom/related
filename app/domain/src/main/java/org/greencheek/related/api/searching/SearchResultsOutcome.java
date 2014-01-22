package org.greencheek.related.api.searching;

/**
 * Enum to represent the success or otherwise
 * of the search.
 */
public enum SearchResultsOutcome {
    HAS_RESULTS(0),
    REQUEST_TIMEOUT(1),
    EMPTY_RESULTS(2),
    FAILED_REQUEST(3),
    MISSING_SEARCH_RESULTS_HANDLER(4);

    private final int ordinalIndex;

    private SearchResultsOutcome(int code) {
        this.ordinalIndex = code;
    }

    public int getIndex() {
        return ordinalIndex;
    }
}
