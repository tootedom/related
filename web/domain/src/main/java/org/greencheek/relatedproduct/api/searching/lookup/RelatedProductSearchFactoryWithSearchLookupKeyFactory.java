package org.greencheek.relatedproduct.api.searching.lookup;

import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchFactory;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**

 */
public class RelatedProductSearchFactoryWithSearchLookupKeyFactory implements RelatedProductSearchFactory {

    private static final Logger log = LoggerFactory.getLogger(RelatedProductSearchFactoryWithSearchLookupKeyFactory.class);
    private final Configuration configuration;
    private final RelatedProductSearchLookupKeyGenerator lookupKeyGenerator;

    public RelatedProductSearchFactoryWithSearchLookupKeyFactory(Configuration config, RelatedProductSearchLookupKeyGenerator lookupKeyGenerator) {
        this.configuration = config;
        this.lookupKeyGenerator = lookupKeyGenerator;

    }

    @Override
    public RelatedProductSearch createSearchObject() {
        return new RelatedProductSearch(configuration);
    }

    @Override
    public void populateSearchObject(RelatedProductSearch objectToPopulate,
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
        int maxPropertiesToCopy = Math.min(props.getMaxNumberOfAvailableProperties(),properties.size());
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

        lookupKeyGenerator.setSearchRequestLookupKeyOn(objectToPopulate);

        objectToPopulate.setValidMessage(true);

    }

    @Override
    public RelatedProductSearch newInstance() {
        return createSearchObject();
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
