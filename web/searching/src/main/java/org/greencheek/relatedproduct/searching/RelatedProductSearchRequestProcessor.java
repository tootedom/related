package org.greencheek.relatedproduct.searching;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.searching.disruptor.requestprocessing.SearchRequestSubmissionStatus;

import javax.servlet.AsyncContext;
import java.util.List;
import java.util.Map;

/**
 * Entry point for search queries.
 * The users request is
 */
public interface RelatedProductSearchRequestProcessor {
    public SearchRequestSubmissionStatus processRequest(RelatedProductSearchType requestType, Map<String,String> parameters, AsyncContext context);
    public void shutdown();
}
