package org.greencheek.relatedproduct.util.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.greencheek.relatedproduct.util.config.ConfigurationConstants.*;

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
        System.setProperty(YamlSystemPropertiesConfiguration.PROPNAME_SETTINGS_YAML_LOCATION,"test-related-product.yaml");
        Configuration config = new YamlSystemPropertiesConfiguration();

        assertEquals(1024, config.getSizeOfRelatedContentSearchRequestHandlerQueue());
        assertEquals(16,config.getNumberOfSearchingRequestProcessors());
        assertEquals(4096,config.getSizeOfBatchIndexingRequestQueue());

    }

    @Test
    public void testYamlFileThatDoesNotExistWillUseDefault() {
        System.setProperty(YamlSystemPropertiesConfiguration.PROPNAME_SETTINGS_YAML_LOCATION,"test-related-product-doesnotexist.yaml");
        Configuration config = new YamlSystemPropertiesConfiguration();

        assertEquals(ConfigurationConstants.DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE, config.getSizeOfRelatedContentSearchRequestHandlerQueue());
        assertEquals(ConfigurationConstants.DEFAULT_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS,config.getNumberOfSearchingRequestProcessors());
        assertEquals(ConfigurationConstants.DEFAULT_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE,config.getSizeOfBatchIndexingRequestQueue());
    }

    @Test
    public void testSystemPropertiesOverrideYamlFile() {
        System.setProperty(YamlSystemPropertiesConfiguration.PROPNAME_SETTINGS_YAML_LOCATION,"test-related-product.yaml");
        System.setProperty(ConfigurationConstants.PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE,"8192");
        Configuration config = new YamlSystemPropertiesConfiguration();

        assertEquals(8192, config.getSizeOfRelatedContentSearchRequestHandlerQueue());
        assertEquals(16,config.getNumberOfSearchingRequestProcessors());
        assertEquals(4096,config.getSizeOfBatchIndexingRequestQueue());
    }

    @Test
    public void testYamlFileOverridesAllProperties() {
        System.setProperty(YamlSystemPropertiesConfiguration.PROPNAME_SETTINGS_YAML_LOCATION,"related-product.yaml");
        Configuration config = new YamlSystemPropertiesConfiguration();

        assertThat(config.isSafeToOutputRequestData(),not(DEFAULT_SAFE_TO_OUTPUT_REQUEST_DATA));
        assertThat(config.getMaxNumberOfRelatedProductProperties(),not(DEFAULT_MAX_NO_OF_RELATED_PRODUCT_PROPERTES ));
        assertThat(config.getMaxNumberOfRelatedProductsPerPurchase(),not(DEFAULT_MAX_NO_OF_RELATED_PRODUCTS_PER_INDEX_REQUEST ));
        assertThat(config.getRelatedProductIdLength(),not(DEFAULT_RELATED_PRODUCT_ID_LENGTH ));
        assertThat(config.getRelatedProductInvalidIdString(),not(DEFAULT_RELATED_PRODUCT_INVALID_ID_STRING ));
        assertThat(config.getMaxRelatedProductPostDataSizeInBytes(),not(DEFAULT_MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES ));
        assertThat(config.getMinRelatedProductPostDataSizeInBytes(),not(DEFAULT_MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES ));
        assertThat(config.getRelatedProductAdditionalPropertyKeyLength(),not(DEFAULT_RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH ));
        assertThat(config.getRelatedProductAdditionalPropertyValueLength(),not(DEFAULT_RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH ));
        assertThat(config.getSizeOfIncomingMessageQueue(),not(DEFAULT_SIZE_OF_INCOMING_REQUEST_QUEUE ));
        assertThat(config.getSizeOfBatchIndexingRequestQueue(),not(DEFAULT_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE ));
        assertThat(config.getIndexBatchSize(),not(DEFAULT_BATCH_INDEX_SIZE ));
        assertThat(config.getSizeOfRelatedContentSearchRequestQueue(),not(DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE ));
        assertThat(config.getSizeOfRelatedContentSearchRequestHandlerQueue(),not(DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE ));
        assertThat(config.getSizeOfRelatedContentSearchRequestAndResponseQueue(),not(DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE ));
        assertThat(config.getMaxNumberOfSearchCriteriaForRelatedContent(),not(DEFAULT_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT ));
        assertThat(config.getNumberOfExpectedLikeForLikeRequests(),not(DEFAULT_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS ));
        assertThat(config.getKeyForFrequencyResultId(),not(DEFAULT_KEY_FOR_FREQUENCY_RESULT_ID ));
        assertThat(config.getKeyForFrequencyResultOccurrence(),not(DEFAULT_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE ));
        assertThat(config.getKeyForFrequencyResultOverallResultsSize(),not(DEFAULT_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_PRODUCTS ));
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
        assertThat(config.getStorageFrequentlyRelatedProductsFacetResultsFacetName(),not(DEFAULT_STORAGE_FREQUENTLY_RELATED_PRODUCTS_FACET_RESULTS_FACET_NAME ));
        assertThat(config.getStorageFacetExecutionHint(),not(DEFAULT_STORAGE_FACET_SEARCH_EXECUTION_HINT ));
        assertThat(config.getKeyForIndexRequestRelatedWithAttr(),not(DEFAULT_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR ));
        assertThat(config.getKeyForIndexRequestDateAttr(),not(DEFAULT_KEY_FOR_INDEX_REQUEST_DATE_ATTR ));
        assertThat(config.getKeyForIndexRequestIdAttr(),not(DEFAULT_KEY_FOR_INDEX_REQUEST_ID_ATTR ));
        assertThat(config.getKeyForIndexRequestProductArrayAttr(),not(DEFAULT_KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR ));
        assertThat(config.getElasticSearchClientDefaultTransportSettingFileName(),not(DEFAULT_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME ));
        assertThat(config.getElasticSearchClientDefaultNodeSettingFileName(),not(DEFAULT_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME ));
        assertThat(config.getElasticSearchClientOverrideSettingFileName(),not(DEFAULT_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME ));
        assertThat(config.getFrequentlyRelatedProductsSearchTimeoutInMillis(),not(DEFAULT_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS ));
        assertThat(config.getStorageLocationMapper(),not(DEFAULT_RELATED_PRODUCT_STORAGE_LOCATION_MAPPER ));
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
        assertThat(config.shouldDiscardIndexRequestWithTooManyRelations(),not(DEFAULT_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS ));
        assertThat(config.getElasticSearchTransportHosts(),not(DEFAULT_ELASTIC_SEARCH_TRANSPORT_HOSTS ));
        assertThat(config.getDefaultElasticSearchPort(),not(DEFAULT_DEFAULT_ELASTIC_SEARCH_PORT ));
        assertThat(config.useSharedSearchRepository(),not(DEFAULT_USE_SHARED_SEARCH_REPOSITORY ));
        assertThat(config.isSearchResponseDebugOutputEnabled(),not(DEFAULT_RELATED_PRODUCT_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED ));
    }

}
