package org.greencheek.relatedproduct.domain;


import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperty;
import org.greencheek.relatedproduct.util.config.Configuration;

import java.util.*;


public class RelatedProduct {

    private final String date;
    private final char[] id;

    private final char[][] relatedProductIds;
    private final RelatedProductAdditionalProperties additionalProperties;


    public RelatedProduct(char[] id,  String date, char[][] relatedPids, RelatedProductAdditionalProperties properties) {
        this.id = id;
        this.date = date;
        this.relatedProductIds = relatedPids;
        this.additionalProperties = properties;
    }

    public String getDate() {
        return date;
    }

    public char[] getId() {
        return id;
    }

    public char[][] getRelatedProductPids() {
        return relatedProductIds;
    }

    public RelatedProductAdditionalProperties getAdditionalProperties() {
        return additionalProperties;
    }


}
