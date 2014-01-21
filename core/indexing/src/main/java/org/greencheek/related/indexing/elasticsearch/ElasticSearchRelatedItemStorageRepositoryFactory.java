package org.greencheek.related.indexing.elasticsearch;

import org.greencheek.related.elastic.ElasticSearchClientFactory;
import org.greencheek.related.elastic.NodeBasedElasticSearchClientFactory;
import org.greencheek.related.elastic.TransportBasedElasticSearchClientFactory;
import org.greencheek.related.indexing.RelatedItemStorageRepository;
import org.greencheek.related.indexing.RelatedItemStorageRepositoryFactory;
import org.greencheek.related.util.config.Configuration;


/**
 * Creates a new elastic search storage repository (client connection to an elastic search cluster) when
 * {@link #getRepository(org.greencheek.related.util.config.Configuration)} is called.
 */
public class ElasticSearchRelatedItemStorageRepositoryFactory implements RelatedItemStorageRepositoryFactory {

    private final Configuration configuration;

    public ElasticSearchRelatedItemStorageRepositoryFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public RelatedItemStorageRepository getRepository(Configuration configuration) {
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

        return new ElasticSearchRelatedItemIndexingRepository(configuration,factory);
    }
}
