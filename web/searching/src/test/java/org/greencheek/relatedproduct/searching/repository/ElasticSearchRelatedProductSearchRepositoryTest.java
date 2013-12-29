package org.greencheek.relatedproduct.searching.repository;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.client.Client;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.api.searching.lookup.SipHashSearchRequestLookupKey;
import org.greencheek.relatedproduct.elastic.ElasticSearchClientFactory;
import org.greencheek.relatedproduct.elastic.TransportBasedElasticSearchClientFactory;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRepository;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.util.elasticsearch.ElasticSearchServer;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test that we can find related products in elastic search
 */
public class ElasticSearchRelatedProductSearchRepositoryTest {

    ElasticSearchServer server;
    Configuration configuration;
    ElasticSearchClientFactory factory;
    RelatedProductSearchRepository repository;


    @After
    public void tearDown() {
        System.clearProperty("related-product.storage.index.name.prefix");
        System.clearProperty("related-product.storage.cluster.name");
        System.clearProperty("related-product.elastic.search.transport.hosts");
        System.clearProperty("related-product.frequently.related.search.timeout.in.millis");
        if(server!=null) {
            server.shutdown();
        }

        factory.shutdown();
        repository.shutdown();

    }

    @Before
    public void setUp() {
        String indexName = "relatedprog";
        System.setProperty("related-product.storage.index.name.prefix",indexName);
        // Set the clustername
        System.setProperty("related-product.storage.cluster.name", "relatedprogrammes");
        configuration = new SystemPropertiesConfiguration();

        // Start the Elastic Search Server
        server = new ElasticSearchServer(configuration.getStorageClusterName(),true);

        if(!server.isSetup()) throw new RuntimeException("ElasticSearch Not set");

        server.setIndexTemplate(indexName);

        // Create the client pointing to the above server
        System.setProperty("related-product.elastic.search.transport.hosts","localhost:" + server.getPort());
        configuration = new SystemPropertiesConfiguration();
        factory = new TransportBasedElasticSearchClientFactory(configuration);

        // Create the repo
        repository = new ElasticSearchRelatedProductSearchRepository(factory,new ElasticSearchFrequentlyRelatedProductSearchProcessor(configuration));
    }

    public void shutDown() {

    }

    public RelatedProductSearch[] createSearch() {
        RelatedProductSearch[] searches = new RelatedProductSearch[2];

        int i =0;
        searches[i]  = new RelatedProductSearch(configuration);
        searches[i].setRelatedContentId("anchorman");
        searches[i].setRelatedProductSearchType(RelatedProductSearchType.FREQUENTLY_RELATED_WITH);
        searches[i].setMaxResults(10);
        searches[i].setValidMessage(true);
        searches[i].setLookupKey(new SipHashSearchRequestLookupKey("anchorman"));

        i=1;
        searches[i]  = new RelatedProductSearch(configuration);
        searches[i].setRelatedContentId("the raid");
        searches[i].setRelatedProductSearchType(RelatedProductSearchType.FREQUENTLY_RELATED_WITH);
        searches[i].setMaxResults(10);
        searches[i].setValidMessage(true);
        searches[i].setLookupKey(new SipHashSearchRequestLookupKey("the raid"));

        return searches;
    }

    @Test
    public void testFailedResultsAreReturnedWhenNoIndexExists() {
        SearchResultEventWithSearchRequestKey[] results = repository.findRelatedProducts(configuration, createSearch());
        assertEquals(2,results.length);
        assertSame(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS, results[0].getResponse());
        assertSame(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS, results[1].getResponse());
    }

    @Test
    public void testFailedResultsAreReturnedWhenIndexIsEmpty() {
        server.createIndex(configuration.getStorageIndexNamePrefix());

        SearchResultEventWithSearchRequestKey[] results = repository.findRelatedProducts(configuration, createSearch());
        assertEquals(2,results.length);
        assertSame(SearchResultsEvent.EMPTY_FREQUENTLY_RELATED_SEARCH_RESULTS, results[0].getResponse());
        assertSame(SearchResultsEvent.EMPTY_FREQUENTLY_RELATED_SEARCH_RESULTS, results[1].getResponse());
    }

