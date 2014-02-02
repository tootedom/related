/*
 *
 *  * Licensed to Relateit under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Relateit licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.greencheek.related.searching.web.bootstrap;

import com.lmax.disruptor.EventFactory;
import org.greencheek.related.api.searching.KeyFactoryBasedRelatedItemSearchLookupKeyGenerator;
import org.greencheek.related.api.searching.RelatedItemSearch;
import org.greencheek.related.api.searching.RelatedItemSearchFactory;
import org.greencheek.related.api.searching.lookup.RelatedItemSearchFactoryWithSearchLookupKeyFactory;
import org.greencheek.related.api.searching.lookup.RelatedItemSearchLookupKeyGenerator;
import org.greencheek.related.api.searching.lookup.SearchRequestLookupKeyFactory;
import org.greencheek.related.api.searching.lookup.SipHashSearchRequestLookupKeyFactory;
import org.greencheek.related.searching.*;
import org.greencheek.related.searching.disruptor.requestprocessing.*;
import org.greencheek.related.searching.disruptor.responseprocessing.*;
import org.greencheek.related.searching.domain.RelatedItemSearchRequestFactory;
import org.greencheek.related.searching.executor.SearchExecutorFactory;
import org.greencheek.related.searching.repository.ElasticSearchClientFactoryCreator;
import org.greencheek.related.searching.repository.ElasticSearchFrequentlyRelatedItemSearchProcessor;
import org.greencheek.related.searching.repository.ElasticSearchRelatedItemSearchRepository;
import org.greencheek.related.searching.repository.NodeOrTransportBasedElasticSearchClientFactoryCreator;
import org.greencheek.related.searching.requestprocessing.MapBasedSearchRequestParameterValidatorLookup;
import org.greencheek.related.searching.requestprocessing.MultiMapSearchResponseContextLookup;
import org.greencheek.related.searching.requestprocessing.SearchRequestParameterValidatorLocator;
import org.greencheek.related.searching.requestprocessing.SearchResponseContextLookup;
import org.greencheek.related.searching.responseprocessing.MapBasedSearchResponseContextHandlerLookup;
import org.greencheek.related.searching.responseprocessing.SearchResponseContextHandlerLookup;
import org.greencheek.related.searching.responseprocessing.resultsconverter.FrequentlyRelatedSearchResultsArrayConverterFactory;
import org.greencheek.related.searching.responseprocessing.resultsconverter.JsonFrequentlyRelatedSearchResultsConverter;
import org.greencheek.related.searching.responseprocessing.resultsconverter.SearchResultsConverterFactory;
import org.greencheek.related.searching.responseprocessing.resultsconverter.StringBasedJsonFrequentlyRelatedSearchResultsConverter;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.YamlSystemPropertiesConfiguration;

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
    private final RelatedItemSearchRepository searchRepository;
    private final SearchRequestLookupKeyFactory searchRequestLookupKeyFactory;
    private final RelatedItemSearchRequestFactory relatedItemSearchRequestFactory;
    private final RelatedItemSearchFactory relatedItemSearchFactory;
    private final RelatedItemSearchLookupKeyGenerator relatedItemSearchLookupKeyGenerator;

//    private final SearchResponseContextLookup responseContextLookup;




    private final boolean useSharedSearchRepository;

    public SearchBootstrapApplicationCtx() {
        this.config = new YamlSystemPropertiesConfiguration();
        this.validatorLocator = new MapBasedSearchRequestParameterValidatorLookup(config);
        this.searchRequestProcessorHandlerFactory = new RoundRobinRelatedContentSearchRequestProcessorHandlerFactory();

        useSharedSearchRepository = config.useSharedSearchRepository();
        if(useSharedSearchRepository) {
            this.searchRepository = getRepository(config);
        } else {
            this.searchRepository = null;
        }
        this.searchRequestLookupKeyFactory = new SipHashSearchRequestLookupKeyFactory();
        this.relatedItemSearchRequestFactory = new RelatedItemSearchRequestFactory(config);
        this.relatedItemSearchLookupKeyGenerator = new KeyFactoryBasedRelatedItemSearchLookupKeyGenerator(config,searchRequestLookupKeyFactory);
        this.relatedItemSearchFactory = new RelatedItemSearchFactoryWithSearchLookupKeyFactory(config, relatedItemSearchLookupKeyGenerator);

    }


    public void shutdown() {

    }

    public ResponseEventHandler getResponseEventHandler() {
        return new DisruptorBasedResponseContextTypeBasedResponseEventHandler(config,
            new ResponseContextTypeBasedResponseEventHandler(
                getResponseContextHandlerLookup(),
                getSearchResultsConverterFactory()));
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
        return new FrequentlyRelatedSearchResultsArrayConverterFactory(new StringBasedJsonFrequentlyRelatedSearchResultsConverter(getConfiguration()));
    }

    @Override
    public RelatedItemSearchResultsToResponseGateway getSearchResultsToReponseGateway() {

        SearchResponseContextLookup responseContextStorage = getResponseContextLookup();
        return new DisruptorRelatedItemSearchResultsToResponseGateway(config,
                new RequestSearchEventProcessor(responseContextStorage),
                new ResponseSearchEventProcessor(responseContextStorage,getResponseEventHandler()));
    }


    @Override
    public RelatedContentSearchRequestProcessorHandlerFactory getSearchRequestProcessingHandlerFactory() {
        return searchRequestProcessorHandlerFactory;
    }

    @Override
    public RelatedItemSearchRequestProcessor getRequestProcessor() {
       RelatedItemSearchResultsToResponseGateway gateway = getSearchResultsToReponseGateway();
       RelatedItemSearchFactory factory = createRelatedItemSearchFactory();
       return new DisruptorBasedSearchRequestProcessor(getSearchRequestTranslator(factory),getSearchRequestProcessingHandlerFactory().createHandler(config,gateway,createSearchExecutorFactory()),
               createRelatedSearchRequestFactory(),config,getSearchRequestParameterValidator());
    }

    private IncomingSearchRequestTranslator getSearchRequestTranslator(RelatedItemSearchFactory searchRequestHandlerFactory) {
        return new RelatedItemSearchRequestTranslator(searchRequestHandlerFactory);
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
    public RelatedItemSearchExecutorFactory createSearchExecutorFactory() {
        return new SearchExecutorFactory(this);

    }

    @Override
    public EventFactory<RelatedItemSearch> createRelatedItemSearchEventFactory() {
        return new EventFactory<RelatedItemSearch>() {
            @Override
            public RelatedItemSearch newInstance() {
                return relatedItemSearchFactory.createSearchObject();
            }
        };
    }

    @Override
    public RelatedItemSearchFactory createRelatedItemSearchFactory() {
        return relatedItemSearchFactory;
    }

    @Override
    public RelatedItemSearchRepository createSearchRepository() {
        if(useSharedSearchRepository) {
            return searchRepository;
        } else {
            return getRepository(config);
        }
    }

    private ElasticSearchClientFactoryCreator getClientFactoryCreator() {
        return NodeOrTransportBasedElasticSearchClientFactoryCreator.INSTANCE;
    }

    private RelatedItemSearchRepository getRepository(Configuration configuration) {
        return new ElasticSearchRelatedItemSearchRepository(
                getClientFactoryCreator().getElasticSearchClientConnectionFactory(configuration),
                new ElasticSearchFrequentlyRelatedItemSearchProcessor(config)
        );
    }


    @Override
    public RelatedItemSearchRequestFactory createRelatedSearchRequestFactory() {
        return relatedItemSearchRequestFactory;
    }

    @Override
    public SearchRequestLookupKeyFactory createSearchRequestLookupKeyFactory() {
        return searchRequestLookupKeyFactory;
    }


}
