package org.greencheek.relatedproduct.indexing.bootstrap;

import org.greencheek.relatedproduct.api.indexing.BasicRelatedProductIndexingMessageConverter;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageConverter;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageFactory;
import org.greencheek.relatedproduct.api.indexing.RelatedProductReferenceMessageFactory;
import org.greencheek.relatedproduct.indexing.*;
import org.greencheek.relatedproduct.indexing.elasticsearch.ElasticSearchRelatedProductStorageRepositoryFactory;
import org.greencheek.relatedproduct.indexing.jsonrequestprocessing.JsonSmartIndexingRequestConverterFactory;
import org.greencheek.relatedproduct.indexing.locationmappers.DayBasedStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.locationmappers.HourBasedStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.locationmappers.MinuteBasedStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.requestprocessorfactory.DisruptorIndexRequestProcessorFactory;
import org.greencheek.relatedproduct.indexing.requestprocessorfactory.IndexRequestProcessorFactory;
import org.greencheek.relatedproduct.indexing.requestprocessors.RelatedProductIndexingMessageEventHandler;
import org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor.BatchingRelatedProductReferenceEventHanderFactory;
import org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor.RelatedProductReferenceEventHandlerFactory;
import org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor.RoundRobinRelatedProductIndexingMessageEventHandler;
import org.greencheek.relatedproduct.indexing.requestprocessors.single.disruptor.SingleRelatedProductIndexingMessageEventHandler;
import org.greencheek.relatedproduct.indexing.util.JodaISO8601UTCCurrentDateAndTimeFormatter;
import org.greencheek.relatedproduct.indexing.util.JodaUTCCurrentDateAndHourAndMinuteFormatter;
import org.greencheek.relatedproduct.indexing.util.JodaUTCCurrentDateAndHourFormatter;
import org.greencheek.relatedproduct.indexing.util.JodaUTCCurrentDateFormatter;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;



/**
 * Basic Bootstrap, that performs the wiring up of application.
 * It's the class that performs the dependency injection
 */
public class BootstrapApplicationCtx implements ApplicationCtx {

    private final Configuration applicationConfiguration;
    private volatile IndexRequestProcessorFactory indexingRequestProcessingFactory;

    public BootstrapApplicationCtx()
    {
        this.applicationConfiguration = createConfiguration();
    }

    public RelatedProductReferenceMessageFactory createRelatedProductReferenceFactory() {
        return new RelatedProductReferenceMessageFactory();
    }


    public RelatedProductReferenceEventHandlerFactory createRelatedProductReferenceFactory(Configuration applicationConfiguration,
                                                                                           RelatedProductStorageLocationMapper locationMapper,
                                                                                           RelatedProductStorageRepositoryFactory repoFactory) {

        return new BatchingRelatedProductReferenceEventHanderFactory(applicationConfiguration,repoFactory,locationMapper);
    }

    public RelatedProductStorageLocationMapper createIndexNameLocationMapper(Configuration applicationConfiguration) {

        RelatedProductStorageLocationMapper locationMapper;

        String storageLocationMapperType = applicationConfiguration.getStorageLocationMapper();
        if(storageLocationMapperType.equalsIgnoreCase("day")) {
            locationMapper = new DayBasedStorageLocationMapper(applicationConfiguration, new JodaUTCCurrentDateFormatter());
        } else if(storageLocationMapperType.equalsIgnoreCase("hour")) {
            locationMapper = new HourBasedStorageLocationMapper(applicationConfiguration, new JodaUTCCurrentDateAndHourFormatter());
        } else {
            locationMapper = new MinuteBasedStorageLocationMapper(applicationConfiguration, new JodaUTCCurrentDateAndHourAndMinuteFormatter());
        }

        return locationMapper;
    }

    public RelatedProductIndexingMessageConverter createIndexingMessageToRelatedProduct(Configuration applicationConfiguration) {
        return new BasicRelatedProductIndexingMessageConverter(applicationConfiguration);
    }

    public RelatedProductIndexingMessageFactory createIndexingMessageFactory() {
        return new RelatedProductIndexingMessageFactory(applicationConfiguration);

    }

    public IndexingRequestConverterFactory createBytesToIndexingMessageConverterFactory() {
        return new JsonSmartIndexingRequestConverterFactory(new JodaISO8601UTCCurrentDateAndTimeFormatter());
    }

    public Configuration createConfiguration() {
         return new SystemPropertiesConfiguration();
    }

    /**
     * Returns the factory that is responsible for creating the backend storage repository objects, that
     * basically store the {@link org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage}
     */
    public RelatedProductStorageRepositoryFactory getStorageRepositoryFactory(Configuration applicationConfiguration) {
        return new ElasticSearchRelatedProductStorageRepositoryFactory(applicationConfiguration);
    }

   /**
    * chooses between the backend processing that is done to turn the request data into a
    * {@link org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage}
    * that is stored in the backend.
    *
    * The processing is in either one of two ways:
    *
    * <pre>
    * 1)  ---->  Request --->  Ring buffer (to IndexingMessage)  --->  Storage repository
    *
    * OR
    *
    * 2)  ---->  Request --->  Ring buffer (to IndexingMessage)  --->  Ring Buffer (Reference) ---> Storage Repo
    *                                                            --->  Ring Buffer (Reference) ---> Storage Repo
    *                                                            --->  Ring Buffer (Reference) ---> Storage Repo
    * </pre>
    *
    * The choice between the two request processor is done based upon the value set for {@link org.greencheek.relatedproduct.util.config.Configuration#getNumberOfIndexingRequestProcessors()}
    */
    @Override
    public synchronized IndexRequestProcessorFactory getIndexRequestProcessorFactory() {
        if(indexingRequestProcessingFactory==null) {
            RelatedProductStorageLocationMapper locationMapper = createIndexNameLocationMapper(applicationConfiguration);
            RelatedProductStorageRepositoryFactory repoFactory = getStorageRepositoryFactory(applicationConfiguration);
            RelatedProductReferenceEventHandlerFactory factory = createRelatedProductReferenceFactory(applicationConfiguration,locationMapper,repoFactory);
            RelatedProductIndexingMessageConverter indexingMessageToRelatedProductsConverter = createIndexingMessageToRelatedProduct(applicationConfiguration);

            RelatedProductIndexingMessageEventHandler eventHandler = null;

            if(applicationConfiguration.getNumberOfIndexingRequestProcessors()>1) {
                eventHandler = new RoundRobinRelatedProductIndexingMessageEventHandler(applicationConfiguration,
                    indexingMessageToRelatedProductsConverter,createRelatedProductReferenceFactory(),factory);
            } else {
                eventHandler = new SingleRelatedProductIndexingMessageEventHandler(applicationConfiguration,
                    indexingMessageToRelatedProductsConverter,repoFactory.getRepository(applicationConfiguration),locationMapper);
            }

            this.indexingRequestProcessingFactory = new DisruptorIndexRequestProcessorFactory(createBytesToIndexingMessageConverterFactory(),
                    createIndexingMessageFactory(),eventHandler);
        }

        return indexingRequestProcessingFactory;
    }

    @Override
    public Configuration getConfiguration() {
        return applicationConfiguration;
    }
}
