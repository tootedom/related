package org.greencheek.related.searching.repository;

import org.greencheek.related.elastic.ElasticSearchClientFactory;
import org.greencheek.related.elastic.http.HttpElasticSearchClientFactory;
import org.greencheek.related.searching.RelatedItemGetRepository;
import org.greencheek.related.searching.RelatedItemGetRepositoryFactory;
import org.greencheek.related.searching.RelatedItemSearchRepository;
import org.greencheek.related.searching.repository.http.ElasticSearchFrequentlyRelatedItemHttpSearchProcessor;
import org.greencheek.related.searching.repository.http.ElasticSearchRelatedItemHttpSearchRepository;
import org.greencheek.related.util.config.Configuration;

/**
 * Created by dominictootell on 05/03/2014.
 */
public class NodeTransportOrHttpGetRepositoryFactory implements RelatedItemGetRepositoryFactory {

    private final HttpElasticSearchClientFactory httpFactory;
    private final ElasticSearchClientFactoryCreator nodeOrTransportFactory;

    public NodeTransportOrHttpGetRepositoryFactory(ElasticSearchClientFactoryCreator nodeOrTransportFactory,
                                                   HttpElasticSearchClientFactory httpFactory) {
        this.nodeOrTransportFactory = nodeOrTransportFactory;
        this.httpFactory = httpFactory;
    }

    @Override
    public RelatedItemGetRepository createRelatedItemGetRepository(Configuration configuration) {
        RelatedItemGetRepository respository = null;

        switch(configuration.getElasticSearchClientType()) {
            case HTTP:
//                respository =
//                        new ElasticSearchRelatedItemHttpSearchRepository(configuration,httpFactory);
                break;
            default:
                ElasticSearchClientFactory factory = nodeOrTransportFactory.getElasticSearchClientConnectionFactory(configuration);

        }


        return respository;
    }
}
