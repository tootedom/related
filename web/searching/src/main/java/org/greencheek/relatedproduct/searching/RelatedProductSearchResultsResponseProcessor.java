package org.greencheek.relatedproduct.searching;

import org.greencheek.relatedproduct.domain.searching.SearchResult;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;

import javax.servlet.AsyncContext;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */
public interface RelatedProductSearchResultsResponseProcessor {
    public void processSearchResults(AsyncContext[] context, SearchResultsEvent results);
    public void shutdown();
}
