package org.greencheek.relatedproduct.indexing.elasticsearch;

import com.github.tlrx.elasticsearch.test.EsSetup;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.greencheek.relatedproduct.domain.RelatedProduct;
import org.greencheek.relatedproduct.elastic.ElasticSearchClientFactory;
import org.greencheek.relatedproduct.elastic.NodeBasedElasticSearchClientFactory;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;
import org.greencheek.relatedproduct.indexing.locationmappers.DayBasedStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.locationmappers.HourBasedStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.util.JodaUTCCurrentDateAndHourFormatter;
import org.greencheek.relatedproduct.indexing.util.JodaUTCCurrentDateFormatter;
import org.greencheek.relatedproduct.indexing.util.UTCCurrentDateFormatter;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.tlrx.elasticsearch.test.EsSetup.deleteAll;
import static com.github.tlrx.elasticsearch.test.EsSetup.index;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 17/06/2013
 * Time: 09:29
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchRelatedProductIndexingRepositoryTest {

    private RelatedProductStorageRepository repository;
    private static Configuration configuration;
    private static UTCCurrentDateFormatter dateFormatter;
    private static ElasticSearchClientFactory clientFactory;
    private static RelatedProductStorageLocationMapper dayStorageLocationMapper;
    private static RelatedProductStorageLocationMapper hourStorageLocationMapper;


    private static EsSetup esSetup;
    private Client esClient;

    private String currentIndexDate;


    @BeforeClass
    public static void setUpElastic() {
        // Instantiates a local node & client
        configuration = new SystemPropertiesConfiguration();

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

        esSetup.execute( deleteAll() );


        // Clean all data

        dateFormatter = new JodaUTCCurrentDateFormatter();

        dayStorageLocationMapper = new DayBasedStorageLocationMapper(configuration,dateFormatter);

        hourStorageLocationMapper = new HourBasedStorageLocationMapper(configuration,new JodaUTCCurrentDateAndHourFormatter());

    }

    @AfterClass
    public static void shutdownElastic() {
        esSetup.terminate();
    }

    @Before
    public void setUp() {
        esSetup.execute( deleteAll() );
        esClient = esSetup.client();
        currentIndexDate = dateFormatter.getCurrentDay();

        Settings settings = ImmutableSettings.
                settingsBuilder().
                put("network.host", "127.0.0.1").
                put("node.local", true).
                put("node.data", false).
                put("discovery.zen.ping.multicast.enabled", "false").build();

        clientFactory = new NodeBasedElasticSearchClientFactory(settings,
                configuration);

        repository = new ElasticSearchRelatedProductIndexingRepository(configuration,clientFactory);
    }

    @After
    public void tearDown() throws Exception {

        // Shutdown the repo
        repository.shutdown();
    }


    private RelatedProduct createRelatedProductWithCurrentDay() {
        return new RelatedProduct(UUID.randomUUID().toString().toCharArray(),currentIndexDate,
                new String[]{UUID.randomUUID().toString()},new String[0][0]);
    }

    private RelatedProduct createRelatedProductWithGivenDate(String date) {
        return new RelatedProduct(UUID.randomUUID().toString().toCharArray(),(date!=null) ? date : dateFormatter.getCurrentDay(),
                new String[]{UUID.randomUUID().toString()},new String[0][0]);
    }

    private RelatedProduct createRelatedProductWithGivenDateAndProperties(String date, String[][] customProp) {
        return new RelatedProduct(UUID.randomUUID().toString().toCharArray(),(date!=null) ? date : dateFormatter.getCurrentDay(),
                new String[]{UUID.randomUUID().toString()},customProp);
    }

    @Test
    public void testSingleStoreOfProductWithTodaysDate() throws Exception {
        RelatedProduct productToStore = createRelatedProductWithCurrentDay();
        indexAndFlush(productToStore);

        assertTrue(esSetup.exists(getIndexNamePrefix()+ currentIndexDate));

        QueryBuilder qb = fieldQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productToStore.getId()));

        SearchResponse response = esClient.prepareSearch(getIndexNamePrefix()+ currentIndexDate).setTypes(configuration.getStorageContentTypeName()).setQuery(qb).execute().actionGet();

        assertEquals(1,response.getHits().getTotalHits());
    }

    private String getIndexNamePrefix() {
        String indexName = configuration.getStorageIndexNamePrefix();
        if(indexName.endsWith("-")) return indexName;
        else return indexName+"-";
    }

    @Test
    public void testSingleStoreOfProductWithCustomDate() {
        RelatedProduct productToStore = createRelatedProductWithGivenDate("2012-05-16");
        indexAndFlush(productToStore);

        assertTrue(esSetup.exists(getIndexNamePrefix()+ "2012-05-16"));

        QueryBuilder qb = fieldQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productToStore.getId()));

        SearchResponse response = esClient.prepareSearch(configuration.getStorageIndexNamePrefix() + "*").setTypes(configuration.getStorageContentTypeName()).setQuery(qb).execute().actionGet();

        assertEquals(1,response.getHits().getTotalHits());
    }


    @Test
    public void testSingleStoreOfProductWithCustomDateAndTime() {
        RelatedProduct productToStore = createRelatedProductWithGivenDate("2012-05-15T12:00:00+01:00");
        indexAndFlush(productToStore);

        assertTrue(esSetup.exists(getIndexNamePrefix()+ "2012-05-15"));

        QueryBuilder qb = fieldQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productToStore.getId()));

        SearchResponse response = esClient.prepareSearch(configuration.getStorageIndexNamePrefix() + "*").setTypes(configuration.getStorageContentTypeName()).setQuery(qb).execute().actionGet();

        assertEquals(1,response.getHits().getTotalHits());
    }

    @Test
    public void testMultiStoreOfProductWithCustomDateAndToday() {
        RelatedProduct[] productsToStore =  new RelatedProduct[] {createRelatedProductWithGivenDate("2012-05-15T12:00:00+01:00"),
                                                                 createRelatedProductWithGivenDate("2011-05-15T11:00:00+01:00"),
                                                                 createRelatedProductWithCurrentDay()};

        indexAndFlush(productsToStore);

        assertTrue(esSetup.exists(getIndexNamePrefix()+"2012-05-15"));
        assertTrue(esSetup.exists(getIndexNamePrefix()+"2011-05-15"));
        assertTrue(esSetup.exists(getIndexNamePrefix()+currentIndexDate));

        QueryBuilder qb = boolQuery().should(fieldQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productsToStore[0].getId())))
                .should(fieldQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productsToStore[1].getId())))
                .should(fieldQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productsToStore[2].getId())));

        SearchResponse response = esClient.prepareSearch(configuration.getStorageIndexNamePrefix() + "*").setTypes(configuration.getStorageContentTypeName()).setQuery(qb).execute().actionGet();

        assertEquals(3,response.getHits().getTotalHits());
    }

    @Test
    public void testMultiStoreOfProductWithCustomDateAndTodayWithHourMapper() {
        RelatedProduct[] productsToStore =  new RelatedProduct[] {createRelatedProductWithGivenDate("2012-05-15T12:00:00+01:00"),
                createRelatedProductWithGivenDate("2011-05-15T11:00:00+01:00"),
                createRelatedProductWithCurrentDay()};

        indexAndFlush(productsToStore);

        assertTrue(esSetup.exists(getIndexNamePrefix()+ "2012-05-15"));
        assertTrue(esSetup.exists(getIndexNamePrefix()+ "2011-05-15"));
        assertTrue(esSetup.exists(getIndexNamePrefix()+ currentIndexDate));

        QueryBuilder qb = boolQuery().should(fieldQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productsToStore[0].getId())))
                .should(fieldQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productsToStore[1].getId())))
                .should(fieldQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productsToStore[2].getId())));

        SearchResponse response = esClient.prepareSearch(configuration.getStorageIndexNamePrefix() + "*").setTypes(configuration.getStorageContentTypeName()).setQuery(qb).execute().actionGet();

        assertEquals(3,response.getHits().getTotalHits());
    }



    @Test
    public void testMultiStoreOfProductWithCustomDateAndProperties() {
        RelatedProduct[] productsToStore =  new RelatedProduct[] {
                createRelatedProductWithGivenDateAndProperties("2012-05-01T12:00:00+01:00",
                        new String[][] { new String[] {
                            "location", "london"}}),

                createRelatedProductWithGivenDateAndProperties("2011-05-02T11:00:00+01:00",
                        new String[][] { new String[] {"location", "liverpool"}}),

                createRelatedProductWithGivenDateAndProperties("2011-05-03T10:00:00+01:00",
                        new String[][] { new String[] {"location", "manchester"}})};


        indexAndFlush(hourStorageLocationMapper,productsToStore);

        assertTrue(esSetup.exists(getIndexNamePrefix()+ "2012-05-01_11"));
        assertTrue(esSetup.exists(getIndexNamePrefix()+ "2011-05-02_10"));
        assertTrue(esSetup.exists(getIndexNamePrefix()+ "2011-05-03_09"));

        QueryBuilder qb = fieldQuery("location", "liverpool");



        SearchResponse response = esClient.prepareSearch(configuration.getStorageIndexNamePrefix() + "*").setTypes(configuration.getStorageContentTypeName()).setQuery(qb).execute().actionGet();

        assertEquals(1,response.getHits().getTotalHits());
    }

    private void indexAndFlush(RelatedProductStorageLocationMapper storageLocationMapper, RelatedProduct... products) {
        repository.store(storageLocationMapper,products);
        esClient.admin().indices().refresh(new RefreshRequest(configuration.getStorageIndexNamePrefix() + "*")).actionGet();
    }

    private void indexAndFlush(RelatedProduct... products) {
       indexAndFlush(dayStorageLocationMapper,products);
    }

    @Test
    public void testShutdown() throws Exception {

    }


}
