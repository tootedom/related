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

package org.greencheek.related.searching.repository;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.client.Client;
import org.greencheek.related.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.related.api.searching.RelatedItemSearch;
import org.greencheek.related.api.searching.RelatedItemSearchType;
import org.greencheek.related.api.searching.lookup.SipHashSearchRequestLookupKey;
import org.greencheek.related.elastic.ElasticSearchClientFactory;
import org.greencheek.related.elastic.TransportBasedElasticSearchClientFactory;
import org.greencheek.related.elastic.util.ElasticSearchServer;
import org.greencheek.related.searching.RelatedItemSearchRepository;
import org.greencheek.related.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.related.searching.domain.api.SearchResultsEvent;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.ConfigurationConstants;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test that we can find related products in elastic search
 */
public class ElasticSearchRelatedItemSearchRepositoryTest {

    ElasticSearchServer server;
    Configuration configuration;
    ElasticSearchClientFactory factory;
    RelatedItemSearchRepository<FrequentlyRelatedSearchResult[]> repository;


    @After
    public void tearDown() {
        System.clearProperty(ConfigurationConstants.PROPNAME_STORAGE_INDEX_NAME_PREFIX);
        System.clearProperty(ConfigurationConstants.PROPNAME_STORAGE_CLUSTER_NAME);
        System.clearProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS);
        System.clearProperty(ConfigurationConstants.PROPNAME_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS);
        if(server!=null) {
            server.shutdown();
        }

