package org.greencheek.relatedproduct.searching.responseprocessing;

import org.greencheek.relatedproduct.resultsconverter.SearchResultsConverter;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsResponseProcessor;

import javax.inject.Named;
import javax.servlet.AsyncContext;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 17:08
 * To change this template use File | Settings | File Templates.
 */
@Named
public class HttpBasedRelatedProductSearchResultsResponseProcessor implements RelatedProductSearchResultsResponseProcessor {
    @Override
    public void processSearchResults(List<AsyncContext> context, SearchResultsConverter results) {
        System.out.println(results.convertToString());
    }
}
