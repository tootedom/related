package org.greencheek.relatedproduct.searching.web.bootstrap;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.api.searching.*;
import org.greencheek.relatedproduct.api.searching.lookup.RelatedProductSearchFactoryWithSearchLookupKeyFactory;
import org.greencheek.relatedproduct.api.searching.lookup.RelatedProductSearchLookupKeyGenerator;
import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKeyFactory;
import org.greencheek.relatedproduct.api.searching.lookup.SipHashSearchRequestLookupKeyFactory;
import org.greencheek.relatedproduct.searching.*;
import org.greencheek.relatedproduct.searching.disruptor.requestprocessing.*;
import org.greencheek.relatedproduct.searching.disruptor.responseprocessing.*;
import org.greencheek.relatedproduct.searching.disruptor.searchexecution.DisruptorBasedRelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.disruptor.searchexecution.RelatedProductSearchEventHandler;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequestFactory;
import org.greencheek.relatedproduct.searching.repository.ElasticSearchClientFactoryCreator;
import org.greencheek.relatedproduct.searching.repository.ElasticSearchFrequentlyRelatedProductSearchProcessor;
import org.greencheek.relatedproduct.searching.repository.ElasticSearchRelatedProductSearchRepository;
import org.greencheek.relatedproduct.searching.repository.NodeOrTransportBasedElasticSearchClientFactoryCreator;
import org.greencheek.relatedproduct.searching.requestprocessing.MultiMapSearchResponseContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.MapBasedSearchRequestParameterValidatorLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchRequestParameterValidatorLocator;
import org.greencheek.relatedproduct.searching.responseprocessing.MapBasedSearchResponseContextHandlerLookup;
import org.greencheek.relatedproduct.searching.responseprocessing.SearchResponseContextHandlerLookup;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.FrequentlyRelatedSearchResultsArrayConverterFactory;
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
public class SearchBootstrapApplicationCtx implements ApplicationCtx {

    private final Configuration config;
    private final SearchRequestParameterValidatorLocator validatorLocator;
    private final RelatedContentSearchRequestProcessorHandlerFactory searchRequestProcessorHandlerFactory;
    private final RelatedProductSearchRepository searchRepository;
    private final SearchRequestLookupKeyFactory searchRequestLookupKeyFactory;
    private final RelatedProductSearchRequestFactory relatedProductSearchRequestFactory;
    private final RelatedProductSearchFactory relatedProductSearchFactory;
    private final RelatedProductSearchLookupKeyGenerator relatedProductSearchLookupKeyGenerator;

//    private final SearchResponseContextLookup responseContextLookup;




    private final boolean useSharedSearchRepository;

    public SearchBootstrapApplicationCtx() {
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

    public ResponseEventHandler getResponseEventHandler() {
        return new ResponseContextTypeBasedResponseEventHandler(
                getResponseContextHandlerLookup(),
                getSearchResultsConverterFactory());
    }

    public SearchResponseContextHandlerLookup getResponseContextHandlerLookup() {
        return new MapBasedSearchResponseContextHandlerLookup(config);
    }

    @Override
    public SearchResponseContextLookup getResponseContextLookup() {
        return new MultiMapSearchResponseContextLookup(config);
    }

    @Override
    public SearchResultsConverterFactory getSearchResultsConverterFactory() {
        return new FrequentlyRelatedSearchResultsArrayConverterFactory(new JsonFrequentlyRelatedSearchResultsConverter(getConfiguration()));
    }

    @Override
    public RelatedProductSearchResultsToResponseGateway getSearchResultsToReponseGateway() {

        SearchResponseContextLookup responseContextStorage = getResponseContextLookup();
        return new DisruptorRelatedProductSearchResultsToResponseGateway(config,
                new RequestSearchEventProcessor(responseContextStorage),
                new ResponseSearchEventProcessor(responseContextStorage,getResponseEventHandler()));
    }


    @Override
    public RelatedContentSearchRequestProcessorHandlerFactory getSearchRequestProcessingHandlerFactory() {
        return searchRequestProcessorHandlerFactory;
    }

    @Override
    public RelatedProductSearchRequestProcessor getRequestProcessor() {
       RelatedProductSearchFactory factory = createRelatedProductSearchFactory();
       return new DisruptorBasedSearchRequestProcessor(getSearchRequestTranslator(factory),getSearchRequestProcessingHandlerFactory().createHandler(config,this),
               createRelatedSearchRequestFactory(),config,getSearchRequestParameterValidator());
    }

    private IncomingSearchRequestTranslator getSearchRequestTranslator(RelatedProductSearchFactory searchRequestHandlerFactory) {
        return new RelatedProductSearchRequestTranslator(searchRequestHandlerFactory);
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
    public RelatedProductSearchExecutor createSearchExecutor(RelatedProductSearchResultsToResponseGateway gateway) {
        return new DisruptorBasedRelatedProductSearchExecutor(config,createRelatedProductSearchEventFactory(),new RelatedProductSearchEventHandler(config,createSearchRepository(),gateway));

    }

    @Override
    public EventFactory<RelatedProductSearch> createRelatedProductSearchEventFactory() {
        return new EventFactory<RelatedProductSearch>() {
            @Override
            public RelatedProductSearch newInstance() {
                return relatedProductSearchFactory.createSearchObject();
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
    public RelatedProductSearchRequestFactory createRelatedSearchRequestFactory() {
        return relatedProductSearchRequestFactory;
    }

    @Override
    public SearchRequestLookupKeyFactory createSearchRequestLookupKeyFactory() {
        return searchRequestLookupKeyFactory;
    }


}
