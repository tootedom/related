package org.greencheek.related.searching.domain;

import com.lmax.disruptor.EventFactory;
import org.greencheek.related.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 16/06/2013
 * Time: 17:32
 * To change this template use File | Settings | File Templates.
 */
public class RelatedItemSearchRequestFactory implements EventFactory<RelatedItemSearchRequest> {

    private final Configuration config;

    public RelatedItemSearchRequestFactory(Configuration configuration) {
        this.config = configuration;
    }

    public RelatedItemSearchRequest createRelatedItemSearchRequest() {
        return new RelatedItemSearchRequest(config);
    }


    @Override
    public RelatedItemSearchRequest newInstance() {
        return createRelatedItemSearchRequest();
    }
}
