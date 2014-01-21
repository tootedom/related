package org.greencheek.related.indexing.elasticsearch;

import com.github.tlrx.elasticsearch.test.EsSetup;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.greencheek.related.api.RelatedItemAdditionalProperties;
import org.greencheek.related.api.indexing.RelatedItem;
import org.greencheek.related.elastic.ElasticSearchClientFactory;
import org.greencheek.related.elastic.NodeBasedElasticSearchClientFactory;
import org.greencheek.related.indexing.RelatedItemStorageLocationMapper;
import org.greencheek.related.indexing.RelatedItemStorageRepository;
import org.greencheek.related.indexing.locationmappers.DayBasedStorageLocationMapper;
import org.greencheek.related.indexing.locationmappers.HourBasedStorageLocationMapper;
import org.greencheek.related.indexing.util.JodaUTCCurrentDateAndHourFormatter;
import org.greencheek.related.indexing.util.JodaUTCCurrentDateFormatter;
import org.greencheek.related.indexing.util.UTCCurrentDateFormatter;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.*;

import java.util.*;

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
public class ElasticSearchRelatedItemIndexingRepositoryTest {

    private RelatedItemStorageRepository repository;
    private static Configuration configuration;
    private static UTCCurrentDateFormatter dateFormatter;
    private static ElasticSearchClientFactory clientFactory;
    private static RelatedItemStorageLocationMapper dayStorageLocationMapper;
    private static RelatedItemStorageLocationMapper hourStorageLocationMapper;


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

