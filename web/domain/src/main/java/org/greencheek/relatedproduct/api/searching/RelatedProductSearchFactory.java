package org.greencheek.relatedproduct.api.searching;

import org.greencheek.relatedproduct.util.config.Configuration;

import javax.inject.Inject;
import javax.inject.Named;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 14:19
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductSearchFactory {


    public static RelatedProductSearch createSearchObject(Configuration configuration) {
        RelatedProductSearch ob = new RelatedProductSearch(configuration);
        ob.setByteBuffer(ByteBuffer.allocate(ob.size()),0);
        return ob;
    }

    public static RelatedProductSearch createAndPopulateSearchObject(Configuration configuration, RelatedProductSearchType type, Map<String, String> properties) {
        RelatedProductSearch ob = new RelatedProductSearch(configuration);
        ob.setByteBuffer(ByteBuffer.allocate(ob.size()),0);

        String sizeKey = configuration.getRequestParameterForSize();
        String idKey = configuration.getRequestParameterForId();
        try {
            ob.maxResults.set(Integer.parseInt(properties.get(sizeKey)));
        } catch(NumberFormatException e) {
            ob.maxResults.set(configuration.getDefaultNumberOfResults());
        }

        ob.relatedContentId.set(properties.get(idKey));

        int maxPropertiesToCopy = Math.min(ob.additionalSearchCriteria.numberOfProperties.get(),properties.size());

        short i=0;
        for(Map.Entry<String,String> entry : properties.entrySet()) {
            String key = entry.getKey();
            if(key.equals(sizeKey) || key.equals(idKey)) continue;

            if(i==maxPropertiesToCopy-1) break;
            ob.additionalSearchCriteria.additionalProperties[i].name.set(key);
            ob.additionalSearchCriteria.additionalProperties[i].value.set(entry.getValue());
            i++;
        }

        ob.additionalSearchCriteria.numberOfProperties.set(i);

        ob.searchType.set(type);

        return ob;
    }
}
