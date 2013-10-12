package org.greencheek.relatedproduct.api.searching;

import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKeyFactory;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 14:19
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductSearchFactory {

    private static final Logger log = LoggerFactory.getLogger(RelatedProductSearchFactory.class);


    public static RelatedProductSearch createSearchObject(Configuration configuration, SearchRequestLookupKeyFactory searchRequestLookupKeyFactory) {
        RelatedProductSearch ob = new RelatedProductSearch(configuration,searchRequestLookupKeyFactory);
        return ob;
    }

    public static void populateSearchObject(Configuration configuration,RelatedProductSearch objectToPopulate,
                                            RelatedProductSearchType type,
                                            Map<String, String> properties) {
        objectToPopulate.setValidMessage(false);

        String sizeKey = configuration.getRequestParameterForSize();
        String idKey = configuration.getRequestParameterForId();
        try {
            objectToPopulate.setMaxResults(Integer.parseInt(properties.remove(sizeKey)));
        } catch(NumberFormatException e) {
            objectToPopulate.setMaxResults(configuration.getDefaultNumberOfResults());
        }

        objectToPopulate.setRelatedContentId(properties.remove(idKey));

        RelatedProductAdditionalProperties props = objectToPopulate.getAdditionalSearchCriteria();
        int maxPropertiesToCopy = Math.min(props.getNumberOfProperties(),properties.size());
        log.debug("max properties to copy {}, from properties {}",maxPropertiesToCopy,properties);

        int i=0;
        List<String> sortedParameters = new ArrayList<String>(properties.keySet());
        Collections.sort(sortedParameters);
        for(String key : sortedParameters) {
            if(i==maxPropertiesToCopy) break;
            props.setProperty(key,properties.get(key),i++);
        }

        props.setNumberOfProperties(i);

        objectToPopulate.setRelatedProductSearchType(type);



        objectToPopulate.setValidMessage(true);

        objectToPopulate.generateAndSaveLookupKey(configuration);
    }
//
//    public static void main(String[] args) {
//        Configuration config = new SystemPropertiesConfiguration();
//        RelatedProductSearch obj = new RelatedProductSearch(config);
//        Map<String,String> ob = new HashMap<String,String>() {{
//            put("id","8676");
//        }};
//
//        RelatedProductAdditionalProperties props = obj.getAdditionalSearchCriteria();
//
//        populateSearchObject(config,obj,
//                RelatedProductSearchType.FREQUENTLY_RELATED_WITH,ob);
//        System.out.println(props.getNumberOfProperties());
//        System.out.println(props.toString());
//        System.out.println(obj.createLookupKey(config));
//        populateSearchObject(config, obj,
//                RelatedProductSearchType.FREQUENTLY_RELATED_WITH, ob);
//        System.out.println(props.getNumberOfProperties());
//        System.out.println(props.toString());
//        System.out.println(obj.createLookupKey(config));
//        populateSearchObject(config, obj,
//                RelatedProductSearchType.FREQUENTLY_RELATED_WITH, ob);
//        System.out.println(props.getNumberOfProperties());
//        System.out.println(props.toString());
//        System.out.println(obj.createLookupKey(config));
//        populateSearchObject(config, obj,
//                RelatedProductSearchType.FREQUENTLY_RELATED_WITH, ob);
//        System.out.println(props.getNumberOfProperties());
//        System.out.println(props.toString());
//        System.out.println(obj.createLookupKey(config));
//        populateSearchObject(config,obj,
//                RelatedProductSearchType.FREQUENTLY_RELATED_WITH,ob);
//        System.out.println(props.getNumberOfProperties());
//        System.out.println(props.toString());
//        System.out.println(obj.createLookupKey(config));
//        populateSearchObject(config,obj,
//                RelatedProductSearchType.FREQUENTLY_RELATED_WITH,ob);
//        System.out.println(props.getNumberOfProperties());
//        System.out.println(props.toString());
//        System.out.println(obj.createLookupKey(config));
//        populateSearchObject(config,obj,
//                RelatedProductSearchType.FREQUENTLY_RELATED_WITH,ob);
//        System.out.println(props.getNumberOfProperties());
//        System.out.println(obj.createLookupKey(config));
//        populateSearchObject(config,obj,
//                RelatedProductSearchType.FREQUENTLY_RELATED_WITH,ob);
//        System.out.println(props.getNumberOfProperties());
//        System.out.println(props.toString());
//
//        System.out.println(obj.createLookupKey(config) + ""  + Math.ceil(3 / 0.75));
//
//
//
//    }
}
