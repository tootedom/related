package org.greencheek.related.api.indexing;


import org.greencheek.related.api.RelatedItemAdditionalProperties;


public class RelatedItem {

    private final String date;
    private final char[] id;

    private final char[][] relatedItemIds;
    private final RelatedItemAdditionalProperties additionalProperties;


    public RelatedItem(char[] id, String date, char[][] relatedPids, RelatedItemAdditionalProperties properties) {
        this.id = id;
        this.date = date;
        this.relatedItemIds = relatedPids;
        this.additionalProperties = properties;
    }

    public String getDate() {
        return date;
    }

    public char[] getId() {
        return id;
    }

    public char[][] getRelatedItemPids() {
        return relatedItemIds;
    }

    public RelatedItemAdditionalProperties getAdditionalProperties() {
        return additionalProperties;
    }


}
