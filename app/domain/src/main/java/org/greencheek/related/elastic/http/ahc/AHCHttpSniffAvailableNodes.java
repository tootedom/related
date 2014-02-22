package org.greencheek.related.elastic.http.ahc;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import org.greencheek.related.elastic.http.HttpMethod;
import org.greencheek.related.elastic.http.HttpResult;
import org.greencheek.related.elastic.http.HttpSearchExecutionStatus;
import org.greencheek.related.elastic.http.SniffAvailableNodes;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.parsing.HostParsingUtil;

import java.util.Set;
import java.util.TreeSet;

/**
 * Iterates one at a time over a set of es nodes, to obtain the http endpoints that are
 * available.
 *
 * Created by dominictootell on 17/02/2014.
 */
public class AHCHttpSniffAvailableNodes implements SniffAvailableNodes {

    public final String NODE_ENDPOINT;

    private static final int MAX_CONNECTIONS = 1;
    private final AsyncHttpClient client;


    public AHCHttpSniffAvailableNodes(Configuration configuration) {
        this.NODE_ENDPOINT = configuration.getElasticSearchNodesSniffingHttpAdminEndpoint();
        AsyncHttpClientConfig.Builder cf = AHCFactory.createClientConfig(configuration);
        cf.setMaximumConnectionsTotal(MAX_CONNECTIONS);
        cf.setMaximumConnectionsPerHost(MAX_CONNECTIONS);
        cf.setAllowPoolingConnection(false);
        cf.setConnectionTimeoutInMs(configuration.getElasticSearchNodeSniffingHttpConnectionTimeoutMs());
        cf.setRequestTimeoutInMs(configuration.getElasticSearchNodeSniffingHttpRequestTimeoutMs());
        client = AHCFactory.createClient(cf);

    }

    @Override
    public Set<String> getAvailableNodes(String[] hosts) {
        Set<String> newHosts = new TreeSet<>();

        for(String host : hosts) {
            HttpResult result = AHCRequestExecutor.executeSearch(client, HttpMethod.GET, host, NODE_ENDPOINT, null);
            if(result.getStatus()== HttpSearchExecutionStatus.OK) {
                newHosts.addAll(HostParsingUtil.parseAvailablePublishedHttpServerAddresses(result.getResult()));
            }
        }
        return newHosts;
    }

    @Override
    public void shutdown() {
        client.close();
    }
}
