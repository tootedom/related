package org.greencheek.related.elastic.http.ahc;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpProviderConfig;
import com.ning.http.client.providers.netty.NettyAsyncHttpProviderConfig;
import org.greencheek.related.util.concurrency.DefaultNameableThreadFactory;
import org.greencheek.related.util.config.Configuration;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

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

        AsyncHttpClientConfig.Builder cf = createClientConfig(configuration).setMaximumConnectionsTotal(numberOfHostsBeingConnectedTo);
        cf.setExecutorService(getExecutorService(numberOfHostsBeingConnectedTo));

        return createClient(cf);
    }

    public static AsyncHttpClient createClient(AsyncHttpClientConfig.Builder config) {
        return new AsyncHttpClient(config.build());
    }

    public static AsyncHttpClientConfig.Builder createClientConfig(Configuration configuration) {
        AsyncHttpProviderConfig providerConfig =  new NettyAsyncHttpProviderConfig();
        providerConfig.addProperty(NettyAsyncHttpProviderConfig.USE_BLOCKING_IO,true);

        AsyncHttpClientConfig.Builder cf = new AsyncHttpClientConfig.Builder();
        cf.setAsyncHttpClientProviderConfig(providerConfig);
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
        return newFixedThreadPool(numberOfHostsBeingConnectedTo,new DefaultNameableThreadFactory("EsHttpSearchExecutor"));
    }
}
