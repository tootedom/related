package org.greencheek.relatedproduct.searching.repository;

import com.github.tlrx.elasticsearch.test.EsSetup;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.greencheek.relatedproduct.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.api.searching.SearchResultsOutcome;
import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKeyFactory;
import org.greencheek.relatedproduct.api.searching.lookup.SipHashSearchRequestLookupKeyFactory;
import org.greencheek.relatedproduct.elastic.ElasticSearchClientFactory;
import org.greencheek.relatedproduct.elastic.NodeBasedElasticSearchClientFactory;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.JsonFrequentlyRelatedSearchResultsConverter;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.*;

import static com.github.tlrx.elasticsearch.test.EsSetup.deleteAll;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the searching for related products against elastic search
 */
public class ElasticSearchFrequentlyRelatedProductSearchProcessorTest {


    private static Configuration configuration;
    private static ElasticSearchClientFactory clientFactory;
    private static EsSetup esSetup;
    private static SearchRequestLookupKeyFactory lookupKeyFactory;

    private static SearchResultsConverter converter;

    private Client esClient;



    @BeforeClass
    public static void setUpElastic() {
        // Instantiates a local node & client
        configuration = new SystemPropertiesConfiguration();
        converter = new JsonFrequentlyRelatedSearchResultsConverter(configuration);




    }

    @After
    public void tearDown() {
        System.clearProperty("related-product-storage.index.name.alias");
        shutdownElastic();
        clientFactory.shutdown();
    }

    public void shutdownElastic() {
        esSetup.terminate();
    }

    @Before
    public void setUp() {

        esSetup = new EsSetup(ImmutableSettings.settingsBuilder()
                .put("cluster.name", configuration.getStorageClusterName())
                .put("index.store.type", "memory")
                .put("index.store.fs.memory.enabled", "true")
                .put("gateway.type", "none")
                .put("index.number_of_shards", "1")
                .put("index.number_of_replicas", "0")
                .put("cluster.routing.schedule", "50ms")
                .put("node.local", true)
                .put("node.data", true)
                .put("discovery.zen.ping.multicast.enabled", "false")
                .put("network.host","127.0.0.1")
                .put("http.enabled",false)
                .build());

        lookupKeyFactory = new SipHashSearchRequestLookupKeyFactory();
        esSetup.execute( deleteAll() );
        esClient = esSetup.client();

        Settings settings = ImmutableSettings.
                settingsBuilder().
                put("network.host", "127.0.0.1").
                put("node.local", true).
                put("node.data", false).
                put("discovery.zen.ping.multicast.enabled", "false").build();

        clientFactory = new NodeBasedElasticSearchClientFactory(settings,configuration);

    }

    private RelatedProductSearch createChannelSearch(String channel, String id) {
        RelatedProductSearch search = new RelatedProductSearch(configuration);
        search.setRelatedContentId(id);
        search.setRelatedProductSearchType(RelatedProductSearchType.FREQUENTLY_RELATED_WITH);

        search.getAdditionalSearchCriteria().addProperty("channel",channel);


        search.setMaxResults(5);
        search.setValidMessage(true);
        return search;
    }

    private RelatedProductSearch createIdSearch(String id) {
        RelatedProductSearch search = new RelatedProductSearch(configuration);

        search.setRelatedContentId(id);
        search.setRelatedProductSearchType(RelatedProductSearchType.FREQUENTLY_RELATED_WITH);

        search.setMaxResults(5);
        search.setValidMessage(true);

        return search;
    }

