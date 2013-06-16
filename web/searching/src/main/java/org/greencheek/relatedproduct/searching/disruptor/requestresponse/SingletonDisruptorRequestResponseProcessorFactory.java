package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestResponseProcessor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestResponseProcessorFactory;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.searching.disruptor.responseprocessing.DisruptorBasedResponseEventHandler;
import org.greencheek.relatedproduct.searching.disruptor.responseprocessing.DisruptorBasedResponseProcessor;
import org.greencheek.relatedproduct.searching.requestprocessing.AsyncContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.MultiMapAsyncContextLookup;
import org.greencheek.relatedproduct.searching.responseprocessing.HttpBasedRelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.util.config.Configuration;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 16/06/2013
 * Time: 06:49
 * To change this template use File | Settings | File Templates.
 */
public class SingletonDisruptorRequestResponseProcessorFactory implements RelatedProductSearchRequestResponseProcessorFactory{

    private final Configuration config;
    private final DisruptorBasedRequestResponseProcessor processor;
    private final AsyncContextLookup asyncContextLookup;
    private final RelatedProductSearchResultsResponseProcessor searchResultsResponseProcessor;

    public SingletonDisruptorRequestResponseProcessorFactory(Configuration configuration) {
        this.config = configuration;
        this.asyncContextLookup = new MultiMapAsyncContextLookup(config);
        this.searchResultsResponseProcessor = new DisruptorBasedResponseProcessor(new DisruptorBasedResponseEventHandler(config,new HttpBasedRelatedProductSearchResultsResponseProcessor()),
                                                                                  config);

        processor = new DisruptorBasedRequestResponseProcessor(new DisruptorBasedSearchEventHandler(config,asyncContextLookup,searchResultsResponseProcessor),
                                                               config);

    }

    @Override
    public RelatedProductSearchRequestResponseProcessor createProcessor() {
        return processor;
    }
}
