package org.greencheek.relatedproduct.util.config;

import org.greencheek.relatedproduct.indexing.RelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepositoryFactory;
import org.greencheek.relatedproduct.indexing.requestprocessorfactory.IndexRequestProcessorFactory;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 02/06/2013
 * Time: 15:03
 * To change this template use File | Settings | File Templates.
 */
@Named
public class SpringApplicationCtx implements ApplicationCtx {

    private final Configuration applicationConfiguration;
    private final IndexRequestProcessorFactory indexingRequestProcessingFactory;
    private final RelatedProductStorageRepositoryFactory storageRepositoryFactory;

    @Inject
    public SpringApplicationCtx(Configuration configuration,
                                IndexRequestProcessorFactory indexingRequestProcessorFactory,
                                RelatedProductStorageRepositoryFactory indexStorageRepositoryFactory)
    {
        this.indexingRequestProcessingFactory = indexingRequestProcessorFactory;
        this.applicationConfiguration = configuration;
        this.storageRepositoryFactory = indexStorageRepositoryFactory;
    }

    @Override
    public RelatedProductIndexRequestProcessor getIndexRequestProcessor() {
        return indexingRequestProcessingFactory.createProcessor(getConfiguration());
    }

    @Override
    public RelatedProductStorageRepository getStorageRepository() {
        return storageRepositoryFactory.getRepository();
    }

    @Override
    public Configuration getConfiguration() {
        return applicationConfiguration;
    }
}
