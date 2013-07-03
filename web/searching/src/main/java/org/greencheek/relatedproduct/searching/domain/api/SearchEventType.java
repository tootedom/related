package org.greencheek.relatedproduct.searching.domain.api;

public enum SearchEventType
{
    SEARCH_REQUEST(0),
    SEARCH_RESULT(1);

    private final int index;
    private SearchEventType(int index)
    {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}