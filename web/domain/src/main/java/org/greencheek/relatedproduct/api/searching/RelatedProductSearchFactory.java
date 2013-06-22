package org.greencheek.relatedproduct.api.searching;

import org.greencheek.relatedproduct.util.config.Configuration;

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

        populateSearchObject(configuration,ob,type,properties);

        return ob;
    }


    public static void populateSearchObject(Configuration configuration,RelatedProductSearch objectToPopulate,
                                                            RelatedProductSearchType type,
                                                            Map<String, String> properties) {
        objectToPopulate.validMessage.set(false);

        String sizeKey = configuration.getRequestParameterForSize();
        String idKey = configuration.getRequestParameterForId();
        try {
            objectToPopulate.maxResults.set(Integer.parseInt(properties.get(sizeKey)));
        } catch(NumberFormatException e) {
            objectToPopulate.maxResults.set(configuration.getDefaultNumberOfResults());
        }

        objectToPopulate.relatedContentId.set(properties.get(idKey));

        int maxPropertiesToCopy = Math.min(objectToPopulate.additionalSearchCriteria.numberOfProperties.get(),properties.size());

        short i=0;
        for(Map.Entry<String,String> entry : properties.entrySet()) {
            String key = entry.getKey();
            if(key.equals(sizeKey) || key.equals(idKey)) continue;

            if(i==maxPropertiesToCopy-1) break;
            objectToPopulate.additionalSearchCriteria.additionalProperties[i].name.set(key);
            objectToPopulate.additionalSearchCriteria.additionalProperties[i].value.set(entry.getValue());
            i++;
        }

        objectToPopulate.additionalSearchCriteria.numberOfProperties.set(i);

        objectToPopulate.searchType.set(type);

        objectToPopulate.validMessage.set(true);
    }
}
