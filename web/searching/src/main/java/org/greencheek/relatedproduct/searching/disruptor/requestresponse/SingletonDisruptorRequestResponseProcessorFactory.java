package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import org.greencheek.relatedproduct.searching.RelatedProductSearchResponseProcessor;
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
 * Time: 06:49
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class SingletonDisruptorRequestResponseProcessorFactory implements RelatedProductSearchRequestResponseProcessorFactory{

    private final DisruptorBasedRequestResponseProcessor processor = null;
    private final SearchResponseContextLookup asyncContextLookup = null;
    private final RelatedProductSearchResultsResponseProcessor searchResultsResponseProcessor = null;

    public SingletonDisruptorRequestResponseProcessorFactory(Configuration configuration) {
//        this.asyncContextLookup = new MultiMapSearchResponseContextLookup(configuration);
//        this.searchResultsResponseProcessor = new DisruptorBasedResponseProcessor(
//                new DisruptorBasedResponseEventHandler(
//                        new MapBasedSearchResponseContextHandlerLookup(configuration),
//                        new ExplicitSearchResultsConverterFactory(new JsonFrequentlyRelatedSearchResultsConverter(configuration))),
//                configuration);
//
//        processor = new DisruptorBasedRequestResponseProcessor(new DisruptorBasedSearchEventHandler(configuration,asyncContextLookup,searchResultsResponseProcessor),
//                configuration);

    }

    @Override
    public RelatedProductSearchResponseProcessor createProcessor() {
        return processor;
    }
}
