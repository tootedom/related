package org.greencheek.relatedproduct.domain;


import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperty;
import org.greencheek.relatedproduct.util.config.Configuration;

import java.util.*;


public class RelatedProduct {

    private final String date;
    private final char[] id;

    private final char[][] relatedProductIds;
    private final RelatedProductAdditionalProperty[] additionalProperties;


    public RelatedProduct(char[] id,  String date, char[][] relatedPids, RelatedProductAdditionalProperty[] properties) {
        this.id = id;
        this.date = date;
        this.relatedProductIds = relatedPids;
        this.additionalProperties = new RelatedProductAdditionalProperty[properties.length];
        for(int i=0;i<properties.length;i++) {
           this.additionalProperties[i] = properties[i].trimAndClone();
        }
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

    public RelatedProductAdditionalProperty[] getAdditionalProperties() {
        return additionalProperties;
    }


}
