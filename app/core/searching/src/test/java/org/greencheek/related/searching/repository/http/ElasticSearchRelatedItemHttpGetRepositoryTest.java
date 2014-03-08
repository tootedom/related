package org.greencheek.related.searching.repository.http;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.greencheek.related.elastic.http.HttpElasticSearchClientFactory;
import org.greencheek.related.elastic.http.ahc.AHCHttpElasticSearchClientFactory;
import org.greencheek.related.searching.RelatedItemGetRepository;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.ConfigurationConstants;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.ServerSocket;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by dominictootell on 05/03/2014.
 */
public class ElasticSearchRelatedItemHttpGetRepositoryTest {

    private final String FOUND_TWO = "{\"docs\":[{\"_index\":\"relateddocs\",\"_type\":\"relateddoc\",\"_id\":\"1\",\"_version\":1,\"found\":true, \"_source\" : {\"channel\":\"de\",\"site\":\"amazon\",\"type\":\"map\",\"md5\":\"a8f346c5ddbbd8d438bc40f0049cc7f8\"}},{\"_index\":\"relateddocs\",\"_type\":\"relateddoc\",\"_id\":\"2\",\"_version\":1,\"found\":true, \"_source\" : {\"channel\":\"de\",\"site\":\"amazon\",\"type\":\"compass\",\"md5\":\"71a5120bdf4d998c2b043a681b1bd211\"}}]}";
    private final String FOUND_ONE_ONE_WITH_ERROR = "{\"docs\":[{\"_index\":\"relateddocs\",\"_type\":\"relateddoc\",\"_id\":\"1\",\"_version\":1,\"found\":true, \"_source\" : {\"channel\":\"de\",\"site\":\"amazon\",\"type\":\"map\",\"md5\":\"a8f346c5ddbbd8d438bc40f0049cc7f8\"}},{\"_index\":\"relateddocs\",\"_type\":\"relateddoc\",\"_id\":\"2\",\"error\":\"EsRejectedExecutionException[rejected execution (queue capacity 1) on org.elasticsearch.action.support.single.shard.TransportShardSingleOperationAction$AsyncSingleAction$1@4149696c]\"}]}";
    private final String FOUND_TWO_ONE_NOT_FOUND = "{\"docs\":[{\"_index\":\"relateddocs\",\"_type\":\"relateddoc\",\"_id\":\"1\",\"_version\":1,\"found\":true, \"_source\" : {\"channel\":\"de\",\"site\":\"amazon\",\"type\":\"map\",\"md5\":\"a8f346c5ddbbd8d438bc40f0049cc7f8\"}},{\"_index\":\"relateddocs\",\"_type\":\"relateddoc\",\"_id\":\"2\",\"_version\":1,\"found\":true, \"_source\" : {\"channel\":\"de\",\"site\":\"amazon\",\"type\":\"compass\",\"md5\":\"71a5120bdf4d998c2b043a681b1bd211\"}},{\"_index\":\"relateddocs\",\"_type\":\"relateddoc\",\"_id\":\"3\",\"found\":false}]}";
    private final String FOUND_NO_DOCS = "{}";
    private final String FOUND_NO_DOCS_404 = "<html lang=en>  <meta charset=utf-8>  <meta name=viewport content=\"initial-scale=1, minimum-scale=1, width=device-width\">  <title>Error 404 (Not Found)!!1</title>  <body>404</body></html>";

    public static int[] ports;

    public static int findFreePort() {
        try {
            ServerSocket server = new ServerSocket(0);
            int port = server.getLocalPort();
            server.close();
            return port;
        } catch (Exception e) {
            return 9999;
        }
    }

    static {
        ports = new int[] { findFreePort(),findFreePort(),findFreePort() };
    }

    @Rule
    public WireMockRule wireMockRule1 = new WireMockRule(ports[0]);

    @Rule
    public WireMockRule wireMockRule2 = new WireMockRule(ports[1]);

    @Rule
    public WireMockRule wireMockRule3 = new WireMockRule(ports[2]);

    RelatedItemGetRepository repository;
    Configuration configuration;
    WireMock wireMock1;
    WireMock wireMock2;
    WireMock wireMock3;

    @Before
    public void setUp() {

        System.setProperty(ConfigurationConstants.PROPNAME_DOCUMENT_INDEXING_ENABLED,"true");
        System.setProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_HTTP_REQUEST_TIMEOUT_MS,"1000");
        configuration = new SystemPropertiesConfiguration();

        String urlPath = HttpUtil.createMGetHttpEnpointUrlPath(configuration);
        wireMock1 = new WireMock("localhost", ports[0]);
        wireMock1.register(post(urlMatching(urlPath))
                .willReturn(aResponse()
                        .withBody(FOUND_TWO).withStatus(200))
        );

        wireMock2 = new WireMock("localhost", ports[1]);
        wireMock2.register(post(urlMatching(urlPath))
                .willReturn(aResponse()
                        .withBody(FOUND_ONE_ONE_WITH_ERROR).withStatus(200)));

