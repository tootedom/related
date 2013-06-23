package org.greencheek.relatedproduct.domain;


import org.greencheek.relatedproduct.util.config.Configuration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class RelatedProduct {

    private final String id;
    private final String date;

    private final String[] relatedProductIds;
    private final Map<String,String> additionalProperties;


    public RelatedProduct(String id, String date, Set<String> relatedPids, Map<String, String> properties, Configuration config) {
        this.id = id;
        this.date = date;
        this.relatedProductIds = relatedPids.toArray(new String[config.getMaxNumberOfRelatedProductsPerPurchase()]);
        additionalProperties = new HashMap<String,String>(properties);

    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }


    public String[] getRelatedProductPids() {
        return relatedProductIds;
    }

    public Map<String,String> getAdditionalProperties() {
        return additionalProperties;
    }


}
