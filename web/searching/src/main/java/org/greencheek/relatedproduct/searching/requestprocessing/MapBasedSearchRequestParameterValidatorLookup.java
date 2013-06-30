package org.greencheek.relatedproduct.searching.requestprocessing;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.util.config.Configuration;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 10/06/2013
 * Time: 22:18
 * To change this template use File | Settings | File Templates.
 */
@Named
public class MapBasedSearchRequestParameterValidatorLookup implements  SearchRequestParameterValidatorLocator {

    private final Map<RelatedProductSearchType,SearchRequestParameterValidator> validatorMap = new HashMap<RelatedProductSearchType,SearchRequestParameterValidator>(2);

    @Inject
    public MapBasedSearchRequestParameterValidatorLookup(Configuration configuration) {
        validatorMap.put(RelatedProductSearchType.FREQUENTLY_RELATED_WITH,new FrequentlyRelatedContentRequestParameterValidator(configuration));
    }

    @Override
    public SearchRequestParameterValidator getValidatorForType(RelatedProductSearchType type) {
        return validatorMap.get(type);
    }
}
