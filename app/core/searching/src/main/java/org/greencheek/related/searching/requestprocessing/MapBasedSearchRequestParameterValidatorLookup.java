package org.greencheek.related.searching.requestprocessing;

import org.greencheek.related.api.searching.RelatedItemSearchType;
import org.greencheek.related.util.config.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores validators that can be used to validate request parameters
 */
public class MapBasedSearchRequestParameterValidatorLookup implements SearchRequestParameterValidatorLocator {

    private final Map<RelatedItemSearchType,SearchRequestParameterValidator> validatorMap = new HashMap<RelatedItemSearchType,SearchRequestParameterValidator>(2);

    public MapBasedSearchRequestParameterValidatorLookup(Configuration configuration) {
        validatorMap.put(RelatedItemSearchType.FREQUENTLY_RELATED_WITH,new FrequentlyRelatedContentRequestParameterValidator(configuration));
    }

    @Override
    public SearchRequestParameterValidator getValidatorForType(RelatedItemSearchType type) {
        return validatorMap.get(type);
    }
}
