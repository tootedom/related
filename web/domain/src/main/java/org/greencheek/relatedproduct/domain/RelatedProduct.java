package org.greencheek.relatedproduct.domain;


import org.greencheek.relatedproduct.util.config.Configuration;

import java.util.*;


public class RelatedProduct {

    private final String date;
    private final String id;

    private final String[] relatedProductIds;
    private final String[][] additionalProperties;


    public RelatedProduct(String id,  String date, String[] relatedPids, String[][] properties) {
        this.id = id;
        this.date = date;
        this.relatedProductIds = Arrays.copyOf(relatedPids, relatedPids.length);
        additionalProperties = properties;

    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public String[] getRelatedProductPids() {
        return relatedProductIds;
    }

    public String[][] getAdditionalProperties() {
        return additionalProperties;
    }


}
