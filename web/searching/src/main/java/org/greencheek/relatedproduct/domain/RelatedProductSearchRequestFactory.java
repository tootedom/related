package org.greencheek.relatedproduct.domain;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.util.config.Configuration;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 16/06/2013
 * Time: 17:32
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductSearchRequestFactory implements EventFactory<RelatedProductSearchRequest> {

    private final Configuration config;

    public RelatedProductSearchRequestFactory(Configuration configuration) {
        this.config = configuration;
    }

    public RelatedProductSearchRequest createRelatedProductSearchRequest() {
        return new RelatedProductSearchRequest(config);
    }


    @Override
    public RelatedProductSearchRequest newInstance() {
        return createRelatedProductSearchRequest();
    }
}
