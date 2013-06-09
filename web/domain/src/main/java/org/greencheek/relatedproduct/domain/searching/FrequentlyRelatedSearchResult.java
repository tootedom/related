package org.greencheek.relatedproduct.domain.searching;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 08/06/2013
 * Time: 16:13
 * To change this template use File | Settings | File Templates.
 */
public class FrequentlyRelatedSearchResult {

    private final long frequency;
    private final String relatedProductId;

    public FrequentlyRelatedSearchResult(String id, long frequency) {
        this.frequency = frequency;
        this.relatedProductId = id;
    }

    public String getRelatedProductId() {
        return relatedProductId;
    }


    public long getFrequency() {
        return frequency;
    }



    public String toString() {
        StringBuilder b = new StringBuilder(44);
        b.append(frequency).append(':').append(relatedProductId);
        return b.toString();
    }
}
