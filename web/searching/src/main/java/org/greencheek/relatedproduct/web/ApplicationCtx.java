package org.greencheek.relatedproduct.web;


import org.greencheek.relatedproduct.searching.*;
import org.greencheek.relatedproduct.searching.disruptor.requestprocessing.RelatedContentSearchRequestProcessorHandlerFactory;
import org.greencheek.relatedproduct.searching.requestprocessing.AsyncContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchRequestParameterValidatorLocator;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 02/06/2013
 * Time: 15:01
 * To change this template use File | Settings | File Templates.
 */
public interface ApplicationCtx {
    public RelatedProductSearchRequestProcessor getRequestProcessor();
    public Configuration getConfiguration();
    public RelatedContentSearchRequestProcessorHandlerFactory getSearchRequestProcessingHandlerFactory();
    public SearchRequestParameterValidatorLocator getSearchRequestParameterValidator();


    public AsyncContextLookup createAsyncContextLookup();
    public RelatedProductSearchResultsResponseProcessor createProcessorForSendingSearchResultsSendToClient();

    public RelatedProductSearchRequestResponseProcessor createSearchRequestAndResponseGateway(AsyncContextLookup asyncContextStorage,
                                                                                              RelatedProductSearchResultsResponseProcessor responseProcessor);

    /**
     * Creates the executor that is responsible for taking search requests, executing them,
     * and sending the results onwards for processing.
     */
    public RelatedProductSearchExecutor createSearchExecutor(RelatedProductSearchRequestResponseProcessor requestAndResponseGateway);

    /**
     * Class that physically performs the search requests, and marshalls the incoming results
     * back into the domain
     * @return
     */
    public RelatedProductSearchRepository createSearchRepository();



}