    @Test
    public void testTimeoutResultsAreReturned() {
        // Create the index
        server.createIndex(configuration.getStorageIndexNamePrefix());

        // set the time tp 1 millis
        System.setProperty("related-product.frequently.related.search.timeout.in.millis", "1");
        try {
            Configuration config = new SystemPropertiesConfiguration();
            ElasticSearchRelatedProductSearchRepository repository = new ElasticSearchRelatedProductSearchRepository(factory,new ElasticSearchFrequentlyRelatedProductSearchProcessor(config));

            SearchResultEventWithSearchRequestKey[] results = repository.findRelatedProducts(configuration, createSearch());
            assertEquals(2,results.length);
            assertSame(SearchResultsEvent.EMPTY_TIMED_OUT_FREQUENTLY_RELATED_SEARCH_RESULTS, results[0].getResponse());
            assertSame(SearchResultsEvent.EMPTY_TIMED_OUT_FREQUENTLY_RELATED_SEARCH_RESULTS, results[1].getResponse());
        }
        finally {
            System.clearProperty("related-product.frequently.related.search.timeout.in.millis");
        }
    }

    @Test
    public void testFailureIsReturnedOnException() {

        ElasticSearchFrequentlyRelatedProductSearchProcessor processor = mock(ElasticSearchFrequentlyRelatedProductSearchProcessor.class);
        doThrow(new RuntimeException()).when(processor).executeSearch(any(Client.class), any(RelatedProductSearch[].class));
        ElasticSearchRelatedProductSearchRepository repository = new ElasticSearchRelatedProductSearchRepository(factory,processor);

        RelatedProductSearch[] searches = createSearch();
        SearchResultEventWithSearchRequestKey[] results = repository.findRelatedProducts(configuration, searches);
        assertEquals(2,results.length);
        assertSame(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS, results[0].getResponse());
        assertSame(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS, results[1].getResponse());

        reset(processor);
        MultiSearchResponse res1 = mock(MultiSearchResponse.class);
        when(processor.executeSearch(any(Client.class), any(RelatedProductSearch[].class))).thenReturn(res1);
        doThrow(new RuntimeException()).when(processor).processMultiSearchResponse(searches, res1);

        results = repository.findRelatedProducts(configuration, searches);
        assertEquals(2,results.length);
        assertSame(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS, results[0].getResponse());
        assertSame(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS, results[1].getResponse());

    }

    private final static String RELATED_CONTENT_BLADES1_PURCHASEa = "{\n"+
            "\"id\": \"anchorman\",\n"+
            "\"date\": \"2013-12-14T17:44:41.943Z\",\n"+
            "\"related-with\": [ \"blades of glory\" ],\n"+
            "\"type\": \"dvd\",\n"+
            "\"site\": \"amazon\",\n"+
            "\"channel\": \"uk\"\n"+
    "}";

    private final static String RELATED_CONTENT_BLADES1_PURCHASEb = "{\n"+
            "\"id\": \"blades of glory\",\n"+
            "\"date\": \"2013-12-14T17:44:41.943Z\",\n"+
            "\"related-with\": [ \"anchor man\" ],\n"+
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
            "\"id\": \"anchorman\",\n"+
            "\"date\": \"2013-12-15T17:44:41.943Z\",\n"+
            "\"related-with\": [ \"blades of glory\",\"dodgeball\" ],\n"+
            "\"type\": \"dvd\",\n"+
            "\"site\": \"amazon\",\n"+
            "\"channel\": \"uk\"\n"+
            "}";

    private final static String RELATED_CONTENT_BLADES2_PURCHASEc = "{\n"+
            "\"id\": \"dodgeball\",\n"+
            "\"date\": \"2013-12-15T17:44:41.943Z\",\n"+
            "\"related-with\": [ \"blades or glory\",\"anchorman\" ],\n"+
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
    public void testFindRelatedProducts() throws Exception {
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
        RelatedProductSearch[] searches = createSearch();

        // search 2 is for the raid
        SearchResultEventWithSearchRequestKey[] results = repository.findRelatedProducts(configuration, searches);

        assertEquals(2,results.length);

        assertNotSame(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS, results[0].getResponse());
        assertNotSame(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS, results[1].getResponse());

        assertNotSame(SearchResultsEvent.EMPTY_FREQUENTLY_RELATED_SEARCH_RESULTS, results[0].getResponse());
        assertNotSame(SearchResultsEvent.EMPTY_FREQUENTLY_RELATED_SEARCH_RESULTS, results[1].getResponse());

        assertNotSame(SearchResultsEvent.EMPTY_TIMED_OUT_FREQUENTLY_RELATED_SEARCH_RESULTS, results[0].getResponse());
        assertNotSame(SearchResultsEvent.EMPTY_TIMED_OUT_FREQUENTLY_RELATED_SEARCH_RESULTS, results[1].getResponse());




        assertEquals(2,results[0].getResponse().getFrequentlyRelatedSearchResults().getNumberOfResults());
        assertEquals("blades of glory",results[0].getResponse().getFrequentlyRelatedSearchResults().getResults()[0].getRelatedProductId());
        assertEquals("enter the dragon",results[1].getResponse().getFrequentlyRelatedSearchResults().getResults()[0].getRelatedProductId());
    }
}