        repository = new ElasticSearchRelatedItemIndexingRepository(configuration,clientFactory);
    }

    @After
    public void tearDown() throws Exception {

        // Shutdown the repo
        repository.shutdown();
    }


    private RelatedItem createRelatedItemWithCurrentDay() {
        return createRelatedItemWithGivenDate(currentIndexDate);
    }

    private RelatedItem createRelatedItemWithGivenDate(String date) {
        return createRelatedItemWithGivenDateAndProperties((date != null) ? date : dateFormatter.getCurrentDay(), null);
    }

    private RelatedItem createRelatedItemWithGivenDateAndProperties(String date, Map<String, String> customProps) {
        if(customProps==null) {
            return new RelatedItem(UUID.randomUUID().toString().toCharArray(),date,new char[][]{UUID.randomUUID().toString().toCharArray()},new RelatedItemAdditionalProperties(configuration,0));
        } else {
            return new RelatedItem(UUID.randomUUID().toString().toCharArray(),date,new char[][]{UUID.randomUUID().toString().toCharArray()}, convertFrom(configuration, customProps));
        }
    }

    @Test
    public void testSingleStoreOfProductWithTodaysDate() throws Exception {
        RelatedItem productToStore = createRelatedItemWithCurrentDay();
        indexAndFlush(Arrays.asList(productToStore));
        assertTrue(esSetup.exists(getIndexNamePrefix()+ currentIndexDate));

        QueryBuilder qb = fieldQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productToStore.getId()));

        SearchResponse response = esClient.prepareSearch(getIndexNamePrefix()+ currentIndexDate).setTypes(configuration.getStorageContentTypeName()).setQuery(qb).execute().actionGet();

        assertEquals(1,response.getHits().getTotalHits());
    }


    public static RelatedItemAdditionalProperties convertFrom(Configuration config, Map<String,String> propertes) {
        RelatedItemAdditionalProperties propArrays = new RelatedItemAdditionalProperties(config,propertes.size());

        int i=0;
        for(Map.Entry<String,String> entry : propertes.entrySet() ) {
            propArrays.setProperty(entry.getKey(),entry.getValue(),i++);
        }
        propArrays.setNumberOfProperties(propertes.size());
        return propArrays;
    }

    private String getIndexNamePrefix() {
        String indexName = configuration.getStorageIndexNamePrefix();
        if(indexName.endsWith("-")) return indexName;
        else return indexName+"-";
    }

    @Test
    public void testSingleStoreOfProductWithCustomDate() {
        RelatedItem productToStore = createRelatedItemWithGivenDate("2012-05-16");
        indexAndFlush(Arrays.asList(productToStore));
        assertTrue(esSetup.exists(getIndexNamePrefix()+ "2012-05-16"));

        QueryBuilder qb = fieldQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productToStore.getId()));

        SearchResponse response = esClient.prepareSearch(configuration.getStorageIndexNamePrefix() + "*").setTypes(configuration.getStorageContentTypeName()).setQuery(qb).execute().actionGet();

        assertEquals(1,response.getHits().getTotalHits());
    }


    @Test
    public void testSingleStoreOfProductWithCustomDateAndTime() {
        RelatedItem productToStore = createRelatedItemWithGivenDate("2012-05-15T12:00:00+01:00");
        indexAndFlush(Arrays.asList(productToStore));

        assertTrue(esSetup.exists(getIndexNamePrefix()+ "2012-05-15"));

        QueryBuilder qb = fieldQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productToStore.getId()));

        SearchResponse response = esClient.prepareSearch(configuration.getStorageIndexNamePrefix() + "*").setTypes(configuration.getStorageContentTypeName()).setQuery(qb).execute().actionGet();

        assertEquals(1,response.getHits().getTotalHits());
    }

    @Test
    public void testMultiStoreOfProductWithCustomDateAndToday() {
        RelatedItem[] productsToStore =  new RelatedItem[] {createRelatedItemWithGivenDate("2012-05-15T12:00:00+01:00"),
                                                                 createRelatedItemWithGivenDate("2011-05-15T11:00:00+01:00"),
                                                                 createRelatedItemWithCurrentDay()};

        indexAndFlush(Arrays.asList(productsToStore));

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
        RelatedItem[] productsToStore =  new RelatedItem[] {createRelatedItemWithGivenDate("2012-05-15T12:00:00+01:00"),
                createRelatedItemWithGivenDate("2011-05-15T11:00:00+01:00"),
                createRelatedItemWithCurrentDay()};

        indexAndFlush(Arrays.asList(productsToStore));

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
        RelatedItem[] productsToStore =  new RelatedItem[] {
                createRelatedItemWithGivenDateAndProperties("2012-05-01T12:00:00+01:00",
                        new HashMap() {{
                            put("location", "london");
                            put("country", "gb");
                        }}),

                createRelatedItemWithGivenDateAndProperties("2011-05-02T11:00:00+01:00",
                        new HashMap() {{
                            put("location", "liverpool");
                        }}),

                createRelatedItemWithGivenDateAndProperties("2011-05-03T10:00:00+01:00",
                        new HashMap() {{
                            put("location", "manchester");
                        }})

        };


        indexAndFlush(hourStorageLocationMapper,Arrays.asList(productsToStore));

        assertTrue(esSetup.exists(getIndexNamePrefix()+ "2012-05-01_11"));
        assertTrue(esSetup.exists(getIndexNamePrefix()+ "2011-05-02_10"));
        assertTrue(esSetup.exists(getIndexNamePrefix()+ "2011-05-03_09"));

        QueryBuilder qb = fieldQuery("location", "liverpool");



        SearchResponse response = esClient.prepareSearch(configuration.getStorageIndexNamePrefix() + "*").setTypes(configuration.getStorageContentTypeName()).setQuery(qb).execute().actionGet();

        assertEquals(1,response.getHits().getTotalHits());
    }

    private void indexAndFlush(RelatedItemStorageLocationMapper storageLocationMapper, List<RelatedItem> products) {
        repository.store(storageLocationMapper,products);
        esClient.admin().indices().refresh(new RefreshRequest(configuration.getStorageIndexNamePrefix() + "*")).actionGet();
    }

    private void indexAndFlush(List<RelatedItem> products) {
       indexAndFlush(dayStorageLocationMapper,products);
    }

    @Test
    public void testShutdown() throws Exception {

    }


}