    @Test
    public void testSearchByChannel() {
        setIndexTemplate();
        indexDoc();

        assertTrue(esSetup.exists(configuration.getStorageIndexNamePrefix() + "-2013-02-11"));

        ElasticSearchFrequentlyRelatedProductSearchProcessor searcher = new ElasticSearchFrequentlyRelatedProductSearchProcessor(configuration);

        RelatedProductSearch[] search = new RelatedProductSearch[] {createChannelSearch("bbc","apprentice")};
        MultiSearchResponse response = searcher.executeSearch(clientFactory.getClient(),search);

        assertTrue(response != null);
        System.out.println(response);
        assertEquals("Did not return the expected 1 search result, for bbc, apprentice search", 1, response.getResponses().length);
        System.out.println(response.getResponses()[0].getFailureMessage());
        assertTrue("Search Response should not be null", response.getResponses()[0].getResponse() != null);
        assertEquals(1,response.getResponses()[0].getResponse().getFacets().getFacets().size());

        Facet f = response.getResponses()[0].getResponse().getFacets().getFacets().get(configuration.getStorageFrequentlyRelatedProductsFacetResultsFacetName());

        assertTrue(f instanceof TermsFacet);

        TermsFacet tf = (TermsFacet)f;

        assertEquals(5,tf.getEntries().size());

        assertEquals("apparentice you're hired",tf.getEntries().get(0).getTerm().string());

        SearchResultEventWithSearchRequestKey[] results = searcher.processMultiSearchResponse(search,response);

        assertTrue(results != null);

        assertEquals("Should have a result",1,results.length);

        verifyTermsInOutput(results[0].getResponse(),tf);

    }

    @Test
    public void testSearchById() {
        setIndexTemplate();
        indexDoc();

        assertTrue(esSetup.exists(configuration.getStorageIndexNamePrefix() + "-2013-02-11"));

        ElasticSearchFrequentlyRelatedProductSearchProcessor searcher = new ElasticSearchFrequentlyRelatedProductSearchProcessor(configuration);

        RelatedProductSearch[] search = new RelatedProductSearch[] {createIdSearch("emmerdale")};
        MultiSearchResponse response = searcher.executeSearch(clientFactory.getClient(),search);

        assertTrue(response != null);
        System.out.println(response);
        assertEquals("Did not return the expected 1 search result, for emmerdale search", 1, response.getResponses().length);
        System.out.println(response.getResponses()[0].getFailureMessage());
        assertTrue("Search Response should not be null", response.getResponses()[0].getResponse() != null);
        assertEquals(1,response.getResponses()[0].getResponse().getFacets().getFacets().size());

        Facet f = response.getResponses()[0].getResponse().getFacets().getFacets().get(configuration.getStorageFrequentlyRelatedProductsFacetResultsFacetName());

        assertTrue(f instanceof TermsFacet);

        TermsFacet tf = (TermsFacet)f;

        assertEquals(3,tf.getEntries().size());

        assertEquals("coronation street",tf.getEntries().get(0).getTerm().string());

        SearchResultEventWithSearchRequestKey[] results = searcher.processMultiSearchResponse(search,response);

        assertTrue(results != null);

        assertEquals("Should have a result",1,results.length);


        verifyTermsInOutput(results[0].getResponse(),tf);
    }

    /**
     * Tests that a failure to connect to ES is caught, and reported back.
     */
    @Test
    public void testSearchFailureIsCaught() {
        setIndexTemplate();
        indexDoc();

        assertTrue(esSetup.exists(configuration.getStorageIndexNamePrefix() + "-2013-02-11"));

        ElasticSearchFrequentlyRelatedProductSearchProcessor searcher = new ElasticSearchFrequentlyRelatedProductSearchProcessor(configuration);

        RelatedProductSearch[] search = new RelatedProductSearch[] {createIdSearch("emmerdale")};

        shutdownElastic();

        MultiSearchResponse response = searcher.executeSearch(clientFactory.getClient(),search);

        SearchResultEventWithSearchRequestKey[] result = searcher.processMultiSearchResponse(search,response);

        SearchRequestLookupKey key = result[0].getRequest();
        SearchResultsEvent<FrequentlyRelatedSearchResult[]> event = result[0].getResponse();

        assertEquals(0, event.getSearchResults().length);
        assertEquals(SearchResultsOutcome.FAILED_REQUEST, event.getOutcomeType());
    }

