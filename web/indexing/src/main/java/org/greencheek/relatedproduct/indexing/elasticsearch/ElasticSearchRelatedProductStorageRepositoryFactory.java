package org.greencheek.relatedproduct.indexing.elasticsearch;

import org.greencheek.relatedproduct.elastic.ElasticSearchClientFactory;
import org.greencheek.relatedproduct.elastic.NodeBasedElasticSearchClientFactory;
import org.greencheek.relatedproduct.elastic.TransportBasedElasticSearchClientFactory;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepositoryFactory;
import org.greencheek.relatedproduct.util.config.Configuration;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 15/06/2013
 * Time: 12:45
 * To change this template use File | Settings | File Templates.
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
