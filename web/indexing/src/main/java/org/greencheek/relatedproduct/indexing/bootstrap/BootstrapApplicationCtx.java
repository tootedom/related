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
import org.greencheek.relatedproduct.indexing.requestprocessorfactory.IndexRequestProcessorFactory;
import org.greencheek.relatedproduct.indexing.requestprocessorfactory.RoundRobinIndexRequestProcessorFactory;
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
    private final IndexRequestProcessorFactory indexingRequestProcessingFactory;

    public BootstrapApplicationCtx()
    {
        this.applicationConfiguration = new SystemPropertiesConfiguration();

        IndexingRequestConverterFactory requestBytesConverter = new JsonSmartIndexingRequestConverterFactory(new JodaISO8601UTCCurrentDateAndTimeFormatter());

        RelatedProductIndexingMessageFactory indexingMessageFactory = new RelatedProductIndexingMessageFactory(applicationConfiguration);
        RelatedProductReferenceMessageFactory indexingReferenceMessageFactory = new RelatedProductReferenceMessageFactory();

        RelatedProductIndexingMessageConverter indexingMessageToRelatedProductsConverter = new BasicRelatedProductIndexingMessageConverter(applicationConfiguration);
        RelatedProductStorageRepositoryFactory repoFactory = getStorageRepositoryFactory(applicationConfiguration);

        RelatedProductStorageLocationMapper locationMapper;

        String storageLocationMapperType = applicationConfiguration.getStorageLocationMapper();
        if(storageLocationMapperType.equalsIgnoreCase("day")) {
            locationMapper = new DayBasedStorageLocationMapper(applicationConfiguration, new JodaUTCCurrentDateFormatter());
        } else if(storageLocationMapperType.equalsIgnoreCase("hour")) {
            locationMapper = new HourBasedStorageLocationMapper(applicationConfiguration, new JodaUTCCurrentDateAndHourFormatter());
        } else {
            locationMapper = new MinuteBasedStorageLocationMapper(applicationConfiguration, new JodaUTCCurrentDateAndHourAndMinuteFormatter());
        }

        RelatedProductReferenceEventHandlerFactory factory = new BatchingRelatedProductReferenceEventHanderFactory(applicationConfiguration,
                repoFactory,locationMapper);

        RelatedProductIndexingMessageEventHandler roundRobinMessageStorage = new RoundRobinRelatedProductIndexingMessageEventHandler(applicationConfiguration,
                indexingMessageToRelatedProductsConverter,indexingReferenceMessageFactory,factory);

        RelatedProductIndexingMessageEventHandler singleMessageStorage = new SingleRelatedProductIndexingMessageEventHandler(applicationConfiguration,
                indexingMessageToRelatedProductsConverter,repoFactory.getRepository(applicationConfiguration),locationMapper);

        this.indexingRequestProcessingFactory = new RoundRobinIndexRequestProcessorFactory(requestBytesConverter,
                indexingMessageFactory,roundRobinMessageStorage,singleMessageStorage);



    }

    /**
     * Returns the factory that is responsible for creating the backend storage repository objects, that
     * basically store the {@link org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage}
     */
    public RelatedProductStorageRepositoryFactory getStorageRepositoryFactory(Configuration applicationConfiguration) {
        return new ElasticSearchRelatedProductStorageRepositoryFactory(applicationConfiguration);
    }



    @Override
    public RelatedProductIndexRequestProcessor getIndexRequestProcessor() {
        return indexingRequestProcessingFactory.createProcessor(getConfiguration());
    }

    @Override
    public Configuration getConfiguration() {
        return applicationConfiguration;
    }
}
