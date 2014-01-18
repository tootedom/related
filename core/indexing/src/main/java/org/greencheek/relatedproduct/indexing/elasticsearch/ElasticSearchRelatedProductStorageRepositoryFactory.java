package org.greencheek.relatedproduct.indexing.elasticsearch;

import org.greencheek.relatedproduct.elastic.ElasticSearchClientFactory;
import org.greencheek.relatedproduct.elastic.NodeBasedElasticSearchClientFactory;
import org.greencheek.relatedproduct.elastic.TransportBasedElasticSearchClientFactory;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepositoryFactory;
import org.greencheek.relatedproduct.util.config.Configuration;


/**
 * Creates a new elastic search storage repository (client connection to an elastic search cluster) when
 * {@link #getRepository(org.greencheek.relatedproduct.util.config.Configuration)} is called.
 */
public class ElasticSearchRelatedProductStorageRepositoryFactory implements RelatedProductStorageRepositoryFactory {

    private final Configuration configuration;

    public ElasticSearchRelatedProductStorageRepositoryFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public RelatedProductStorageRepository getRepository(Configuration configuration) {
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

        return new ElasticSearchRelatedProductIndexingRepository(configuration,factory);
    }
}