    /**
     * Tests that an empty results set is returned when no related products are found
     */
    @Test
    public void testNoRelatedProducts() {
        setIndexTemplate();
        indexDoc();

        assertTrue(esSetup.exists(configuration.getStorageIndexNamePrefix() + "-2013-02-11"));

        ElasticSearchFrequentlyRelatedProductSearchProcessor searcher = new ElasticSearchFrequentlyRelatedProductSearchProcessor(configuration);

        RelatedProductSearch[] search = new RelatedProductSearch[] {createIdSearch("elf")};


        MultiSearchResponse response = searcher.executeSearch(clientFactory.getClient(),search);

        SearchResultEventWithSearchRequestKey<FrequentlyRelatedSearchResult[]>[] result = searcher.processMultiSearchResponse(search,response);

        SearchRequestLookupKey key = result[0].getRequest();
        SearchResultsEvent<FrequentlyRelatedSearchResult[]> event = result[0].getResponse();

        assertEquals(0, event.getSearchResults().length);
        assertEquals(SearchResultsOutcome.EMPTY_RESULTS,event.getOutcomeType());
    }

    @Test
    public void testAliasCanBeUsed() {
        System.setProperty("related-product-storage.index.name.alias","beginningoftheyear");
        Configuration configuration = new SystemPropertiesConfiguration();


        setIndexTemplate();
        indexDoc();
        setAlias(configuration);

        ElasticSearchFrequentlyRelatedProductSearchProcessor searcher = new ElasticSearchFrequentlyRelatedProductSearchProcessor(configuration);

        RelatedProductSearch[] search = new RelatedProductSearch[] {createIdSearch("emmerdale")};
        MultiSearchResponse response = searcher.executeSearch(clientFactory.getClient(),search);

        assertTrue(response != null);
        System.out.println(response);
        assertEquals("Did not return the expected 1 search result, for emmerdale search", 1, response.getResponses().length);
        System.out.println(response.getResponses()[0].getFailureMessage());
        assertTrue("Search Response should not be null", response.getResponses()[0].getResponse() != null);
        assertEquals(1,response.getResponses()[0].getResponse().getFacets().getFacets().size());

        Facet f = response.getResponses()[0].getResponse().getFacets().getFacets().get(configuration.getStorageFrequentlyRelatedProductsFacetResultsFacetName());

        assertTrue(f instanceof TermsFacet);

        TermsFacet tf = (TermsFacet)f;

        assertEquals(2,tf.getEntries().size());

        assertEquals("coronation street",tf.getEntries().get(0).getTerm().string());

        SearchResultEventWithSearchRequestKey[] results = searcher.processMultiSearchResponse(search,response);

        assertTrue(results != null);

        assertEquals("Should have a result",1,results.length);


        verifyTermsInOutput(results[0].getResponse(),tf);
    }

    private void verifyTermsInOutput(SearchResultsEvent results, TermsFacet tf) {

        StringBuilder b = new StringBuilder(".*");

        for(TermsFacet.Entry term : tf.getEntries()) {
          b.append(term.getTerm().string()).append(".*");
        }
        String s = converter.convertToString(results);
        assertTrue(s.matches(b.toString()));
    }


    private void setAlias(Configuration configuration) {
        esClient.admin().indices().aliases(new IndicesAliasesRequest().addAlias(configuration.getStorageIndexNamePrefix()+"-2013-01-01",configuration.getStorageIndexNameAlias())).actionGet();
    }

    private void setIndexTemplate() {

        esClient.admin().indices().putTemplate(new PutIndexTemplateRequest("relatedproducts").source("" +
                "{\n" +
                "    \"template\" : \"relatedproducts*\",\n" +
                "    \"settings\" : {\n" +
                "        \"number_of_shards\" : 1,\n" +
                "        \"number_of_replicas\" : 0,\n" +
                "        \"index.cache.field.type\" : \"soft\",\n" +
                "        \"index.refresh_interval\" : \"5s\",\n" +
                "        \"index.store.compress.stored\" : true,\n" +
                "        \"index.query.default_field\" : \"id\",\n" +
                "        \"index.routing.allocation.total_shards_per_node\" : 3\n" +
                "    },\n" +
                "    \"mappings\" : {\n" +
                "        \"relatedproduct\" : {\n" +
                "           \"_all\" : {\"enabled\" : false},\n" +
                "           \"properties\" : {              \n" +
                "              \"id\": { \"type\": \"string\", \"index\": \"not_analyzed\" },\n" +
                "              \"related-with\": { \"type\": \"string\", \"index\": \"not_analyzed\" },              \n" +
                "              \"date\": { \"type\": \"date\", \"index\": \"not_analyzed\" },\n" +
                "              \"channel\" : {\"type\" : \"string\" , \"index\" : \"not_analyzed\" },\n" +
                "              \"site\" : {\"type\" : \"string\" , \"index\" : \"not_analyzed\" },\n" +
                "              \"type\" : {\"type\" : \"string\" , \"index\" : \"not_analyzed\" } \n" +
                "           }   \n" +
                "        }\n" +
                "   }\n" +
                "}")).actionGet();

    }