        wireMock3 = new WireMock("localhost", ports[2]);
        wireMock3.register(post(urlMatching(urlPath))
                .willReturn(aResponse()
                        .withBody(FOUND_TWO_ONE_NOT_FOUND).withStatus(200)));


        wireMock3.register(post(urlMatching(urlPath)).withRequestBody(containing("[\"5\",\"6\"]"))
                .willReturn(aResponse()
                        .withBody(FOUND_NO_DOCS).withStatus(404)));

        wireMock3.register(post(urlMatching(urlPath)).withRequestBody(containing("[\"9\",\"10\"]"))
                .willReturn(aResponse()
                        .withBody(FOUND_NO_DOCS_404).withStatus(404)));


        wireMock3.register(post(urlMatching(urlPath)).withRequestBody(containing("[\"11\",\"12\"]"))
                .willReturn(aResponse()
                        .withStatus(500)));

        wireMock3.register(post(urlMatching(urlPath)).withRequestBody(containing("[\"14\",\"15\"]"))
                .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        wireMock3.register(post(urlMatching(urlPath)).withRequestBody(containing("[\"22\",\"23\"]"))
                .willReturn(aResponse().withFixedDelay(5000)));

        wireMock3.register(post(urlMatching(urlPath)).withRequestBody(containing("[\"27\",\"28\"]"))
                .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));


    }

    @After
    public void tearDown() {
        System.clearProperty(ConfigurationConstants.PROPNAME_DOCUMENT_INDEXING_ENABLED);
        System.clearProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_HTTP_HOSTS);
        System.clearProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_HTTP_REQUEST_TIMEOUT_MS);

        if(repository!=null) {
            repository.shutdown();
        }

        wireMock1.shutdown();
        wireMock2.shutdown();
        wireMock3.shutdown();
    }



    @Test
    public void parseFoundTwoSources() {
        Map<String,String> parsedDoc = ElasticSearchRelatedItemHttpGetRepository.processResults(FOUND_TWO);

        assertTrue(parsedDoc.size()==2);

        assertTrue(parsedDoc.containsKey("1"));
        assertTrue(parsedDoc.containsKey("2"));

        assertTrue(parsedDoc.get("1").contains("a8f346c5ddbbd8d438bc40f0049cc7f8"));
        assertTrue(parsedDoc.get("2").contains("71a5120bdf4d998c2b043a681b1bd211"));
    }

    @Test
    public void testHttpParseFoundTwoSources() {
        System.setProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_HTTP_HOSTS, "http://localhost:" + ports[0]);
        Configuration configuration = new SystemPropertiesConfiguration();


        HttpElasticSearchClientFactory factory = new AHCHttpElasticSearchClientFactory(configuration);
        repository = new ElasticSearchRelatedItemHttpGetRepository(configuration,factory);

        Map<String,String> map = repository.getRelatedItemDocument(new String[]{"1","2"});

        assertEquals("Should have parsed to items",3,map.size());

        assertTrue("Document id 1 contains correct hash values",map.get("1").contains("a8f346c5ddbbd8d438bc40f0049cc7f8"));
        assertTrue("Document id 2 contains correct hash values",map.get("2").contains("71a5120bdf4d998c2b043a681b1bd211"));
    }


    @Test
    public void parseFoundOneAndOneWithError() {
        Map<String,String> parsedDoc = ElasticSearchRelatedItemHttpGetRepository.processResults(FOUND_ONE_ONE_WITH_ERROR);

        assertTrue(parsedDoc.size()==1);

        assertTrue(parsedDoc.containsKey("1"));

        assertTrue(parsedDoc.get("1").contains("a8f346c5ddbbd8d438bc40f0049cc7f8"));
    }

    @Test
    public void testHttpParseFoundTwoSourcesOneWithAnError() {
        System.setProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_HTTP_HOSTS, "http://localhost:" + ports[1]);
        Configuration configuration = new SystemPropertiesConfiguration();


        HttpElasticSearchClientFactory factory = new AHCHttpElasticSearchClientFactory(configuration);
        repository = new ElasticSearchRelatedItemHttpGetRepository(configuration,factory);

        Map<String,String> map = repository.getRelatedItemDocument(new String[]{"1","2"});

        assertEquals("Should have parsed to items",3,map.size());

        assertTrue("Document id 1 contains correct hash values",map.get("1").contains("a8f346c5ddbbd8d438bc40f0049cc7f8"));
        assertTrue("Document id 2 contains correct hash values",map.get("2").contains("{}"));
    }

    @Test
    public void parseFoundTwoAndOneNotFound() {
        Map<String,String> parsedDoc = ElasticSearchRelatedItemHttpGetRepository.processResults(FOUND_TWO_ONE_NOT_FOUND);

        assertTrue(parsedDoc.size()==2);

        assertTrue(parsedDoc.containsKey("1"));
        assertTrue(parsedDoc.containsKey("2"));

        assertTrue(parsedDoc.get("1").contains("a8f346c5ddbbd8d438bc40f0049cc7f8"));
        assertTrue(parsedDoc.get("2").contains("71a5120bdf4d998c2b043a681b1bd211"));
    }

    @Test
    public void testHttpParseFoundThreeSourcesOneWithAnError() {
        System.setProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_HTTP_HOSTS, "http://localhost:" + ports[2]);
        Configuration configuration = new SystemPropertiesConfiguration();


        HttpElasticSearchClientFactory factory = new AHCHttpElasticSearchClientFactory(configuration);
        repository = new ElasticSearchRelatedItemHttpGetRepository(configuration,factory);

        Map<String,String> map = repository.getRelatedItemDocument(new String[]{"1","2","3"});

        assertEquals("Should have parsed to items", 4, map.size());

        assertTrue("Document id 1 contains correct hash values",map.get("1").contains("a8f346c5ddbbd8d438bc40f0049cc7f8"));
        assertTrue("Document id 2 contains correct hash values", map.get("2").contains("71a5120bdf4d998c2b043a681b1bd211"));
        assertEquals("Document id 3 contains no source","{}",map.get("3"));
    }

    @Test
    public void testHttpParseWithNoDocsReturned() {
        System.setProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_HTTP_HOSTS, "http://localhost:" + ports[2]);
        Configuration configuration = new SystemPropertiesConfiguration();


        HttpElasticSearchClientFactory factory = new AHCHttpElasticSearchClientFactory(configuration);
        repository = new ElasticSearchRelatedItemHttpGetRepository(configuration,factory);

        Map<String,String> map = repository.getRelatedItemDocument(new String[]{"5","6"});

        assertEquals("Should have parsed to items",3,map.size());

        assertEquals("Document id 5 contains no source", "{}", map.get("5"));
        assertEquals("Document id 6 contains no source","{}",map.get("6"));
    }


    @Test
    public void testHttpParseWithNoDocsReturnedOnA404() {
        System.setProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_HTTP_HOSTS, "http://localhost:" + ports[2]);
        Configuration configuration = new SystemPropertiesConfiguration();


        HttpElasticSearchClientFactory factory = new AHCHttpElasticSearchClientFactory(configuration);
        repository = new ElasticSearchRelatedItemHttpGetRepository(configuration,factory);

        Map<String,String> map = repository.getRelatedItemDocument(new String[]{"9","10"});

        assertEquals("Should have parsed to items",3,map.size());

        assertEquals("Document id 9 contains no source","{}",map.get("9"));
        assertEquals("Document id 10 contains no source","{}",map.get("10"));
    }

    @Test
    public void testHttpParseWith500Error() {
        System.setProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_HTTP_HOSTS, "http://localhost:" + ports[2]);
        Configuration configuration = new SystemPropertiesConfiguration();


        HttpElasticSearchClientFactory factory = new AHCHttpElasticSearchClientFactory(configuration);
        repository = new ElasticSearchRelatedItemHttpGetRepository(configuration,factory);

        Map<String,String> map = repository.getRelatedItemDocument(new String[]{"11","12"});

        assertEquals("Should have parsed to items",3,map.size());

        assertEquals("Document id 11 contains no source","{}",map.get("11"));
        assertEquals("Document id 12 contains no source","{}",map.get("12"));
    }

    @Test
    public void testHttpParseWithError() {
        System.setProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_HTTP_HOSTS, "http://localhost:" + ports[2]);
        Configuration configuration = new SystemPropertiesConfiguration();


        HttpElasticSearchClientFactory factory = new AHCHttpElasticSearchClientFactory(configuration);
        repository = new ElasticSearchRelatedItemHttpGetRepository(configuration,factory);

        Map<String,String> map = repository.getRelatedItemDocument(new String[]{"14","15"});

        assertEquals("Should have parsed to items",3,map.size());

        assertEquals("Document id 14 contains no source","{}",map.get("14"));
        assertEquals("Document id 15 contains no source","{}",map.get("15"));
    }

    @Test
    public void testHttpParseWithTimeout() {
        System.setProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_HTTP_HOSTS, "http://localhost:" + ports[2]);
        Configuration configuration = new SystemPropertiesConfiguration();


        HttpElasticSearchClientFactory factory = new AHCHttpElasticSearchClientFactory(configuration);
        repository = new ElasticSearchRelatedItemHttpGetRepository(configuration,factory);

        Map<String,String> map = repository.getRelatedItemDocument(new String[]{"22","23"});

        assertEquals("Should have parsed to items",3,map.size());

        assertEquals("Document id 22 contains no source","{}",map.get("22"));
        assertEquals("Document id 23 contains no source","{}",map.get("23"));
    }

    @Test
    public void testHttpParseWithIOException() {
        System.setProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_HTTP_HOSTS, "http://localhost:" + ports[2]+100000);
        Configuration configuration = new SystemPropertiesConfiguration();


        HttpElasticSearchClientFactory factory = new AHCHttpElasticSearchClientFactory(configuration);
        repository = new ElasticSearchRelatedItemHttpGetRepository(configuration,factory);
        repository.shutdown();
        Map<String,String> map = repository.getRelatedItemDocument(new String[]{"27","28"});

        assertEquals("Should have parsed to items",3,map.size());

        assertEquals("Document id 27 contains no source","{}",map.get("27"));
        assertEquals("Document id 28 contains no source","{}",map.get("28"));
    }
}
