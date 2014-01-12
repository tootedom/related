package org.greencheek.relatedproduct.searching.web.bootstrap;


import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchFactory;
import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKeyFactory;
import org.greencheek.relatedproduct.searching.*;
import org.greencheek.relatedproduct.searching.disruptor.requestprocessing.RelatedContentSearchRequestProcessorHandlerFactory;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequestFactory;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchRequestParameterValidatorLocator;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverterFactory;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Central point for the wiring up of the search components
 */
public interface ApplicationCtx
{
    /**
     * Process that accepts the type of search request being performed,
     * the search request parameters and the search response contexts to which
     * the reponses are to be sent.
     * @return
     */
    public RelatedProductSearchRequestProcessor getRequestProcessor();


    public Configuration getConfiguration();
    public RelatedContentSearchRequestProcessorHandlerFactory getSearchRequestProcessingHandlerFactory();
    public SearchRequestParameterValidatorLocator getSearchRequestParameterValidator();


    /**
     * Returns the class that is responsible for storing incoming requests against target that are awaiting the
     * results of that request.
     *
     * It is the point of contention for search request and search responses.  The Search requests are stored via the
     * context lookup (the search key), against the response context waiting for the search response. When the response
     * is complete.  The context is used to remove the awaiting contexts that were associated with the search key.
     *
     *
     * @return
     */
    public SearchResponseContextLookup getAsyncContextLookup();

    /**
     * returns the factory object that is able to lookup and return a SearchResultsConvert that
     * is able to convert a SearchResult type to another respresentation
     * {@link org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter}
     * @return
     */
    public SearchResultsConverterFactory getSearchResultsConverterFactory();



    /**
     * Returns the gateway that uses a {@link org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextLookup}
     * to store {@link org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey} against awaiting response
     * objects ({@link org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder}.
     *
     * Search results for the pending requests, are then sent to the awaiting Response Objects that were associated with the
     * search request key
     *
     * @return
     */
    public RelatedProductSearchResultsToResponseGateway getSearchResultsToReponseGateway();


    /**
     * Creates the executor that is responsible for taking search requests, executing them,
     * and sending the results onwards for processing.
     */
    public RelatedProductSearchExecutor createSearchExecutor();

    /**
     * Class that physically performs the search requests, and marshalls the incoming results
     * back into the domain
     * @return
     */
    public RelatedProductSearchRepository createSearchRepository();


    /**
     * Creates the RelatedProductSearchRequest factory
     */
    public RelatedProductSearchRequestFactory createRelatedSearchRequestFactory();


    /**
     * Creates the search request key factory.  This factory is responsible for  creating
     * search request lookup keys, that are based on user requests.
     */
    public SearchRequestLookupKeyFactory createSearchRequestLookupKeyFactory();


    /**
     *
     * @return
     */
    public EventFactory<RelatedProductSearch> createRelatedProductSearchEventFactory();


    /**
     * Returns the factory that is response for creating and populating RelatedProductSearch
     * objects.
     */
    public RelatedProductSearchFactory createRelatedProductSearchFactory();

    public void shutdown();

}
