package org.greencheek.relatedproduct.web;

import org.greencheek.relatedproduct.elastic.NodeBasedElasticSearchClientFactory;
import org.greencheek.relatedproduct.searching.*;
import org.greencheek.relatedproduct.searching.disruptor.requestprocessing.DisruptorBasedSearchRequestProcessor;
import org.greencheek.relatedproduct.searching.disruptor.requestprocessing.RelatedContentSearchRequestProcessorHandlerFactory;
import org.greencheek.relatedproduct.searching.disruptor.requestprocessing.RoundRobinRelatedContentSearchRequestProcessorHandlerFactory;
import org.greencheek.relatedproduct.searching.disruptor.requestresponse.DisruptorBasedRequestResponseProcessor;
import org.greencheek.relatedproduct.searching.disruptor.requestresponse.DisruptorBasedSearchEventHandler;
import org.greencheek.relatedproduct.searching.disruptor.requestresponse.SearchEventHandler;
import org.greencheek.relatedproduct.searching.disruptor.responseprocessing.DisruptorBasedResponseEventHandler;
import org.greencheek.relatedproduct.searching.disruptor.responseprocessing.DisruptorBasedResponseProcessor;
import org.greencheek.relatedproduct.searching.disruptor.searchexecution.DisruptorBasedRelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.disruptor.searchexecution.RelatedProductSearchEventHandler;
import org.greencheek.relatedproduct.searching.repository.ElasticSearchFrequentlyRelatedProductSearchProcessor;
import org.greencheek.relatedproduct.searching.repository.ElasticSearchRelatedProductSearchRepository;
import org.greencheek.relatedproduct.searching.requestprocessing.AsyncContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.MultiMapAsyncContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchRequestParameterValidatorLocator;
import org.greencheek.relatedproduct.searching.responseprocessing.HttpBasedRelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.util.config.Configuration;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 10/06/2013
 * Time: 21:44
 * To change this template use File | Settings | File Templates.
 */
@Named
public class InjectionBasedApplicationContext implements ApplicationCtx{

    private final Configuration config;
    private final SearchRequestParameterValidatorLocator validatorLocator;
    private final RelatedContentSearchRequestProcessorHandlerFactory searchRequestProcessorHandlerFactory;

    @Inject
    public InjectionBasedApplicationContext(Configuration configuration,
                                            SearchRequestParameterValidatorLocator validatorLocator,
                                            RelatedContentSearchRequestProcessorHandlerFactory searchRequestProcessorHandlerFactory) {
        this.config = configuration;
        this.validatorLocator = validatorLocator;
        this.searchRequestProcessorHandlerFactory = searchRequestProcessorHandlerFactory;

    }

    @Override
    public RelatedContentSearchRequestProcessorHandlerFactory getSearchRequestProcessingHandlerFactory() {
        return searchRequestProcessorHandlerFactory;
    }

    @Override
    public RelatedProductSearchRequestProcessor getRequestProcessor() {
       return new DisruptorBasedSearchRequestProcessor(getSearchRequestProcessingHandlerFactory().createHandler(this),
                                                       config,getSearchRequestParameterValidator());
    }

    @Override
    public Configuration getConfiguration() {
        return config;
    }

    @Override
    public SearchRequestParameterValidatorLocator getSearchRequestParameterValidator() {
        return this.validatorLocator;
    }




    @Override
    public AsyncContextLookup createAsyncContextLookup() {
        return new MultiMapAsyncContextLookup(config);
    }

    @Override
    public RelatedProductSearchResultsResponseProcessor createProcessorForSendingSearchResultsSendToClient() {


        return new DisruptorBasedResponseProcessor(new DisruptorBasedResponseEventHandler(config,new HttpBasedRelatedProductSearchResultsResponseProcessor()),config);

    }


    @Override
    public RelatedProductSearchRequestResponseProcessor createSearchRequestAndResponseGateway(AsyncContextLookup asyncContextStorage,
                                                                                              RelatedProductSearchResultsResponseProcessor responseProcessor) {

        SearchEventHandler searchEventHandler = new DisruptorBasedSearchEventHandler(config,asyncContextStorage,responseProcessor);
        return new DisruptorBasedRequestResponseProcessor(searchEventHandler,config);
    }

    @Override
    public RelatedProductSearchExecutor createSearchExecutor(RelatedProductSearchRequestResponseProcessor requestAndResponseGateway) {
        return new DisruptorBasedRelatedProductSearchExecutor(config,new RelatedProductSearchEventHandler(config,createSearchRepository(),requestAndResponseGateway));

    }

    @Override
    public RelatedProductSearchRepository createSearchRepository() {
        return new ElasticSearchRelatedProductSearchRepository(config,new NodeBasedElasticSearchClientFactory(config),new ElasticSearchFrequentlyRelatedProductSearchProcessor(config));
    }






}
