package org.greencheek.related.elastic.http.ahc;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.ning.http.client.AsyncHttpClient;
import org.greencheek.related.elastic.http.SniffAvailableNodes;
import org.greencheek.related.util.concurrency.DefaultNameableThreadFactory;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.ConfigurationConstants;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.ServerSocket;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;


/**
 * Created by dominictootell on 19/02/2014.
 */
public class ServerListSniffingRunnableTest{

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

    public static String responseBody = "{\n" +
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
            "            \"publish_address\":\"inet[/localhost:9255]\",\n" +
            "            \"max_content_length\":\"100mb\",\n" +
            "            \"max_content_length_in_bytes\":104857600\n" +
            "         }\n" +
            "      }\n" +
            "   }\n" +
            "}";

    public static String responseBody2 = "{\n" +
            "   \"ok\":true,\n" +
            "   \"cluster_name\":\"e09\",\n" +
            "   \"nodes\":{\n" +
            "      \"TvWpoPp4Tc2H-e2PR-i-Ng\":{\n" +
            "         \"name\":\"Beetle II\",\n" +
            "         \"transport_address\":\"inet[/10.0.1.19:9301]\",\n" +
            "         \"version\":\"0.90.11\",\n" +
            "         \"http_address\":\"inet[/localhost:"+ports[1]+"]\",\n" +
            "         \"http\":{\n" +
            "            \"bound_address\":\"inet[/0:0:0:0:0:0:0:0%0:"+ports[1]+"]\",\n" +
            "            \"publish_address\":\"inet[/localhost:"+ports[1]+"]\",\n" +
            "            \"max_content_length\":\"100mb\",\n" +
            "            \"max_content_length_in_bytes\":104857600\n" +
            "         }\n" +
            "      }"+
            "   }\n" +
            "}";


    @Rule
    public WireMockRule wireMockRule = new WireMockRule(ports[0]);

    @Rule
    public WireMockRule wireMockRule2 = new WireMockRule(ports[1]);

    private SniffAvailableNodes nodeSniffer;

    private AsyncHttpClient initialClient;

    @Before
    public void setUp() {

        WireMock wireMock = new WireMock("localhost", ports[0]);
        wireMock.register(get(urlMatching(ConfigurationConstants.DEFAULT_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENDPOINT))
                .willReturn(aResponse()
                        .withBody(responseBody).withStatus(200))
        );

        wireMock = new WireMock("localhost", ports[1]);
        wireMock.register(get(urlMatching(ConfigurationConstants.DEFAULT_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENDPOINT))
                .willReturn(aResponse()
                        .withBody(responseBody2).withStatus(200))
        );


        nodeSniffer = new AHCHttpSniffAvailableNodes(new SystemPropertiesConfiguration());

        initialClient = new AsyncHttpClient();
    }


    @After
    public void tearDown() {
        nodeSniffer.shutdown();
        initialClient.close();
    }


    @Test
    public void testHostIsContacted() {

        Configuration config = new SystemPropertiesConfiguration();
        AtomicReference<ServerList> ref = new AtomicReference<ServerList>();
        PowerOfTwoServerList list = new PowerOfTwoServerList(initialClient,new String[]{"http://localhost:"+ports[0]});
        ref.set(list);

        String hosts = list.getHostsStringIndentifier();
        String[] hostList = list.getHostList();

        CustomServerListSniffingRunnable runnable = new CustomServerListSniffingRunnable(nodeSniffer,ref,config);
        CountDownLatch latch = runnable.latch;

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, new DefaultNameableThreadFactory("ESHttpClientSniffingScheduler"));
        scheduledExecutorService.scheduleWithFixedDelay(runnable,0,15, TimeUnit.MINUTES);

        try {
            boolean ok = latch.await(5000,TimeUnit.MILLISECONDS);
            assertTrue("scheduler should have run", ok);
            assertFalse("Host lists should have changed", !hosts.equals(list.getHostsStringIndentifier()));
            wireMockRule.verify(1,getRequestedFor(urlMatching(ConfigurationConstants.DEFAULT_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENDPOINT)));

            assertEquals("Should be 2 nodes available for load balancing", 2, ref.get().getNumberOfHosts());
        } catch(Exception e) {
            fail("exception whilst calling the scheduler");
        }
        finally {
            scheduledExecutorService.shutdownNow();
        }
    }

    @Test
    public void testHostIsContactedButServerListIsNotModified() {

        Configuration config = new SystemPropertiesConfiguration();
        AtomicReference<ServerList> ref = new AtomicReference<ServerList>();
        PowerOfTwoServerList list = new PowerOfTwoServerList(initialClient,new String[]{"http://localhost:"+ports[1]});
        ref.set(list);

        String hosts = list.getHostsStringIndentifier();
        String[] hostList = list.getHostList();

        CustomServerListSniffingRunnable runnable = new CustomServerListSniffingRunnable(nodeSniffer,ref,config);
        CountDownLatch latch = runnable.latch;

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, new DefaultNameableThreadFactory("ESHttpClientSniffingScheduler"));
        scheduledExecutorService.scheduleWithFixedDelay(runnable,0,15, TimeUnit.MINUTES);

        try {
            boolean ok = latch.await(8000,TimeUnit.MILLISECONDS);
            assertTrue("scheduler should have run", ok);
            wireMockRule.verify(0,getRequestedFor(urlMatching(ConfigurationConstants.DEFAULT_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENDPOINT)));
            wireMockRule2.verify(1,getRequestedFor(urlMatching(ConfigurationConstants.DEFAULT_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENDPOINT)));

            assertTrue("Host lists should not have been changed",hosts.equals(list.getHostsStringIndentifier()));
            assertSame("Host lists should not have been changed",list,ref.get());

            assertEquals("Should be 1 nodes available for load balancing", 1, ref.get().getNumberOfHosts());
        } catch(Exception e) {
            fail("exception whilst calling the scheduler");
        }
        finally {
            scheduledExecutorService.shutdownNow();
        }
    }

    private class CustomServerListSniffingRunnable extends ServerListSniffingRunnable {

        public CountDownLatch latch = new CountDownLatch(1);

        public CustomServerListSniffingRunnable(SniffAvailableNodes nodesSniffer, AtomicReference<ServerList> serverListReferenceToUpdate, Configuration configuration) {
            super(nodesSniffer, serverListReferenceToUpdate, configuration);
        }

        public void run() {
            super.run();
            latch.countDown();
        }
    }

}
