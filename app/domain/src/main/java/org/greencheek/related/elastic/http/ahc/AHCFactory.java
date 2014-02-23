package org.greencheek.related.elastic.http.ahc;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpProviderConfig;
import com.ning.http.client.providers.netty.NettyAsyncHttpProviderConfig;
import org.greencheek.related.util.concurrency.DefaultNameableThreadFactory;
import org.greencheek.related.util.config.Configuration;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Given a configuration object, creates a AsyncHttpClient object
 * Created by dominictootell on 17/02/2014.
 */
public class AHCFactory {

    /**
     * Creates a AsyncHttpClient object that can be used for talking to elasticsearch
     *
     * @param configuration The configuration object containing properties for configuring the http connections
     * @param numberOfHostsBeingConnectedTo the number of hosts that are currently known about in the es cluster
     *
     * @return
     */
    public static AsyncHttpClient createClient(Configuration configuration, int numberOfHostsBeingConnectedTo) {


        AsyncHttpClientConfig.Builder cf = createClientConfig(configuration);
        // A Bug exists in the AsyncConnection library that leak permits on a
        // Connection exception (i.e. when host not listening .. hard fail)
        // So we do not enable connection tracking.  Which is fine as the ring
        // buffer does the job of having a single thread talking to the backend repo (ES)
        // So the connection should not grow in an unmanaged way, as the ring buffer
        // is restricting the connections
        // cf.setMaximumConnectionsTotal(numberOfHostsBeingConnectedTo);
        cf.setMaximumConnectionsTotal(-1);
        cf.setMaximumConnectionsPerHost(-1);
        cf.setExecutorService(getExecutorService(numberOfHostsBeingConnectedTo));

        return createClient(cf);
    }

    public static AsyncHttpClient createClient(AsyncHttpClientConfig.Builder config) {
        return new AsyncHttpClient(config.build());
    }

    public static AsyncHttpClientConfig.Builder createClientConfig(Configuration configuration) {
        AsyncHttpProviderConfig providerConfig =  new NettyAsyncHttpProviderConfig();
        // We use nio, instead of having a thread per backend ES server node.
        // providerConfig.addProperty(NettyAsyncHttpProviderConfig.USE_BLOCKING_IO,true);
        providerConfig.addProperty(NettyAsyncHttpProviderConfig.EXECUTE_ASYNC_CONNECT,false);

        AsyncHttpClientConfig.Builder cf = new AsyncHttpClientConfig.Builder();

        cf.setAsyncHttpClientProviderConfig(providerConfig);
        cf.setIOThreadMultiplier(1);
        cf.setCompressionEnabled(configuration.getElasticSearchHttpCompressionEnabled());
        cf.setConnectionTimeoutInMs(configuration.getElasticSearchHttpConnectionTimeoutMs());
        cf.setFollowRedirects(configuration.getElasticSearchHttpFollowRedirects());
        cf.setMaximumConnectionsPerHost(1);
        cf.setMaxRequestRetry(configuration.getElasticSearchHttpNoOfRetries());
        cf.setAllowPoolingConnection(configuration.getElasticSearchHttpConnectionPoolingEnabled());
        cf.setRequestTimeoutInMs(configuration.getElasticSearchHttpRequestTimeoutMs());

        return cf;
    }

    private static ExecutorService getExecutorService(int numberOfHostsBeingConnectedTo) {
        // Cached thread pool is used.  As we cannot control the number of workers
        // for netty.  Which is determine via the number of processors available on the machine
        // The number of items on the queue of requests for the executor is bounded by the
        // ring buffer.
        //
        //
        return newCachedThreadPool(new DefaultNameableThreadFactory("EsHttpSearchExecutor"));
    }
}
