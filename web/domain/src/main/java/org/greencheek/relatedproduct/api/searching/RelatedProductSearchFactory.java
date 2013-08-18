package org.greencheek.relatedproduct.api.searching;

import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 14:19
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductSearchFactory {

    private static final Logger log = LoggerFactory.getLogger(RelatedProductSearchFactory.class);


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

        short maxPropertiesToCopy = (short)Math.min(objectToPopulate.additionalSearchCriteria.additionalProperties.length,properties.size());
        log.debug("max properties to copy {}, from properties {}",maxPropertiesToCopy,properties);
        short i=0;
        for(Map.Entry<String,String> entry : properties.entrySet()) {
            if(i==maxPropertiesToCopy) break;
            String key = entry.getKey();
            if(key.equals(sizeKey) || key.equals(idKey)) continue;

            objectToPopulate.additionalSearchCriteria.additionalProperties[i].setName(key);
            objectToPopulate.additionalSearchCriteria.additionalProperties[i].setValue(entry.getValue());
            i++;
        }

        objectToPopulate.additionalSearchCriteria.setNumberOfProperties(i);

        objectToPopulate.searchType.set(type);

        objectToPopulate.validMessage.set(true);
    }

    public static void main(String[] args) {
        Configuration config = new SystemPropertiesConfiguration();
        RelatedProductSearch obj = new RelatedProductSearch(config);
        obj.setByteBuffer(ByteBuffer.allocate(obj.size()),0);
        Map<String,String> ob = new HashMap<String,String>() {{
            put("id","8676");
        }};

        populateSearchObject(config,obj,
                RelatedProductSearchType.FREQUENTLY_RELATED_WITH,ob);
        System.out.println(obj.additionalSearchCriteria.numberOfProperties);
        System.out.println(obj.additionalSearchCriteria.toString());
        System.out.println(obj.getLookupKey(config));
        populateSearchObject(config, obj,
                RelatedProductSearchType.FREQUENTLY_RELATED_WITH, ob);
        System.out.println(obj.additionalSearchCriteria.numberOfProperties);
        System.out.println(obj.additionalSearchCriteria.toString());
        System.out.println(obj.getLookupKey(config));
        populateSearchObject(config, obj,
                RelatedProductSearchType.FREQUENTLY_RELATED_WITH, ob);
        System.out.println(obj.additionalSearchCriteria.numberOfProperties);
        System.out.println(obj.additionalSearchCriteria.toString());
        System.out.println(obj.getLookupKey(config));
        populateSearchObject(config, obj,
                RelatedProductSearchType.FREQUENTLY_RELATED_WITH, ob);
        System.out.println(obj.additionalSearchCriteria.numberOfProperties);
        System.out.println(obj.additionalSearchCriteria.toString());
        System.out.println(obj.getLookupKey(config));
        populateSearchObject(config,obj,
                RelatedProductSearchType.FREQUENTLY_RELATED_WITH,ob);
        System.out.println(obj.additionalSearchCriteria.numberOfProperties);
        System.out.println(obj.additionalSearchCriteria.toString());
        System.out.println(obj.getLookupKey(config));
        populateSearchObject(config,obj,
                RelatedProductSearchType.FREQUENTLY_RELATED_WITH,ob);
        System.out.println(obj.additionalSearchCriteria.numberOfProperties);
        System.out.println(obj.additionalSearchCriteria.toString());
        System.out.println(obj.getLookupKey(config));
        populateSearchObject(config,obj,
                RelatedProductSearchType.FREQUENTLY_RELATED_WITH,ob);
        System.out.println(obj.additionalSearchCriteria.numberOfProperties);
        System.out.println(obj.getLookupKey(config));
        populateSearchObject(config,obj,
                RelatedProductSearchType.FREQUENTLY_RELATED_WITH,ob);
        System.out.println(obj.additionalSearchCriteria.numberOfProperties);
        System.out.println(obj.additionalSearchCriteria.toString());

        System.out.println(obj.getLookupKey(config) + ""  + Math.ceil(3 / 0.75));



    }
}
