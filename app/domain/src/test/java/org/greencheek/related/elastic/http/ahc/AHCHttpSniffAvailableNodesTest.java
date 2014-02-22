package org.greencheek.related.elastic.http.ahc;


import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.greencheek.related.elastic.http.SniffAvailableNodes;
import org.greencheek.related.util.config.ConfigurationConstants;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.ServerSocket;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by dominictootell on 19/02/2014.
 */
public class AHCHttpSniffAvailableNodesTest {

    public static int[] ports;
    public static AtomicInteger defaultPortNumber = new AtomicInteger(9000);

    public static int findFreePort() {
        try {
            ServerSocket server = new ServerSocket(0);
            int port = server.getLocalPort();
            server.close();
            return port;
        } catch (Exception e) {
            return defaultPortNumber.incrementAndGet();
        }
    }

    static {
        ports = new int[] { findFreePort(),findFreePort() };
    }

    public static String responseBody1 = "{\"ok\":true,\"cluster_name\":\"e09\",\"nodes\":{\"TvWpoPp4Tc2H-e2PR-i-Ng\":" +
            "{\"name\":\"Beetle II\",\"transport_address\":\"inet[/10.0.1.19:9301]\",\"version\":\"0.90.11\"," +
            "\"http_address\":\"inet[/10.0.1.19:9201]\",\"http\":{\"bound_address\":\"inet[/0:0:0:0:0:0:0:0:9201]\"," +
            "\"publish_address\":\"inet[/10.0.1.19:9201]\",\"max_content_length\":\"100mb\",\"max_content_length_in_bytes\":104857600}}}}";

    public static String responseBody2 = "{\n" +
            "   \"ok\":true,\n" +
            "   \"cluster_name\":\"e09\",\n" +
            "   \"nodes\":{\n" +
            "      \"TvWpoPp4Tc2H-e2PR-i-Ng\":{\n" +
            "         \"name\":\"Beetle II\",\n" +
            "         \"transport_address\":\"inet[/10.0.1.19:9301]\",\n" +
            "         \"version\":\"0.90.11\",\n" +
            "         \"http_address\":\"inet[/10.0.1.19:9211]\",\n" +
            "         \"http\":{\n" +
            "            \"bound_address\":\"inet[/0:0:0:0:0:0:0:0%0:9211]\",\n" +
            "            \"publish_address\":\"inet[/10.0.1.19:9211]\",\n" +
            "            \"max_content_length\":\"100mb\",\n" +
            "            \"max_content_length_in_bytes\":104857600\n" +
            "         }\n" +
            "      },\n" +
            "      \"JyoPrsJSR4K1VQxEG2O1aQ\":{\n" +
            "         \"name\":\"Barracuda\",\n" +
            "         \"transport_address\":\"inet[/10.0.1.9:9302]\",\n" +
            "         \"hostname\":\"Dominics-MacBook-Pro.local\",\n" +
            "         \"version\":\"0.90.11\",\n" +
            "         \"http_address\":\"inet[/10.0.1.9:9255]\",\n" +
            "         \"http\":{\n" +
            "            \"bound_address\":\"inet[/0:0:0:0:0:0:0:0:9255]\",\n" +
            "            \"publish_address\":\"inet[/10.0.1.9:9255]\",\n" +
            "            \"max_content_length\":\"100mb\",\n" +
            "            \"max_content_length_in_bytes\":104857600\n" +
            "         }\n" +
            "      }\n" +
            "   }\n" +
            "}";

    @Rule
    public WireMockRule wireMockRule1 = new WireMockRule(ports[0]); // No-args constructor defaults to port 8080

    @Rule
    public WireMockRule wireMockRule2 = new WireMockRule(ports[1]);


    private SniffAvailableNodes nodeSniffer;

    @Before
    public void setUp() {

        WireMock wireMock = new WireMock("localhost", ports[0]);
        wireMock.register(get(urlMatching(ConfigurationConstants.DEFAULT_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENDPOINT))
                .willReturn(aResponse()
                        .withBody(responseBody1).withStatus(200))
        );

        wireMock = new WireMock("localhost", ports[1]);
        wireMock.register(get(urlMatching(ConfigurationConstants.DEFAULT_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENDPOINT))
                .willReturn(aResponse()
                        .withBody(responseBody2).withStatus(200)));

        nodeSniffer = new AHCHttpSniffAvailableNodes(new SystemPropertiesConfiguration());
    }


    @After
    public void tearDown() {
        nodeSniffer.shutdown();
    }


    @Test
    public void testOneHostIsContacted() {
        Set<String> hosts = nodeSniffer.getAvailableNodes(new String[]{"http://localhost:"+ports[0]});

        assertEquals("Should be one host",1,hosts.size());

        assertEquals("parsed host should be: http://10.0.1.19:9201","http://10.0.1.19:9201",(String)hosts.toArray()[0]);

        wireMockRule1.verify(1,getRequestedFor(urlMatching(ConfigurationConstants.DEFAULT_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENDPOINT)));
        wireMockRule2.verify(0,getRequestedFor(urlMatching(ConfigurationConstants.DEFAULT_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENDPOINT)));
    }

    @Test
    public void testBothHostIsContacted() {
        Set<String> hosts = nodeSniffer.getAvailableNodes(new String[]{"http://localhost:"+ports[0],"http://localhost:"+ports[1]});

        assertEquals("Should be one host",3,hosts.size());

        assertEquals("parsed host should be: http://10.0.1.19:9201","http://10.0.1.19:9201",(String)hosts.toArray()[0]);
        assertEquals("parsed host should be: http://10.0.1.19:9211","http://10.0.1.19:9211",(String)hosts.toArray()[1]);
        assertEquals("parsed host should be: http://10.0.1.9:9255","http://10.0.1.9:9255",(String)hosts.toArray()[2]);

        wireMockRule1.verify(1,getRequestedFor(urlMatching(ConfigurationConstants.DEFAULT_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENDPOINT)));
        wireMockRule2.verify(1,getRequestedFor(urlMatching(ConfigurationConstants.DEFAULT_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENDPOINT)));
    }



}
