package org.greencheek.related.elastic.http.ahc;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import org.greencheek.related.util.config.Configuration;

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
        return createClient(cf);
    }

    public static AsyncHttpClient createClient(AsyncHttpClientConfig.Builder config) {
        return new AsyncHttpClient(config.build());
    }

    public static AsyncHttpClientConfig.Builder createClientConfig(Configuration configuration) {
        AsyncHttpClientConfig.Builder cf = new AsyncHttpClientConfig.Builder();
        cf.setCompressionEnabled(configuration.getElasticSearchHttpCompressionEnabled());
        cf.setConnectionTimeoutInMs(configuration.getElasticSearchHttpConnectionTimeoutMs());
        cf.setFollowRedirects(configuration.getElasticSearchHttpFollowRedirects());
        cf.setMaximumConnectionsPerHost(1);
        cf.setMaxRequestRetry(configuration.getElasticSearchHttpNoOfRetries());
        cf.setAllowPoolingConnection(configuration.getElasticSearchHttpConnectionPoolingEnabled());
        cf.setRequestTimeoutInMs(configuration.getElasticSearchHttpRequestTimeoutMs());
        return cf;
    }
}
