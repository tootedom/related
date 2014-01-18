package org.greencheek.relatedproduct.api.searching;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 14:27
 * To change this template use File | Settings | File Templates.
 */
public enum RelatedProductSearchType {
    FREQUENTLY_RELATED_WITH(0),
    MOST_RECENTLY_RELATED_WITH(1);

    private final int index;

    private RelatedProductSearchType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
