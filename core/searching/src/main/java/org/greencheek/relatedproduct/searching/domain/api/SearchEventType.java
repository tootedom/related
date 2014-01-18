package org.greencheek.relatedproduct.searching.domain.api;

/**
 *
 */
public enum SearchEventType {
    REQUEST(0),
    RESPONSE(1);

    private final int index;

    private SearchEventType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}