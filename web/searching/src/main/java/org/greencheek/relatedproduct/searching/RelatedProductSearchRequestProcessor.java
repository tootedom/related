package org.greencheek.relatedproduct.searching;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.searching.disruptor.requestprocessing.SearchRequestSubmissionStatus;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;

import javax.servlet.AsyncContext;
import java.util.List;
import java.util.Map;

/**
 * Entry point for search queries.
 * The users request is
 */
public interface RelatedProductSearchRequestProcessor {
    public SearchRequestSubmissionStatus processRequest(RelatedProductSearchType requestType,
                                                        Map<String,String> parameters,
                                                        SearchResponseContext[] contexts);
    public void shutdown();
}
