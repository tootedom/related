package org.greencheek.related.searching.web.bootstrap;


import com.lmax.disruptor.EventFactory;
import org.greencheek.related.api.searching.RelatedItemSearch;
import org.greencheek.related.api.searching.RelatedItemSearchFactory;
import org.greencheek.related.api.searching.lookup.SearchRequestLookupKeyFactory;
import org.greencheek.related.searching.*;
import org.greencheek.related.searching.disruptor.requestprocessing.RelatedContentSearchRequestProcessorHandlerFactory;
import org.greencheek.related.searching.domain.RelatedItemSearchRequestFactory;
import org.greencheek.related.searching.requestprocessing.SearchRequestParameterValidatorLocator;
import org.greencheek.related.searching.requestprocessing.SearchResponseContextLookup;
import org.greencheek.related.searching.responseprocessing.resultsconverter.SearchResultsConverterFactory;
import org.greencheek.related.util.config.Configuration;

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
    public RelatedItemSearchRequestProcessor getRequestProcessor();
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
    public SearchResponseContextLookup getResponseContextLookup();

    /**
     * returns the factory object that is able to lookup and return a SearchResultsConvert that
     * is able to convert a SearchResult type to another respresentation
     * {@link org.greencheek.related.searching.responseprocessing.resultsconverter.SearchResultsConverter}
     * @return
     */
    public SearchResultsConverterFactory getSearchResultsConverterFactory();



    /**
     * Returns the gateway that uses a {@link org.greencheek.related.searching.requestprocessing.SearchResponseContextLookup}
     * to store {@link org.greencheek.related.api.searching.lookup.SearchRequestLookupKey} against awaiting response
     * objects ({@link org.greencheek.related.searching.requestprocessing.SearchResponseContextHolder}.
     *
     * Search results for the pending requests, are then sent to the awaiting Response Objects that were associated with the
     * search request key
     *
     * @return
     */
    public RelatedItemSearchResultsToResponseGateway getSearchResultsToReponseGateway();


    /**
     * Creates the executor that is responsible for taking search requests, executing them,
     * and sending the results onwards for processing.
     */
    public RelatedItemSearchExecutorFactory createSearchExecutorFactory();

    /**
     * Class that physically performs the search requests, and marshalls the incoming results
     * back into the domain
     * @return
     */
    public RelatedItemSearchRepository createSearchRepository();


    /**
     * Creates the RelatedItemSearchRequest factory
     */
    public RelatedItemSearchRequestFactory createRelatedSearchRequestFactory();


    /**
     * Creates the search request key factory.  This factory is responsible for  creating
     * search request lookup keys, that are based on user requests.
     */
    public SearchRequestLookupKeyFactory createSearchRequestLookupKeyFactory();


    /**
     *
     * @return
     */
    public EventFactory<RelatedItemSearch> createRelatedItemSearchEventFactory();


    /**
     * Returns the factory that is response for creating and populating RelatedItemSearch
     * objects.
     */
    public RelatedItemSearchFactory createRelatedItemSearchFactory();

    public void shutdown();

}
