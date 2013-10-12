package org.greencheek.relatedproduct.searching.domain;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKeyFactory;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 16/06/2013
 * Time: 17:32
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductSearchRequestFactory implements EventFactory<RelatedProductSearchRequest> {

    private final Configuration config;
    private final SearchRequestLookupKeyFactory searchRequestLookupKeyFactory;

    public RelatedProductSearchRequestFactory(Configuration configuration, SearchRequestLookupKeyFactory searchRequestLookupKeyFactory) {
        this.config = configuration;
        this.searchRequestLookupKeyFactory = searchRequestLookupKeyFactory;
    }

    public RelatedProductSearchRequest createRelatedProductSearchRequest() {
        return new RelatedProductSearchRequest(config,searchRequestLookupKeyFactory);
    }


    @Override
    public RelatedProductSearchRequest newInstance() {
        return createRelatedProductSearchRequest();
    }
}
