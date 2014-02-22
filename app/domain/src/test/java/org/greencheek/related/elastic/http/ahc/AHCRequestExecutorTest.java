package org.greencheek.related.elastic.http.ahc;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.io.stream.ByteBufferStreamInput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.rest.XContentRestResponse;
import org.greencheek.related.elastic.http.HttpSearchExecutionStatus;
import org.greencheek.related.elastic.http.HttpMethod;
import org.greencheek.related.elastic.http.HttpResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Created by dominictootell on 17/02/2014.
 */
public class AHCRequestExecutorTest {



    public static int port = findFreePort();

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

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(port); // No-args constructor defaults to port 8080

    AsyncHttpClient client;

    @Before
    public void setUp() {
        AsyncHttpClientConfig.Builder cf = new AsyncHttpClientConfig.Builder();
        cf.setRequestTimeoutInMs(2000);
        cf.setConnectionTimeoutInMs(1000);
        client = new AsyncHttpClient(cf.build());
    }

    @After
    public void tearDown() {
        client.close();
    }

    @Test
    public void testRequestTimeoutIsCaught() {
        stubFor(get(urlEqualTo("/my/resource"))
                .willReturn(aResponse().withFixedDelay(3000)
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));


        HttpResult httpResult = AHCRequestExecutor.executeSearch(client, HttpMethod.GET,"http://localhost:"+port,"/my/resource",null);

        assertTrue(httpResult.getStatus()== HttpSearchExecutionStatus.REQUEST_TIMEOUT);
    }

    @Test
    public void testResponseBodyIsReturned() {
        String response = "<response>Some content</response>";
        stubFor(get(urlEqualTo("/my/resource"))
                .willReturn(aResponse().withFixedDelay(1000)
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(response)));

        HttpResult httpResult = AHCRequestExecutor.executeSearch(client, HttpMethod.GET,"http://localhost:"+port,"/my/resource",null);

        assertTrue(httpResult.getStatus()== HttpSearchExecutionStatus.OK);

        assertEquals(response,httpResult.getResult());
    }

    @Test
    public void testMultiSearch() {
        AsyncHttpClient client = new AsyncHttpClient();

        String s = "{\n" +
                "  \"size\" : 0,\n" +
                "  \"timeout\" : 5000,\n" +
                "  \"query\" : {\n" +
                "    \"constant_score\" : {\n" +
                "      \"filter\" : {\n" +
                "        \"bool\" : {\n" +
                "          \"must\" : {\n" +
                "            \"term\" : {\n" +
                "              \"related-with\" : \"9da7320e-acd7-4f49-9b3f-4818f4afc67b\"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"facets\" : {\n" +
                "    \"frequently-related-with\" : {\n" +
                "      \"terms\" : {\n" +
                "        \"field\" : \"id\",\n" +
                "        \"size\" : 10,\n" +
                "        \"execution_hint\" : \"map\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n";

        HttpResult httpResult = AHCRequestExecutor.executeSearch(client, HttpMethod.GET,"http://localhost:9200","/test/type1/_search",s);

        System.out.println(httpResult.getResult());

        try {
            SearchResponse r = SearchResponse.readSearchResponse(new ByteBufferStreamInput(ByteBuffer.wrap(httpResult.getResult().getBytes())));
            System.out.println(r);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testConnectionException() {
        HttpResult httpResult = AHCRequestExecutor.executeSearch(client, HttpMethod.GET,"http://localhost:9999","/",null);
        assertTrue(httpResult.getStatus()== HttpSearchExecutionStatus.CONNECTION_FAILURE);
    }

//    /**
//     * requires: sudo route add -host 1.1.1.1 127.0.0.1
//     * to remove: sudo route delete -host 1.1.1.1 127.0.0.1
//     */
//    @Test
//    public void connectionTimeout() {
//        HttpResult httpResult = AHCRequestExecutor.executeSearch(client, HttpMethod.GET,"http://1.1.1.1:9999","/",null);
//        assertTrue(httpResult.getStatus()== HttpSearchExecutionStatus.CONNECTION_FAILURE);
//    }


}
