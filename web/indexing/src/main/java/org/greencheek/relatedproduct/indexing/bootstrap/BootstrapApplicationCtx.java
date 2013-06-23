package org.greencheek.relatedproduct.indexing.bootstrap;

import org.greencheek.relatedproduct.api.indexing.BasicRelatedProductIndexingMessageConverter;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageConverter;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageFactory;
import org.greencheek.relatedproduct.indexing.*;
import org.greencheek.relatedproduct.indexing.elasticsearch.ElasticSearchRelatedProductStorageRepositoryFactory;
import org.greencheek.relatedproduct.indexing.jsonrequestprocessing.JsonSmartIndexingRequestConverterFactory;
import org.greencheek.relatedproduct.indexing.locationmappers.DayBasedStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.locationmappers.HourBasedStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.locationmappers.MinuteBasedStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.requestprocessorfactory.IndexRequestProcessorFactory;
import org.greencheek.relatedproduct.indexing.requestprocessorfactory.RoundRobinIndexRequestProcessorFactory;
import org.greencheek.relatedproduct.indexing.util.JodaISO8601UTCCurrentDateAndTimeFormatter;
import org.greencheek.relatedproduct.indexing.util.JodaUTCCurrentDateAndHourAndMinuteFormatter;
import org.greencheek.relatedproduct.indexing.util.JodaUTCCurrentDateAndHourFormatter;
import org.greencheek.relatedproduct.indexing.util.JodaUTCCurrentDateFormatter;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 02/06/2013
 * Time: 15:03
 * To change this template use File | Settings | File Templates.
 */
public class BootstrapApplicationCtx implements ApplicationCtx {

    private final Configuration applicationConfiguration;
    private final IndexRequestProcessorFactory indexingRequestProcessingFactory;

    public BootstrapApplicationCtx()
    {
        this.applicationConfiguration = new SystemPropertiesConfiguration();

        IndexingRequestConverterFactory requestBytesConverter = new JsonSmartIndexingRequestConverterFactory(
                new JodaISO8601UTCCurrentDateAndTimeFormatter());

        RelatedProductIndexingMessageFactory indexingMessageFactory = new RelatedProductIndexingMessageFactory(applicationConfiguration);

        RelatedProductIndexingMessageConverter indexingMessageToRelatedProductsConverter = new BasicRelatedProductIndexingMessageConverter(applicationConfiguration);
        RelatedProductStorageRepositoryFactory repoFactory = new ElasticSearchRelatedProductStorageRepositoryFactory(applicationConfiguration);

        RelatedProductStorageLocationMapper locationMapper;

        String storageLocationMapperType = applicationConfiguration.getStorageLocationMapper();
        if(storageLocationMapperType.equalsIgnoreCase("day")) {
            locationMapper = new DayBasedStorageLocationMapper(applicationConfiguration, new JodaUTCCurrentDateFormatter());
        } else if(storageLocationMapperType.equalsIgnoreCase("hour")) {
            locationMapper = new HourBasedStorageLocationMapper(applicationConfiguration, new JodaUTCCurrentDateAndHourFormatter());
        } else {
            locationMapper = new MinuteBasedStorageLocationMapper(applicationConfiguration, new JodaUTCCurrentDateAndHourAndMinuteFormatter());
        }

        this.indexingRequestProcessingFactory = new RoundRobinIndexRequestProcessorFactory(requestBytesConverter,
                indexingMessageFactory,indexingMessageToRelatedProductsConverter,repoFactory,locationMapper);



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
