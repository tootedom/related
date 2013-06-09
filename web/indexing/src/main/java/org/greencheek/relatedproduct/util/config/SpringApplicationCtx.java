package org.greencheek.relatedproduct.util.config;

import org.greencheek.relatedproduct.indexing.RelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;

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

    private final RelatedProductIndexRequestProcessor indexRequestProcessor;
    private final RelatedProductStorageRepository storageRepository;
    private final Configuration applicationConfiguration;

    @Inject
    public SpringApplicationCtx(RelatedProductIndexRequestProcessor processor,
                                RelatedProductStorageRepository storageRepository,
                                Configuration configuration)
    {
        this.indexRequestProcessor = processor;
        this.storageRepository = storageRepository;
        this.applicationConfiguration = configuration;
    }

    @Override
    public RelatedProductIndexRequestProcessor getIndexRequestProcessor() {
        return indexRequestProcessor;
    }

    @Override
    public RelatedProductStorageRepository getStorageRepository() {
        return storageRepository;
    }

    @Override
    public Configuration getConfiguration() {
        return applicationConfiguration;
    }
}
