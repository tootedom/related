package org.greencheek.relatedproduct.api.searching;

import org.greencheek.relatedproduct.api.searching.lookup.RelatedProductSearchLookupKeyGenerator;
import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKeyFactory;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 13/10/2013
 * Time: 15:36
 * To change this template use File | Settings | File Templates.
 */
public class KeyFactoryBasedRelatedProductSearchLookupKeyGenerator implements RelatedProductSearchLookupKeyGenerator {

    private final SearchRequestLookupKeyFactory keyFactory;
    private final Configuration configuration;

    public KeyFactoryBasedRelatedProductSearchLookupKeyGenerator(Configuration configuration,SearchRequestLookupKeyFactory factory) {
        this.configuration = configuration;
        this.keyFactory = factory;
    }

    @Override
    public SearchRequestLookupKey createSearchRequestLookupKeyFor(RelatedProductSearch userSearch) {
        return createLookupKey(configuration,userSearch);
    }

    @Override
    public void setSearchRequestLookupKeyOn(RelatedProductSearch userSearch) {
        userSearch.setLookupKey(createLookupKey(configuration,userSearch));
    }

    private int getStringLength(Configuration configuration, RelatedProductSearch search) {
        return search.getAdditionalSearchCriteria().getUrlQueryTypeStringLength() + configuration.getRelatedProductIdLength() + RelatedProductSearch.RESULTS_SET_SIZE_KEY_LENGTH +
               RelatedProductSearch.ID_KEY_LENGTH + 4;

    }

    private SearchRequestLookupKey createLookupKey(Configuration configuration, RelatedProductSearch search) {
        StringBuilder string = new StringBuilder(getStringLength(configuration,search));
        string.append(RelatedProductSearch.ID_KEY).append('=').append(search.getRelatedContentId()).append('&');
        string.append(RelatedProductSearch.RESULTS_SET_SIZE_KEY).append('=').append(search.getMaxResults()).append('&');
        string.append(search.getAdditionalSearchCriteria().toUrlQueryTypeString());
        return keyFactory.createSearchRequestLookupKey(string.toString());
    }

}