    private void indexDoc() {




        String content = "{ \"channel\":\"bbc\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"apprentice\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[ \"apprentice you're fired\"] }";

        String content2 = "{ \"channel\":\"channel4\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"8 out of ten cats\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"jimmy carrs standup\"]}";

        String content3 = "{ \"channel\":\"channel4\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"8 out of ten cats\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"big fat quiz of the year\"]}";

        String content4 = "{ \"channel\":\"channel4\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"8 out of ten cats\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"big fat quiz of the year\"]}";

        String content5 = "{ \"channel\":\"channel4\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"8 out of ten cats\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"8 out of ten cats does countdown\"]}";

        String content6 = "{ \"channel\":\"bbc\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"apprentice\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"before they were famous\"]}";

        String content7 = "{ \"channel\":\"bbc\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"apprentice\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"apparentice you're hired\"]}";

        String content8 = "{ \"channel\":\"bbc\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"apprentice\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"apparentice you're hired\"]}";

        String content9 = "{ \"channel\":\"bbc\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"apprentice\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"strictly come dancing\"]}";

        String content10 = "{ \"channel\":\"bbc\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"apprentice\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"the rock\"]}";

        String content11 = "{ \"channel\":\"bbc\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"apprentice\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"apparentice you're hired\"]}";

        String content12 = "{ \"channel\":\"bbc\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"apprentice\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"apparentice you're hired\"]}";

        String content13 = "{ \"channel\":\"bbc\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"apprentice\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"apparentice you're fired\"]}";

        String content14 = "{ \"channel\":\"bbc\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"apprentice\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"apparentice you're fired\"]}";

        String content15 = "{ \"channel\":\"itv\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"emmerdale\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"the bill\"]}";

        String content16 = "{ \"channel\":\"itv\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"emmerdale\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"coronation street\",\"the bill\"]}";

        String content17 = "{ \"channel\":\"itv\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"emmerdale\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"coronation street\"]}";

        String content18 = "{ \"channel\":\"itv\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"emmerdale\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"the bill\",\"coronation street\"]}";

        String content19 = "{ \"channel\":\"itv\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"emmerdale\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"coronation street\",\"itn news\"]}";

        String content20 = "{ \"date\":\"2013-01-01T10:00:00+0100\",\"channel\":\"itv\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"emmerdale\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"coronation street\"]}";

        String content21 = "{ \"date\":\"2013-01-01T10:00:00+0100\",\"channel\":\"itv\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"emmerdale\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[\"coronation street\",\"itn news\"]}";

        String contentNoRelation = "{ \"channel\":\"film4\", \""+ configuration.getKeyForIndexRequestIdAttr()+"\" : \"elf\", \"" + configuration.getKeyForIndexRequestRelatedWithAttr() +"\":" +
                "[ ]}";


        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content2)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content3)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content4)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content5)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content6)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content7)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content8)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content9)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content10)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content11)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content12)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content13)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content14)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content15)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content16)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content17)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content18)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-02-11").type(configuration.getStorageContentTypeName()).source(content19)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-01-01").type(configuration.getStorageContentTypeName()).source(content20)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-01-01").type(configuration.getStorageContentTypeName()).source(content21)).actionGet();
        esClient.index(new IndexRequest().index(configuration.getStorageIndexNamePrefix()+"-2013-01-01").type(configuration.getStorageContentTypeName()).source(contentNoRelation)).actionGet();



        esClient.admin().indices().refresh(new RefreshRequest(configuration.getStorageIndexNamePrefix() + "-2013-02-11")).actionGet();
        esClient.admin().indices().refresh(new RefreshRequest(configuration.getStorageIndexNamePrefix() + "-2013-01-01")).actionGet();


    }
}