        factory.shutdown();
        repository.shutdown();

    }

    @Before
    public void setUp() {
        String indexName = "relatedprog";
        System.setProperty(ConfigurationConstants.PROPNAME_STORAGE_INDEX_NAME_PREFIX,indexName);
        // Set the clustername
        System.setProperty(ConfigurationConstants.PROPNAME_STORAGE_CLUSTER_NAME, "relatedprogrammes");
        configuration = new SystemPropertiesConfiguration();

        // Start the Elastic Search Server
        server = new ElasticSearchServer(configuration.getStorageClusterName(),true);

        if(!server.isSetup()) throw new RuntimeException("ElasticSearch Not set");

        server.setIndexTemplate(indexName);

        // Create the client pointing to the above server
        System.setProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS,"localhost:" + server.getPort());
        configuration = new SystemPropertiesConfiguration();
        factory = new TransportBasedElasticSearchClientFactory(configuration);

        // Create the repo
        repository = new ElasticSearchRelatedItemSearchRepository(factory,new ElasticSearchFrequentlyRelatedItemSearchProcessor(configuration,new FrequentRelatedSearchRequestBuilder(configuration),RelatedItemNoopGetRepository.INSTANCE));
    }

    public void shutDown() {

    }

    public RelatedItemSearch[] createSearch() {
        RelatedItemSearch[] searches = new RelatedItemSearch[2];

        int i =0;
        searches[i]  = new RelatedItemSearch(configuration);
        searches[i].setRelatedItemId("anchorman");
        searches[i].setRelatedItemSearchType(RelatedItemSearchType.FREQUENTLY_RELATED_WITH);
        searches[i].setMaxResults(10);
        searches[i].setLookupKey(new SipHashSearchRequestLookupKey("anchorman"));

        i=1;
        searches[i]  = new RelatedItemSearch(configuration);
        searches[i].setRelatedItemId("the raid");
        searches[i].setRelatedItemSearchType(RelatedItemSearchType.FREQUENTLY_RELATED_WITH);
        searches[i].setMaxResults(10);
        searches[i].setLookupKey(new SipHashSearchRequestLookupKey("the raid"));

        return searches;
    }

    @Test
    public void testFailedResultsAreReturnedWhenNoIndexExists() {
        SearchResultEventWithSearchRequestKey[] results = repository.findRelatedItems(configuration, createSearch());
        assertEquals(2,results.length);
        System.out.println("testFailedResultsAreReturnedWhenNoIndexExists, Results 0 outcometype: " + results[0].getResponse().getOutcomeType());
        System.out.println("testFailedResultsAreReturnedWhenNoIndexExists, Results 1 outcometype: " + results[1].getResponse().getOutcomeType());

        assertSame(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS, results[0].getResponse());
        assertSame(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS, results[1].getResponse());
    }

    @Test
    public void testFailedResultsAreReturnedWhenIndexIsEmpty() {
        server.createIndex(configuration.getStorageIndexNamePrefix());

        SearchResultEventWithSearchRequestKey[] results = repository.findRelatedItems(configuration, createSearch());
        assertEquals(2,results.length);
        System.out.println("testFailedResultsAreReturnedWhenIndexIsEmpty, Results 0 outcometype: " + results[0].getResponse().getOutcomeType());
        System.out.println("testFailedResultsAreReturnedWhenIndexIsEmpty, Results 1 outcometype: " + results[1].getResponse().getOutcomeType());
        assertSame(SearchResultsEvent.EMPTY_FREQUENTLY_RELATED_SEARCH_RESULTS, results[0].getResponse());
        assertSame(SearchResultsEvent.EMPTY_FREQUENTLY_RELATED_SEARCH_RESULTS, results[1].getResponse());
    }

    @Test
    public void testTimeoutResultsAreReturned() {
        // Create the index
        server.createIndex(configuration.getStorageIndexNamePrefix());

        // set the time tp 1 millis
        System.setProperty(ConfigurationConstants.PROPNAME_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS, "0");
        try {
            Configuration config = new SystemPropertiesConfiguration();
            ElasticSearchRelatedItemSearchRepository repository = new ElasticSearchRelatedItemSearchRepository(factory,new ElasticSearchFrequentlyRelatedItemSearchProcessor(config,new FrequentRelatedSearchRequestBuilder(config),RelatedItemNoopGetRepository.INSTANCE));

            SearchResultEventWithSearchRequestKey[] results = repository.findRelatedItems(configuration, createSearch());
            assertEquals(2,results.length);
            System.out.println("testTimeoutResultsAreReturned, Results 0 outcometype: " + results[0].getResponse().getOutcomeType());
            System.out.println("testTimeoutResultsAreReturned, Results 1 outcometype: " + results[1].getResponse().getOutcomeType());
            assertSame(SearchResultsEvent.EMPTY_TIMED_OUT_FREQUENTLY_RELATED_SEARCH_RESULTS, results[0].getResponse());
            assertSame(SearchResultsEvent.EMPTY_TIMED_OUT_FREQUENTLY_RELATED_SEARCH_RESULTS, results[1].getResponse());
        }
        finally {
            System.clearProperty(ConfigurationConstants.PROPNAME_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS);
        }
    }

    @Test
    public void testFailureIsReturnedOnException() {

        ElasticSearchFrequentlyRelatedItemSearchProcessor processor = mock(ElasticSearchFrequentlyRelatedItemSearchProcessor.class);
        doThrow(new RuntimeException()).when(processor).executeSearch(any(Client.class), any(RelatedItemSearch[].class));
        ElasticSearchRelatedItemSearchRepository repository = new ElasticSearchRelatedItemSearchRepository(factory,processor);

        RelatedItemSearch[] searches = createSearch();
        SearchResultEventWithSearchRequestKey[] results = repository.findRelatedItems(configuration, searches);
        assertEquals(2,results.length);
        System.out.println("testFailureIsReturnedOnException, Results 0 outcometype: " + results[0].getResponse().getOutcomeType());
        System.out.println("testFailureIsReturnedOnException, Results 1 outcometype: " + results[1].getResponse().getOutcomeType());
        assertSame(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS, results[0].getResponse());
        assertSame(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS, results[1].getResponse());

        reset(processor);
        MultiSearchResponse res1 = mock(MultiSearchResponse.class);
        when(processor.executeSearch(any(Client.class), any(RelatedItemSearch[].class))).thenReturn(res1);
        doThrow(new RuntimeException()).when(processor).processMultiSearchResponse(searches, res1);

        results = repository.findRelatedItems(configuration, searches);
        assertEquals(2,results.length);
        System.out.println("testFailureIsReturnedOnException, Results 0 outcometype: " + results[0].getResponse().getOutcomeType());
        System.out.println("testFailureIsReturnedOnException, Results 1 outcometype: " + results[1].getResponse().getOutcomeType());
        assertSame(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS, results[0].getResponse());
        assertSame(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS, results[1].getResponse());

    }

    private final static String RELATED_CONTENT_BLADES1_PURCHASEa = "{\n"+
            "\"id\": \"anchor man\",\n"+
            "\"date\": \"2013-12-14T17:44:41.943Z\",\n"+
            "\"related-with\": [ \"blades of glory\" ],\n"+
            "\"type\": \"dvd\",\n"+
            "\"site\": \"amazon\",\n"+
            "\"channel\": \"uk\"\n"+
    "}";

    private final static String RELATED_CONTENT_BLADES1_PURCHASEb = "{\n"+
            "\"id\": \"blades of glory\",\n"+
            "\"date\": \"2013-12-14T17:44:41.943Z\",\n"+
            "\"related-with\": [ \"anchorman\" ],\n"+
            "\"type\": \"dvd\",\n"+
            "\"site\": \"amazon\",\n"+
            "\"channel\": \"uk\"\n"+
            "}";

    private final static String RELATED_CONTENT_BLADES2_PURCHASEa = "{\n"+
            "\"id\": \"blades of glory\",\n"+
            "\"date\": \"2013-12-15T17:44:41.943Z\",\n"+
            "\"related-with\": [ \"anchorman\",\"dodgeball\" ],\n"+
            "\"type\": \"dvd\",\n"+
            "\"site\": \"amazon\",\n"+
            "\"channel\": \"uk\"\n"+
            "}";

    private final static String RELATED_CONTENT_BLADES2_PURCHASEb = "{\n"+
            "\"id\": \"anchor man\",\n"+
            "\"date\": \"2013-12-15T17:44:41.943Z\",\n"+
            "\"related-with\": [ \"blades of glory\",\"dodgeball\" ],\n"+
            "\"type\": \"dvd\",\n"+
            "\"site\": \"amazon\",\n"+
            "\"channel\": \"uk\"\n"+
            "}";

    private final static String RELATED_CONTENT_BLADES2_PURCHASEc = "{\n"+
            "\"id\": \"dodgeball\",\n"+
            "\"date\": \"2013-12-15T17:44:41.943Z\",\n"+
            "\"related-with\": [ \"blades or glory\",\"anchor man\" ],\n"+
            "\"type\": \"dvd\",\n"+
            "\"site\": \"amazon\",\n"+
            "\"channel\": \"uk\"\n"+
            "}";

    private final static String RELATED_CONTENT_THERAID_PURCHASEa = "{\n"+
            "\"id\": \"the raid\",\n"+
            "\"date\": \"2013-12-24T16:44:41.943Z\",\n"+
            "\"related-with\": [ \"enter the dragon\" ],\n"+
            "\"type\": \"dvd\",\n"+
            "\"site\": \"amazon\",\n"+
            "\"channel\": \"uk\"\n"+
            "}";

    private final static String RELATED_CONTENT_THERAID_PURCHASEb = "{\n"+
            "\"id\": \"enter the dragon\",\n"+
            "\"date\": \"2013-12-24T16:44:41.943Z\",\n"+
            "\"related-with\": [ \"the raid\" ],\n"+
            "\"type\": \"dvd\",\n"+
            "\"site\": \"amazon\",\n"+
            "\"channel\": \"uk\"\n"+
            "}";

    @Test
    public void testFindRelatedItems() throws Exception {
        try {
            server.indexDocument(configuration.getStorageIndexNamePrefix()+"-2013-12-14",RELATED_CONTENT_BLADES1_PURCHASEa);
            server.indexDocument(configuration.getStorageIndexNamePrefix()+"-2013-12-14",RELATED_CONTENT_BLADES1_PURCHASEb);
            server.indexDocument(configuration.getStorageIndexNamePrefix()+"-2013-12-15",RELATED_CONTENT_BLADES2_PURCHASEa);
            server.indexDocument(configuration.getStorageIndexNamePrefix()+"-2013-12-15",RELATED_CONTENT_BLADES2_PURCHASEb);
            server.indexDocument(configuration.getStorageIndexNamePrefix()+"-2013-12-15",RELATED_CONTENT_BLADES2_PURCHASEc);
            server.indexDocument(configuration.getStorageIndexNamePrefix()+"-2013-12-24",RELATED_CONTENT_THERAID_PURCHASEa);
            server.indexDocument(configuration.getStorageIndexNamePrefix()+"-2013-12-24",RELATED_CONTENT_THERAID_PURCHASEb);

            assertEquals(3,server.getIndexCount());
            assertEquals(2,server.getDocCount(configuration.getStorageIndexNamePrefix()+"-2013-12-14"));
            assertEquals(3,server.getDocCount(configuration.getStorageIndexNamePrefix()+"-2013-12-15"));
            assertEquals(2,server.getDocCount(configuration.getStorageIndexNamePrefix()+"-2013-12-24"));
        } catch(Exception e)  {
            fail("Cannot create test date for search test");
        }

        // search 1 is for anchor man
        RelatedItemSearch[] searches = createSearch();

        // search 2 is for the raid
        SearchResultEventWithSearchRequestKey<FrequentlyRelatedSearchResult[]>[] results = repository.findRelatedItems(configuration, searches);

        assertEquals(2,results.length);

        assertNotSame(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS, results[0].getResponse());
        assertNotSame(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS, results[1].getResponse());

        assertNotSame(SearchResultsEvent.EMPTY_FREQUENTLY_RELATED_SEARCH_RESULTS, results[0].getResponse());
        assertNotSame(SearchResultsEvent.EMPTY_FREQUENTLY_RELATED_SEARCH_RESULTS, results[1].getResponse());

        assertNotSame(SearchResultsEvent.EMPTY_TIMED_OUT_FREQUENTLY_RELATED_SEARCH_RESULTS, results[0].getResponse());
        assertNotSame(SearchResultsEvent.EMPTY_TIMED_OUT_FREQUENTLY_RELATED_SEARCH_RESULTS, results[1].getResponse());




        assertEquals(1, results[0].getResponse().getSearchResults().length);
        assertEquals("blades of glory",results[0].getResponse().getSearchResults()[0].getRelatedItemId());
        assertEquals("enter the dragon",results[1].getResponse().getSearchResults()[0].getRelatedItemId());
    }
}
