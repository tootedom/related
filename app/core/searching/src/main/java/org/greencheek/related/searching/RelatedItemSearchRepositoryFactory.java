package org.greencheek.related.searching;

import org.greencheek.related.searching.repository.FrequentRelatedSearchRequestBuilder;
import org.greencheek.related.util.config.Configuration;

/**
 * Created by dominictootell on 21/02/2014.
 */
public interface RelatedItemSearchRepositoryFactory {
    public RelatedItemSearchRepository createRelatedItemSearchRepository(Configuration configuration,
                                                                         FrequentRelatedSearchRequestBuilder searchRequestBuilder);
}
