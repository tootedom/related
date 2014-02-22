package org.greencheek.related.searching.repository;

import org.greencheek.related.elastic.ElasticSearchClientFactory;
import org.greencheek.related.elastic.NodeBasedElasticSearchClientFactory;
import org.greencheek.related.elastic.TransportBasedElasticSearchClientFactory;
import org.greencheek.related.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 15/12/2013
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
public class NodeOrTransportBasedElasticSearchClientFactoryCreator implements ElasticSearchClientFactoryCreator {

    public static final ElasticSearchClientFactoryCreator INSTANCE = new NodeOrTransportBasedElasticSearchClientFactoryCreator();
    @Override
    public ElasticSearchClientFactory getElasticSearchClientConnectionFactory(Configuration configuration) {
        ElasticSearchClientFactory factory;
        switch(configuration.getElasticSearchClientType()) {
            case NODE:
                factory = new NodeBasedElasticSearchClientFactory(configuration);
                break;
            case TRANSPORT:
                factory = new TransportBasedElasticSearchClientFactory(configuration);
                break;
            default:
                factory = new TransportBasedElasticSearchClientFactory(configuration);
                break;
        }
        return factory;
    }
}