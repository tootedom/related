package org.greencheek.related.searching;

import org.greencheek.related.api.searching.RelatedItemSearchType;
import org.greencheek.related.searching.disruptor.requestprocessing.SearchRequestSubmissionStatus;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;

import java.util.Map;

/**
 * Entry point for search queries.
 * The users request is
 */
public interface RelatedItemSearchRequestProcessor {
    public SearchRequestSubmissionStatus processRequest(RelatedItemSearchType requestType,
                                                        Map<String,String> parameters,
                                                        SearchResponseContext[] contexts);
    public void shutdown();
}
