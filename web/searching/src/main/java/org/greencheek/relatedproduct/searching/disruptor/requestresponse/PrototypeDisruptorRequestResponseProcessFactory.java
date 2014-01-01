package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import org.greencheek.relatedproduct.searching.RelatedProductSearchResponseProcessor;
import org.greencheek.relatedproduct.searching.disruptor.responseprocessing.ResponseEventHandler;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestResponseProcessorFactory;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.searching.disruptor.responseprocessing.DisruptorBasedResponseEventHandler;
import org.greencheek.relatedproduct.searching.disruptor.responseprocessing.DisruptorBasedResponseProcessor;
import org.greencheek.relatedproduct.searching.requestprocessing.MultiMapSearchResponseContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextLookup;
import org.greencheek.relatedproduct.searching.responseprocessing.MapBasedSearchResponseContextHandlerLookup;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.ExplicitSearchResultsConverterFactory;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.JsonFrequentlyRelatedSearchResultsConverter;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 16/06/2013
 * Time: 08:15
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class PrototypeDisruptorRequestResponseProcessFactory implements RelatedProductSearchRequestResponseProcessorFactory {

    private final Configuration configuration;

    public PrototypeDisruptorRequestResponseProcessFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public RelatedProductSearchResponseProcessor createProcessor() {

        SearchResponseContextLookup ctxStorage =  new MultiMapSearchResponseContextLookup(configuration);

        ResponseEventHandler searchResultsHandler  = new DisruptorBasedResponseEventHandler(
                new MapBasedSearchResponseContextHandlerLookup(configuration),
                new ExplicitSearchResultsConverterFactory(new JsonFrequentlyRelatedSearchResultsConverter(configuration)));




//        RelatedProductSearchResultsResponseProcessor searchResultsProcessor = new DisruptorBasedResponseProcessor(searchResultsHandler,configuration);

//        SearchEventHandler searchEventRequestOrResponseOccurredHandler = new DisruptorBasedSearchEventHandler(configuration,
//                ctxStorage,searchResultsProcessor);
//
//        return new DisruptorBasedRequestResponseProcessor(searchEventRequestOrResponseOccurredHandler,configuration);
        return null;
    }
}
