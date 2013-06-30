package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import com.lmax.disruptor.EventHandler;
import org.greencheek.relatedproduct.searching.domain.api.ResponseEvent;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestResponseProcessor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestResponseProcessorFactory;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.searching.disruptor.responseprocessing.DisruptorBasedResponseEventHandler;
import org.greencheek.relatedproduct.searching.disruptor.responseprocessing.DisruptorBasedResponseProcessor;
import org.greencheek.relatedproduct.searching.requestprocessing.AsyncContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.MultiMapAsyncContextLookup;
import org.greencheek.relatedproduct.searching.responseprocessing.HttpBasedRelatedProductSearchResultsResponseProcessor;
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
public class PrototypeDisruptorRequestResponseProcessFactory implements RelatedProductSearchRequestResponseProcessorFactory {

    private final Configuration config;

    public PrototypeDisruptorRequestResponseProcessFactory(Configuration configuration) {
        this.config = configuration;
    }

    @Override
    public RelatedProductSearchRequestResponseProcessor createProcessor() {

        AsyncContextLookup ctxStorage =  new MultiMapAsyncContextLookup(config);

        EventHandler<ResponseEvent> searchResultsHandler  = new DisruptorBasedResponseEventHandler(
                new HttpBasedRelatedProductSearchResultsResponseProcessor(new ExplicitSearchResultsConverterFactory(new JsonFrequentlyRelatedSearchResultsConverter(config))));

        RelatedProductSearchResultsResponseProcessor searchResultsProcessor = new DisruptorBasedResponseProcessor(searchResultsHandler,config);

        SearchEventHandler searchEventRequestOrResponseOccurredHandler = new DisruptorBasedSearchEventHandler(config,
                ctxStorage,searchResultsProcessor);

        return new DisruptorBasedRequestResponseProcessor(searchEventRequestOrResponseOccurredHandler,config);
    }
}
