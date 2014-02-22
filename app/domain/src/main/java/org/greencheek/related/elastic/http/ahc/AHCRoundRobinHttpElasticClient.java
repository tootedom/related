package org.greencheek.related.elastic.http.ahc;

import com.ning.http.client.AsyncHttpClient;
import org.greencheek.related.elastic.http.*;
import org.greencheek.related.util.concurrency.DefaultNameableThreadFactory;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.parsing.HostParsingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by dominictootell on 16/02/2014.
 * This class is not thread safe.  It is to be used one in a single threaded manner
 */
public class AHCRoundRobinHttpElasticClient implements HttpElasticClient {
    private static final Logger log = LoggerFactory.getLogger(AHCRoundRobinHttpElasticClient.class);

    private final ScheduledExecutorService scheduledExecutorService;

    private final AtomicReference<ServerList> httpConnection = new AtomicReference<ServerList>();
    private final SniffAvailableNodes nodeSniffer;
    private final boolean nodeSniffingEnabled;

    public AHCRoundRobinHttpElasticClient(Configuration config, SniffAvailableNodes nodeSniffer) {
        this.nodeSniffer = nodeSniffer;
        this.nodeSniffingEnabled = config.getElasticSearchNodesSniffingEnabled();
        String[] hosts = HostParsingUtil.parseHttpHosts(config);
        AsyncHttpClient client = createClient(config,hosts);

        httpConnection.set(new PowerOfTwoServerList(client,hosts));

        if(nodeSniffingEnabled) {
            scheduledExecutorService = Executors.newScheduledThreadPool(1, new DefaultNameableThreadFactory("ESHttpClientSniffingScheduler"));
            scheduledExecutorService.scheduleWithFixedDelay(new ServerListSniffingRunnable(nodeSniffer,httpConnection,config),
                config.getElasticSearchNodesSniffingHttpRetryInterval(),config.getElasticSearchNodesSniffingHttpRetryInterval(), config.getElasticSearchNodesSniffingRetryIntervalUnit());
        } else {
            scheduledExecutorService = null;
        }
    }


    @Override
    public HttpResult executeSearch(HttpMethod method, String path, String searchQuery) {
        ServerList server = httpConnection.get();
        String host = server.getHost();
        AsyncHttpClient client = server.getClient();
        HttpResult result = HttpResult.REQUEST_FAILURE;
        HttpResult res = AHCRequestExecutor.executeSearch(client, method, host, path, searchQuery);
        HttpSearchExecutionStatus status = res.getStatus();

        // The revalidation node sniffer may have switched out the client on us
        if(nodeSniffingEnabled && status == HttpSearchExecutionStatus.CLIENT_CLOSED) {
            server = httpConnection.get();
            host = server.getHost();
            client = server.getClient();
            result = HttpResult.REQUEST_FAILURE;
            res = AHCRequestExecutor.executeSearch(client, method, host, path, searchQuery);
            status = res.getStatus();
        }

        if(status == HttpSearchExecutionStatus.CONNECTION_FAILURE) {
            for(int i=1;i<server.getNumberOfHosts();i++) {
                host = server.getHost();
                res = AHCRequestExecutor.executeSearch(client, method, host, path, searchQuery);
                if(res.getStatus() != HttpSearchExecutionStatus.CONNECTION_FAILURE) {
                    result = res;
                    break;
                }
            }
            return result;
        } else {
            return res;
        }
    }




    @Override
    public HttpResult executeSearch(HttpMethod method, String path) {
        return executeSearch(method,path,null);
    }



    @Override
    public void shutdown() {
        if(nodeSniffingEnabled && scheduledExecutorService!=null) {
            scheduledExecutorService.shutdownNow();
            nodeSniffer.shutdown();
        }
        httpConnection.get().getClient().close();
    }


    private AsyncHttpClient createClient(Configuration config,String[] hosts) {
        return AHCFactory.createClient(config, hosts.length);
    }



}
