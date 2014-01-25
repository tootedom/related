package org.greencheek.related.util.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.greencheek.related.util.config.ConfigurationConstants.*;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class YamlSystemPropertiesConfigurationTest {

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
        System.clearProperty(YamlSystemPropertiesConfiguration.PROPNAME_SETTINGS_YAML_LOCATION);
        System.clearProperty(ConfigurationConstants.PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE);
    }


    @Test
    public void testTesYamlFileIsRead() {
        System.setProperty(YamlSystemPropertiesConfiguration.PROPNAME_SETTINGS_YAML_LOCATION, "test-related-item.yaml");
        Configuration config = new YamlSystemPropertiesConfiguration();

        assertEquals(1024, config.getSizeOfRelatedItemSearchRequestHandlerQueue());
        assertEquals(16,config.getNumberOfSearchingRequestProcessors());
        assertEquals(4096,config.getSizeOfBatchIndexingRequestQueue());

    }

    @Test
    public void testYamlFileThatDoesNotExistWillUseDefault() {
        System.setProperty(YamlSystemPropertiesConfiguration.PROPNAME_SETTINGS_YAML_LOCATION,"test-related-item-doesnotexist.yaml");
        Configuration config = new YamlSystemPropertiesConfiguration();

        assertEquals(config.getSizeOfRelatedItemSearchRequestQueue()/ConfigurationConstants.DEFAULT_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS, config.getSizeOfRelatedItemSearchRequestHandlerQueue());
        assertEquals(ConfigurationConstants.DEFAULT_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS,config.getNumberOfSearchingRequestProcessors());
        assertEquals(ConfigurationConstants.DEFAULT_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE,config.getSizeOfBatchIndexingRequestQueue());
    }

    @Test
    public void testSystemPropertiesOverrideYamlFile() {
        System.setProperty(YamlSystemPropertiesConfiguration.PROPNAME_SETTINGS_YAML_LOCATION, "test-related-item.yaml");
        System.setProperty(ConfigurationConstants.PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE,"8192");
        Configuration config = new YamlSystemPropertiesConfiguration();

        assertEquals(8192, config.getSizeOfRelatedItemSearchRequestHandlerQueue());
        assertEquals(16,config.getNumberOfSearchingRequestProcessors());
        assertEquals(4096,config.getSizeOfBatchIndexingRequestQueue());
    }

    @Test
    public void testYamlFileOverridesAllProperties() {
        Configuration config = new YamlSystemPropertiesConfiguration();

        assertThat(config.isSafeToOutputRequestData(),not(DEFAULT_SAFE_TO_OUTPUT_REQUEST_DATA));
        assertThat(config.getMaxNumberOfRelatedItemProperties(),not(DEFAULT_MAX_NO_OF_RELATED_ITEM_PROPERTES));
        assertThat(config.getMaxNumberOfRelatedItemsPerItem(),not(DEFAULT_MAX_NO_OF_RELATED_ITEMS_PER_INDEX_REQUEST));
        assertThat(config.getRelatedItemIdLength(),not(DEFAULT_RELATED_ITEM_ID_LENGTH));
        assertThat(config.getMaxRelatedItemPostDataSizeInBytes(),not(DEFAULT_MAX_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES));
        assertThat(config.getMinRelatedItemPostDataSizeInBytes(),not(DEFAULT_MIN_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES));
        assertThat(config.getRelatedItemAdditionalPropertyKeyLength(),not(DEFAULT_RELATED_ITEM_ADDITIONAL_PROPERTY_KEY_LENGTH));
        assertThat(config.getRelatedItemAdditionalPropertyValueLength(),not(DEFAULT_RELATED_ITEM_ADDITIONAL_PROPERTY_VALUE_LENGTH));
        assertThat(config.getSizeOfIncomingMessageQueue(),not(DEFAULT_SIZE_OF_INCOMING_REQUEST_QUEUE ));
        assertThat(config.getSizeOfBatchIndexingRequestQueue(),not(DEFAULT_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE ));
        assertThat(config.getIndexBatchSize(),not(DEFAULT_BATCH_INDEX_SIZE ));
        assertThat(config.getSizeOfRelatedItemSearchRequestQueue(),not(DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE ));
        assertThat(config.getSizeOfRelatedItemSearchRequestHandlerQueue(),not(DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE ));
        assertThat(config.getSizeOfRelatedItemSearchRequestAndResponseQueue(),not(DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE ));
        assertThat(config.getMaxNumberOfSearchCriteriaForRelatedContent(),not(DEFAULT_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT ));
        assertThat(config.getNumberOfExpectedLikeForLikeRequests(),not(DEFAULT_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS ));
        assertThat(config.getKeyForFrequencyResultId(),not(DEFAULT_KEY_FOR_FREQUENCY_RESULT_ID ));
        assertThat(config.getKeyForFrequencyResultOccurrence(),not(DEFAULT_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE ));
        assertThat(config.getKeyForFrequencyResultOverallResultsSize(),not(DEFAULT_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_ITEMS));
        assertThat(config.getKeyForFrequencyResults(),not(DEFAULT_KEY_FOR_FREQUENCY_RESULTS ));
        assertThat(config.getRequestParameterForSize(),not(DEFAULT_REQUEST_PARAMETER_FOR_SIZE ));
        assertThat(config.getRequestParameterForId(),not(DEFAULT_REQUEST_PARAMETER_FOR_ID ));
        assertThat(config.getDefaultNumberOfResults(),not(DEFAULT_DEFAULT_NUMBER_OF_RESULTS));
        assertThat(config.getSizeOfResponseProcessingQueue(),not(DEFAULT_SIZE_OF_RESPONSE_PROCESSING_QUEUE ));
        assertThat(config.getNumberOfIndexingRequestProcessors(),not(DEFAULT_NUMBER_OF_INDEXING_REQUEST_PROCESSORS ));
        assertThat(config.getNumberOfSearchingRequestProcessors(),not(DEFAULT_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS ));
        assertThat(config.getStorageIndexNamePrefix(),not(DEFAULT_STORAGE_INDEX_NAME_PREFIX ));
        assertThat(config.getStorageIndexNameAlias(),not(DEFAULT_STORAGE_INDEX_NAME_ALIAS ));
        assertThat(config.getStorageContentTypeName(),not(DEFAULT_STORAGE_CONTENT_TYPE_NAME ));
        assertThat(config.getStorageClusterName(),not(DEFAULT_STORAGE_CLUSTER_NAME ));
        assertThat(config.getStorageFrequentlyRelatedItemsFacetResultsFacetName(),not(DEFAULT_STORAGE_FREQUENTLY_RELATED_ITEMS_FACET_RESULTS_FACET_NAME));
        assertThat(config.getStorageFacetExecutionHint(),not(DEFAULT_STORAGE_FACET_SEARCH_EXECUTION_HINT ));
        assertThat(config.getKeyForIndexRequestRelatedWithAttr(),not(DEFAULT_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR ));
        assertThat(config.getKeyForIndexRequestDateAttr(),not(DEFAULT_KEY_FOR_INDEX_REQUEST_DATE_ATTR ));
        assertThat(config.getKeyForIndexRequestIdAttr(),not(DEFAULT_KEY_FOR_INDEX_REQUEST_ID_ATTR ));
        assertThat(config.getKeyForIndexRequestProductArrayAttr(),not(DEFAULT_KEY_FOR_INDEX_REQUEST_ITEM_ARRAY_ATTR));
        assertThat(config.getElasticSearchClientDefaultTransportSettingFileName(),not(DEFAULT_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME ));
        assertThat(config.getElasticSearchClientDefaultNodeSettingFileName(),not(DEFAULT_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME ));
        assertThat(config.getElasticSearchClientOverrideSettingFileName(),not(DEFAULT_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME ));
        assertThat(config.getFrequentlyRelatedItemsSearchTimeoutInMillis(),not(DEFAULT_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS ));
        assertThat(config.getStorageLocationMapper(),not(DEFAULT_RELATED_ITEM_STORAGE_LOCATION_MAPPER));
        assertThat(config.getTimedOutSearchRequestStatusCode(),not(DEFAULT_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE ));
        assertThat(config.getFailedSearchRequestStatusCode(),not(DEFAULT_FAILED_SEARCH_REQUEST_STATUS_CODE ));
        assertThat(config.getNoFoundSearchResultsStatusCode(),not(DEFAULT_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE ));
        assertThat(config.getFoundSearchResultsStatusCode(),not(DEFAULT_FOUND_SEARCH_REQUEST_STATUS_CODE ));
        assertThat(config.getMissingSearchResultsHandlerStatusCode(),not(DEFAULT_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE ));
        assertThat(config.getPropertyEncoding(),not(DEFAULT_PROPERTY_ENCODING ));
        assertThat(config.getWaitStrategyFactory().toString(),not(DEFAULT_WAIT_STRATEGY ));
        assertThat(config.getElasticSearchClientType().toString(),not(DEFAULT_ES_CLIENT_TYPE ));
        assertThat(config.isIndexNameDateCachingEnabled(),not(DEFAULT_INDEXNAME_DATE_CACHING_ENABLED ));
        assertThat(config.getNumberOfIndexNamesToCache(),not(DEFAULT_NUMBER_OF_INDEXNAMES_TO_CACHE ));
        assertThat(config.getShouldReplaceOldContentIfExists(),not(DEFAULT_REPLACE_OLD_INDEXED_CONTENT ));
        assertThat(config.getShouldUseSeparateIndexStorageThread(),not(DEFAULT_SEPARATE_INDEXING_THREAD ));
        assertThat(config.shouldDiscardIndexRequestWithTooManyRelations(),not(DEFAULT_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_ITEMS));
        assertThat(config.getElasticSearchTransportHosts(),not(DEFAULT_ELASTIC_SEARCH_TRANSPORT_HOSTS ));
        assertThat(config.getDefaultElasticSearchPort(),not(DEFAULT_DEFAULT_ELASTIC_SEARCH_PORT ));
        assertThat(config.useSharedSearchRepository(),not(DEFAULT_USE_SHARED_SEARCH_REPOSITORY ));
        assertThat(config.isSearchResponseDebugOutputEnabled(),not(DEFAULT_RELATED_ITEM_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED));
    }

}
