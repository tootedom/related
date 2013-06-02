package org.greencheek.relatedproduct.web;

import org.greencheek.relatedproduct.indexing.RelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 02/06/2013
 * Time: 10:27
 * To change this template use File | Settings | File Templates.
 */

public class ShutdownExtension implements Extension {

    private static final Logger log = LoggerFactory.getLogger(ShutdownExtension.class);
//    @Inject
//    private RelatedProductIndexRequestProcessor indexer;
//
//    @Inject
//    private RelatedProductStorageRepository repository;

    public ShutdownExtension() {

    }

    public void onShutdown(@Observes BeforeShutdown beforeShutdownEvent) {
        log.error("Shutdown event received!");

//        try {
//            indexer.shutdown();
//        } catch (Exception e) {
//            log.info("Unable to shutdown index request processor: {}", indexer.getClass().getCanonicalName());
//        }
//
//        try {
//            repository.shutdown();
//        } catch(Exception e) {
//            log.info("Unable to shutdown related product storage repository: {}", repository.getClass().getCanonicalName());
//        }

    }
}
