package org.greencheek.relatedproduct.searching.bootstrap;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.api.searching.*;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKeyFactory;
import org.greencheek.relatedproduct.domain.searching.SipHashSearchRequestLookupKeyFactory;
import org.greencheek.relatedproduct.elastic.ElasticSearchClientFactory;
import org.greencheek.relatedproduct.elastic.NodeBasedElasticSearchClientFactory;
import org.greencheek.relatedproduct.elastic.TransportBasedElasticSearchClientFactory;
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
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequestFactory;
import org.greencheek.relatedproduct.searching.repository.ElasticSearchClientFactoryCreator;
import org.greencheek.relatedproduct.searching.repository.ElasticSearchFrequentlyRelatedProductSearchProcessor;
import org.greencheek.relatedproduct.searching.repository.ElasticSearchRelatedProductSearchRepository;
import org.greencheek.relatedproduct.searching.repository.NodeOrTransportBasedElasticSearchClientFactoryCreator;
import org.greencheek.relatedproduct.searching.requestprocessing.AsyncContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.MapBasedSearchRequestParameterValidatorLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.MultiMapAsyncContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchRequestParameterValidatorLocator;
import org.greencheek.relatedproduct.searching.responseprocessing.HttpBasedRelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.ExplicitSearchResultsConverterFactory;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.JsonFrequentlyRelatedSearchResultsConverter;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverterFactory;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 10/06/2013
 * Time: 21:44
 * To change this template use File | Settings | File Templates.
 */
public class BootstrapApplicationContext implements ApplicationCtx {

    private final Configuration config;
    private final SearchRequestParameterValidatorLocator validatorLocator;
    private final RelatedContentSearchRequestProcessorHandlerFactory searchRequestProcessorHandlerFactory;
    private final RelatedProductSearchRepository searchRepository;
    private final SearchRequestLookupKeyFactory searchRequestLookupKeyFactory;
    private final RelatedProductSearchRequestFactory relatedProductSearchRequestFactory;
    private final RelatedProductSearchFactory relatedProductSearchFactory;
    private final RelatedProductSearchLookupKeyGenerator relatedProductSearchLookupKeyGenerator;
    private final boolean useSharedSearchRepository;

    public BootstrapApplicationContext() {
        this.config = new SystemPropertiesConfiguration();
        this.validatorLocator = new MapBasedSearchRequestParameterValidatorLookup(config);
        this.searchRequestProcessorHandlerFactory = new RoundRobinRelatedContentSearchRequestProcessorHandlerFactory();

        useSharedSearchRepository = config.useSharedSearchRepository();
        if(useSharedSearchRepository) {
            this.searchRepository = getRepository(config);
        } else {
            this.searchRepository = null;
        }
        this.searchRequestLookupKeyFactory = new SipHashSearchRequestLookupKeyFactory();
        this.relatedProductSearchRequestFactory = new RelatedProductSearchRequestFactory(config);
        this.relatedProductSearchLookupKeyGenerator = new KeyFactoryBasedRelatedProductSearchLookupKeyGenerator(config,searchRequestLookupKeyFactory);
        this.relatedProductSearchFactory = new RelatedProductSearchFactoryWithSearchLookupKeyFactory(config,relatedProductSearchLookupKeyGenerator);


    }


    public void shutdown() {

    }

    @Override
    public RelatedContentSearchRequestProcessorHandlerFactory getSearchRequestProcessingHandlerFactory() {
        return searchRequestProcessorHandlerFactory;
    }

    @Override
    public RelatedProductSearchRequestProcessor getRequestProcessor() {
       return new DisruptorBasedSearchRequestProcessor(getSearchRequestProcessingHandlerFactory().createHandler(config,this),
               createRelatedSearchRequestFactory(),createRelatedProductSearchFactory(),config,getSearchRequestParameterValidator());
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
        return new DisruptorBasedResponseProcessor(new DisruptorBasedResponseEventHandler(
                new HttpBasedRelatedProductSearchResultsResponseProcessor(config,createSearchResultsConverterFactory())),config);

    }


    @Override
    public RelatedProductSearchRequestResponseProcessor createSearchRequestAndResponseGateway(AsyncContextLookup asyncContextStorage,
                                                                                              RelatedProductSearchResultsResponseProcessor responseProcessor) {

        SearchEventHandler searchEventHandler = new DisruptorBasedSearchEventHandler(config,asyncContextStorage,responseProcessor);
        return new DisruptorBasedRequestResponseProcessor(searchEventHandler,config);
    }

    @Override
    public RelatedProductSearchExecutor createSearchExecutor(RelatedProductSearchRequestResponseProcessor requestAndResponseGateway) {
        return new DisruptorBasedRelatedProductSearchExecutor(config,createRelatedProductSearchEventFactory(),new RelatedProductSearchEventHandler(config,createSearchRepository(),requestAndResponseGateway));

    }

    @Override
    public EventFactory<RelatedProductSearch> createRelatedProductSearchEventFactory() {
        return new EventFactory<RelatedProductSearch>() {
            @Override
            public RelatedProductSearch newInstance() {
                return relatedProductSearchFactory.createSearchObject(config);
            }
        };
    }

    @Override
    public RelatedProductSearchFactory createRelatedProductSearchFactory() {
        return relatedProductSearchFactory;
    }

    @Override
    public RelatedProductSearchRepository createSearchRepository() {
        if(useSharedSearchRepository) {
            return searchRepository;
        } else {
            return getRepository(config);
        }
    }

    private ElasticSearchClientFactoryCreator getClientFactoryCreator() {
        return NodeOrTransportBasedElasticSearchClientFactoryCreator.INSTANCE;
    }

    private RelatedProductSearchRepository getRepository(Configuration configuration) {
        return new ElasticSearchRelatedProductSearchRepository(
                getClientFactoryCreator().getElasticSearchClientConnectionFactory(configuration),
                new ElasticSearchFrequentlyRelatedProductSearchProcessor(config)
        );
    }


        @Override
    public SearchResultsConverterFactory createSearchResultsConverterFactory() {
        return new ExplicitSearchResultsConverterFactory(new JsonFrequentlyRelatedSearchResultsConverter(getConfiguration()));
    }

    @Override
    public RelatedProductSearchRequestFactory createRelatedSearchRequestFactory() {
        return relatedProductSearchRequestFactory;
    }

    @Override
    public SearchRequestLookupKeyFactory createSearchRequestLookupKeyFactory() {
        return searchRequestLookupKeyFactory;
    }


}
