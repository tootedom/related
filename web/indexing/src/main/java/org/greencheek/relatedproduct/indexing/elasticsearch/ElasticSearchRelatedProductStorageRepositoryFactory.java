package org.greencheek.relatedproduct.indexing.elasticsearch;

import org.greencheek.relatedproduct.elastic.NodeBasedElasticSearchClientFactory;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepositoryFactory;
import org.greencheek.relatedproduct.util.UTCCurrentDateFormatter;
import org.greencheek.relatedproduct.util.config.Configuration;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 15/06/2013
 * Time: 12:45
 * To change this template use File | Settings | File Templates.
 */
@Named
public class ElasticSearchRelatedProductStorageRepositoryFactory implements RelatedProductStorageRepositoryFactory {

    private final Configuration configuration;
    private final UTCCurrentDateFormatter currentDateFormatter;

    @Inject
    public ElasticSearchRelatedProductStorageRepositoryFactory(Configuration configuration,
                                                               UTCCurrentDateFormatter currentDayFormatter) {
        this.configuration = configuration;
        this.currentDateFormatter = currentDayFormatter;

    }

    @Override
    public RelatedProductStorageRepository getRepository() {
        NodeBasedElasticSearchClientFactory factory = new NodeBasedElasticSearchClientFactory();

        return new ElasticSearchRelatedProductIndexingRepository(configuration,currentDateFormatter,factory);
    }
}
