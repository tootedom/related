package org.greencheek.related.elastic.http.ahc;

import org.greencheek.related.elastic.http.HttpElasticClient;
import org.greencheek.related.elastic.http.HttpElasticSearchClientFactory;
import org.greencheek.related.elastic.http.SniffAvailableNodes;
import org.greencheek.related.util.config.Configuration;

import java.util.List;

/**
 * Created by dominictootell on 07/02/2014.
 */
public class AHCHttpElasticSearchClientFactory implements HttpElasticSearchClientFactory {

    // This class rotates the Servers.
    private final Configuration config;

    public AHCHttpElasticSearchClientFactory(Configuration config) {
        this.config = config;
    }

    @Override
    public HttpElasticClient getClient() {
        // This returns the round robin load balancing client.

        return new AHCRoundRobinHttpElasticClient(config,new AHCHttpSniffAvailableNodes(config));
    }


}
