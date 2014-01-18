package org.greencheek.relatedproduct.util.config;

import org.greencheek.relatedproduct.api.searching.SearchResultsOutcome;
import org.greencheek.relatedproduct.util.arrayindexing.Util;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 14:52
 * To change this template use File | Settings | File Templates.
 * ==============================================================
 * For Indexing:
 * -------------
 * The settings for a 2 cpu, 4 core per cpu, with a 12gb heap would be:
 * (JDK7) :
 * Standard JDK Opts:
 *
 * -Djava.awt.headless=true
 * -XX:+UseParNewGC
 * -XX:+UseConcMarkSweepGC
 * -XX:CMSInitiatingOccupancyFraction=90
 * -XX:+UseCMSInitiatingOccupancyOnly
 * -XX:MaxTenuringThreshold=15
 * -Djava.rmi.server.hostname=10.0.1.29
 * -Dcom.sun.management.jmxremote.port=3333
 * -Dcom.sun.management.jmxremote.ssl=false
 * -Dcom.sun.management.jmxremote.authenticate=false
 * -Xmx11776m -Xmn8700m -Xms11776m -Xss256k -XX:MaxPermSize=128m
 * -XX:+UnlockDiagnosticVMOptions -XX:ParGCCardsPerStrideChunk=4096 -XX:+AggressiveOpts
 * -XX:+UseCondCardMark
 *
 * Indexing opts:
 *
 * -Drelated-product.wait.strategy=busy
 * -Drelated-product.size.of.incoming.request.queue=65536
 * -Drelated-product.number.of.indexing.request.processors=8
 * -Drelated-product.index.batch.size=450
 * -Drelated-product.elastic.search.transport.hosts=10.0.1.19:9300
 * -Des.discovery.zen.ping.multicast.enabled=false
 * -Des.discovery.zen.ping.unicast.hosts=10.0.1.19
 * -Dnetwork.tcp.no_delay=false
 *
 * ==============================================================
 *
 * -Drelated-product.size.of.related.content.search.request.queue=32768
 * -Drelated-product.size.of.response.processing.queue=32768
 * -Drelated-product.size.of.related.content.search.request.and.response.queue=262144
 * -Drelated-product.size.of.related.content.search.request.handler.queue=32768
 * -Drelated-product.number.of.searching.request.processors=8
 *
 */
public class SystemPropertiesConfiguration implements Configuration {


    private final boolean SAFE_TO_OUTPUT_REQUEST_DATA = Boolean.valueOf(System.getProperty(PROPNAME_SAFE_TO_OUTPUT_REQUEST_DATA,"false"));
    private final int MAX_NUMBER_OF_RELATED_PRODUCT_PROPERTIES = Short.valueOf(System.getProperty(PROPNAME_MAX_NO_OF_RELATED_PRODUCT_PROPERTES, "10"));
    private final int MAX_NUMBER_OF_RELATED_PRODUCTS_PER_PURCHASE = Short.valueOf(System.getProperty(PROPNAME_MAX_NO_OF_RELATED_PRODUCTS_PER_INDEX_REQUEST, "10"));
    private final int RELATED_PRODUCT_ID_LENGTH = Short.valueOf(System.getProperty(PROPNAME_RELATED_PRODUCT_ID_LENGTH, "36"));
    private final String RELATED_PRODUCT_INVALID_ID_STRING = System.getProperty(PROPNAME_RELATED_PRODUCT_INVALID_ID_STRING, "INVALID_ID");
    private final int MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES = Integer.valueOf(System.getProperty(PROPNAME_MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES,"10240"));
    private final int MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES = Integer.valueOf(System.getProperty(PROPNAME_MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES,"4096"));

    private final int RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH = Short.valueOf(System.getProperty(PROPNAME_RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH, "30"));
    private final int RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH = Short.valueOf(System.getProperty(PROPNAME_RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH, "30"));
    private final int SIZE_OF_INCOMING_REQUEST_QUEUE = Integer.valueOf(System.getProperty(PROPNAME_SIZE_OF_INCOMING_REQUEST_QUEUE, "2048"));
    private final int SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE = Integer.valueOf(System.getProperty(PROPNAME_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE, "-1"));
    private final int BATCH_INDEX_SIZE = Integer.valueOf(System.getProperty(PROPNAME_BATCH_INDEX_SIZE,"128"));

    private final int SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE = Util.ceilingNextPowerOfTwo(Integer.valueOf(System.getProperty(PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE, "2048")));

    private final int SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE = Util.ceilingNextPowerOfTwo(Integer.valueOf(System.getProperty(PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE, "2048")));
    private final int SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE = Util.ceilingNextPowerOfTwo(Integer.valueOf(System.getProperty(PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE, "2048")));

    private final int MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT =  Short.valueOf(System.getProperty(PROPNAME_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT, "10"));
    private final int NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS = Integer.valueOf(System.getProperty(PROPNAME_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS, "10"));

    private final String KEY_FOR_FREQUENCY_RESULT_ID = System.getProperty(PROPNAME_KEY_FOR_FREQUENCY_RESULT_ID,"id");
    private final String KEY_FOR_FREQUENCY_RESULT_OCCURRENCE = System.getProperty(PROPNAME_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE,"frequency");
    private final String KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_PRODUCTS = System.getProperty(PROPNAME_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_PRODUCTS,"size");
    private final String KEY_FOR_FREQUENCY_RESULTS = System.getProperty(PROPNAME_KEY_FOR_FREQUENCY_RESULTS,"results");

    private final String REQUEST_PARAMETER_FOR_SIZE = System.getProperty(PROPNAME_REQUEST_PARAMETER_FOR_SIZE,"maxresults");
    private final String REQUEST_PARAMETER_FOR_ID = System.getProperty(PROPNAME_REQUEST_PARAMETER_FOR_ID,"id");

    private final int DEFAULT_NUMBER_OF_RESULTS = Integer.valueOf(System.getProperty(PROPNAME_DEFAULT_NUMBER_OF_RESULTS,"4"));
    private final int SIZE_OF_RESPONSE_PROCESSING_QUEUE = Util.ceilingNextPowerOfTwo(Integer.valueOf(System.getProperty(PROPNAME_SIZE_OF_RESPONSE_PROCESSING_QUEUE,"2048")));
    private final int NUMBER_OF_INDEXING_REQUEST_PROCESSORS = Short.valueOf(System.getProperty(PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS,"2"));
    private final int NUMBER_OF_SEARCHING_REQUEST_PROCESSORS = Util.ceilingNextPowerOfTwo(Short.valueOf(System.getProperty(PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS,"4")));

    private final String STORAGE_INDEX_NAME_PREFIX = System.getProperty(PROPNAME_STORAGE_INDEX_NAME_PREFIX,"relatedproducts");
    private final String STORAGE_INDEX_NAME_ALIAS = System.getProperty(PROPNAME_STORAGE_INDEX_NAME_ALIAS,"");
    private final String STORAGE_CONTENT_TYPE_NAME = System.getProperty(PROPNAME_STORAGE_CONTENT_TYPE_NAME,"relatedproduct");
    private final String STORAGE_CLUSTER_NAME = System.getProperty(PROPNAME_STORAGE_CLUSTER_NAME,"relatedproducts");
    private final String STORAGE_FREQUENTLY_RELATED_PRODUCTS_FACET_RESULTS_FACET_NAME =  System.getProperty(PROPNAME_STORAGE_FREQUENTLY_RELATED_PRODUCTS_FACET_RESULTS_FACET_NAME,"frequently-related-with");
    private final String STORAGE_FACET_SEARCH_EXECUTION_HINT = System.getProperty(PROPNAME_STORAGE_FACET_SEARCH_EXECUTION_HINT);

    private final String KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR = System.getProperty(PROPNAME_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR,"related-with");
    private final String KEY_FOR_INDEX_REQUEST_DATE_ATTR = System.getProperty(PROPNAME_KEY_FOR_INDEX_REQUEST_DATE_ATTR,"date");
    private final String KEY_FOR_INDEX_REQUEST_ID_ATTR = System.getProperty(PROPNAME_KEY_FOR_INDEX_REQUEST_ID_ATTR,"id");
    private final String KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR = System.getProperty(PROPNAME_KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR,"products");

    private final String ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME = System.getProperty(PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME,"default-transport-elasticsearch.yml");
    private final String ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME = System.getProperty(PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME,"default-node-elasticsearch.yml");
    private final String ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME = System.getProperty(PROPNAME_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME,"elasticsearch.yml");

    private final long FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS = Long.valueOf(System.getProperty(PROPNAME_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS, "5000"));

    // can be "day|hour|minute"
    private final String RELATED_PRODUCT_STORAGE_LOCATION_MAPPER = System.getProperty(PROPNAME_RELATED_PRODUCT_STORAGE_LOCATION_MAPPER,"day");

    private final int TIMED_OUT_SEARCH_REQUEST_STATUS_CODE = Integer.valueOf(System.getProperty(PROPNAME_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE, "504"));
    private final int FAILED_SEARCH_REQUEST_STATUS_CODE = Integer.valueOf(System.getProperty(PROPNAME_FAILED_SEARCH_REQUEST_STATUS_CODE,"502"));
    private final int NO_FOUND_SEARCH_REQUEST_STATUS_CODE = Integer.valueOf(System.getProperty(PROPNAME_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE,"404"));
    private final int FOUND_SEARCH_REQUEST_STATUS_CODE = Integer.valueOf(System.getProperty(PROPNAME_FOUND_SEARCH_REQUEST_STATUS_CODE,"200"));
    private final int MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE = Integer.valueOf(System.getProperty(PROPNAME_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE,"500"));

    // string Encoding for the properties
    private final String PROPERTY_ENCODING = System.getProperty(PROPNAME_PROPERTY_ENCODING,"UTF-8");

    private final int[] searchRequestResponseCodes = new int[SearchResultsOutcome.values().length];

    private final String WAIT_STRATEGY = System.getProperty(PROPNAME_WAIT_STRATEGY,"yield").toLowerCase();

    private final String ES_CLIENT_TYPE = System.getProperty(PROPNAME_ES_CLIENT_TYPE,"transport").toLowerCase();

    private final boolean INDEXNAME_DATE_CACHING_ENABLED = Boolean.parseBoolean(System.getProperty(PROPNAME_INDEXNAME_DATE_CACHING_ENABLED,"true"));

    private final int NUMBER_OF_INDEXNAMES_TO_CACHE = Integer.valueOf(System.getProperty(PROPNAME_NUMBER_OF_INDEXNAMES_TO_CACHE,"365"));

    private final boolean REPLACE_OLD_INDEXED_CONTENT = Boolean.parseBoolean(System.getProperty(PROPNAME_REPLACE_OLD_INDEXED_CONTENT, "false"));

    private final boolean SEPARATE_INDEXING_THREAD  = Boolean.parseBoolean(System.getProperty(PROPNAME_SEPARATE_INDEXING_THREAD,"false"));

    private final boolean DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS =  Boolean.parseBoolean(System.getProperty(PROPNAME_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS,"false"));

    private final String ELASTIC_SEARCH_TRANSPORT_HOSTS = System.getProperty(PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS,"127.0.0.1:9300");

    private final int DEFAULT_ELASTIC_SEARCH_PORT = Integer.valueOf(System.getProperty(PROPNAME_DEFAULT_ELASTIC_SEARCH_PORT,"9300"));

    private final boolean USE_SHARED_SEARCH_REPOSITORY = Boolean.parseBoolean(System.getProperty(PROPNAME_USE_SHARED_SEARCH_REPOSITORY,"false"));

    private final boolean RELATED_PRODUCT_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED = Boolean.parseBoolean(System.getProperty(PROPNAME_RELATED_PRODUCT_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED,"false"));

    private final WaitStrategyFactory waitStrategyFactory;

    private final ElasticeSearchClientType esClientType;

    public SystemPropertiesConfiguration() {
        searchRequestResponseCodes[SearchResultsOutcome.EMPTY_RESULTS.getIndex()] = NO_FOUND_SEARCH_REQUEST_STATUS_CODE;
        searchRequestResponseCodes[SearchResultsOutcome.FAILED_REQUEST.getIndex()] = FAILED_SEARCH_REQUEST_STATUS_CODE;
        searchRequestResponseCodes[SearchResultsOutcome.REQUEST_TIMEOUT.getIndex()] = TIMED_OUT_SEARCH_REQUEST_STATUS_CODE;
        searchRequestResponseCodes[SearchResultsOutcome.HAS_RESULTS.getIndex()] = FOUND_SEARCH_REQUEST_STATUS_CODE;
        searchRequestResponseCodes[SearchResultsOutcome.MISSING_SEARCH_RESULTS_HANDLER.getIndex()] = MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE;


        if(WAIT_STRATEGY.contains("yield")) {
            waitStrategyFactory = new DefaultWaitStrategyFactory(DefaultWaitStrategyFactory.WAIT_STRATEGY_TYPE.YIELDING);
        }
        else if(WAIT_STRATEGY.contains("block")) {
            waitStrategyFactory = new DefaultWaitStrategyFactory(DefaultWaitStrategyFactory.WAIT_STRATEGY_TYPE.BLOCKING);
        }
        else if(WAIT_STRATEGY.contains("sleep")) {
            waitStrategyFactory = new DefaultWaitStrategyFactory(DefaultWaitStrategyFactory.WAIT_STRATEGY_TYPE.SLEEPING);
        }
        else if (WAIT_STRATEGY.contains("busy")) {
            waitStrategyFactory = new DefaultWaitStrategyFactory(DefaultWaitStrategyFactory.WAIT_STRATEGY_TYPE.BUSY);
        }
        else {
            waitStrategyFactory = new DefaultWaitStrategyFactory(DefaultWaitStrategyFactory.WAIT_STRATEGY_TYPE.YIELDING);
        }

        if(ES_CLIENT_TYPE.equals("transport")) {
            esClientType = ElasticeSearchClientType.TRANSPORT;
        } else if(ES_CLIENT_TYPE.equals("node")) {
            esClientType = ElasticeSearchClientType.NODE;
        } else {
            esClientType = ElasticeSearchClientType.TRANSPORT;
        }

    }


    public WaitStrategyFactory getWaitStrategyFactory() {
        return waitStrategyFactory;
    }

    public int getMaxNumberOfSearchCriteriaForRelatedContent() {
        return MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT;
    }

    public int getSizeOfRelatedContentSearchRequestHandlerQueue() {
        return SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE;
    }

    public int getSizeOfRelatedContentSearchRequestQueue() {
        return SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE;
    }

    @Override
    public int getSizeOfRelatedContentSearchRequestAndResponseQueue() {
        return SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE;
    }

    @Override
    public int getSizeOfResponseProcessingQueue() {
        return SIZE_OF_RESPONSE_PROCESSING_QUEUE;
    }

    @Override
    public int getNumberOfSearchingRequestProcessors() {
        return NUMBER_OF_SEARCHING_REQUEST_PROCESSORS;
    }

    @Override
    public int getNumberOfExpectedLikeForLikeRequests() {
        return NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS;
    }


    @Override
    public String getKeyForFrequencyResultOccurrence() {
        return KEY_FOR_FREQUENCY_RESULT_OCCURRENCE;
    }

    @Override
    public String getKeyForFrequencyResultId() {
        return KEY_FOR_FREQUENCY_RESULT_ID;
    }

    @Override
    public String getKeyForFrequencyResults() {
        return KEY_FOR_FREQUENCY_RESULTS;
    }


    @Override
    public String getKeyForFrequencyResultOverallResultsSize() {
        return KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_PRODUCTS;
    }

    @Override
    public String getKeyForIndexRequestRelatedWithAttr() {
        return KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR;
    }


    @Override
    public String getKeyForIndexRequestDateAttr() {
        return KEY_FOR_INDEX_REQUEST_DATE_ATTR;
    }

    @Override
    public String getKeyForIndexRequestIdAttr() {
        return KEY_FOR_INDEX_REQUEST_ID_ATTR;
    }

    @Override
    public String getKeyForIndexRequestProductArrayAttr() {
        return KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR;
    }

    @Override
    public String getRequestParameterForSize() {
        return REQUEST_PARAMETER_FOR_SIZE;
    }

    @Override
    public String getRequestParameterForId() {
        return REQUEST_PARAMETER_FOR_ID;
    }

    @Override
    public int getDefaultNumberOfResults() {
        return DEFAULT_NUMBER_OF_RESULTS;
    }

    @Override
    public String getStorageIndexNamePrefix() {
        return STORAGE_INDEX_NAME_PREFIX;
    }

    @Override
    public String getStorageIndexNameAlias() {
        return STORAGE_INDEX_NAME_ALIAS;
    }

    @Override
    public String getStorageContentTypeName() {
        return STORAGE_CONTENT_TYPE_NAME;
    }

    @Override
    public String getStorageClusterName() {
        return STORAGE_CLUSTER_NAME;
    }

    @Override
    public String getStorageFrequentlyRelatedProductsFacetResultsFacetName() {
        return STORAGE_FREQUENTLY_RELATED_PRODUCTS_FACET_RESULTS_FACET_NAME;
    }

    @Override
    public String getElasticSearchClientDefaultNodeSettingFileName() {
        return ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME;
    }

    @Override
    public String getElasticSearchClientDefaultTransportSettingFileName() {
        return ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME;
    }

    @Override
    public String getElasticSearchClientOverrideSettingFileName() {
        return ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME;
    }

    @Override
    public String getStorageLocationMapper() {
        return RELATED_PRODUCT_STORAGE_LOCATION_MAPPER;
    }

    @Override
    public long getFrequentlyRelatedProductsSearchTimeoutInMillis() {
        return FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS;
    }

    @Override
    public int getResponseCode(SearchResultsOutcome type) {
        return  searchRequestResponseCodes[type.getIndex()];
    }

    @Override
    public int getTimedOutSearchRequestStatusCode() {
        return TIMED_OUT_SEARCH_REQUEST_STATUS_CODE;
    }

    @Override
    public int getFailedSearchRequestStatusCode() {
        return FAILED_SEARCH_REQUEST_STATUS_CODE;
    }

    @Override
    public int getNoFoundSearchResultsStatusCode() {
        return NO_FOUND_SEARCH_REQUEST_STATUS_CODE;
    }

    @Override
    public int getFoundSearchResultsStatusCode() {
        return FOUND_SEARCH_REQUEST_STATUS_CODE;
    }

    @Override
    public int getMissingSearchResultsHandlerStatusCode() {
        return MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE;
    }

    @Override
    public int getNumberOfIndexingRequestProcessors() {
        return NUMBER_OF_INDEXING_REQUEST_PROCESSORS;
    }

    @Override
    public int getMaxNumberOfRelatedProductProperties() {
        return MAX_NUMBER_OF_RELATED_PRODUCT_PROPERTIES;
    }

    @Override
    public int getMaxNumberOfRelatedProductsPerPurchase() {
        return MAX_NUMBER_OF_RELATED_PRODUCTS_PER_PURCHASE;
    }

    @Override
    public int getRelatedProductIdLength() {
        return RELATED_PRODUCT_ID_LENGTH;
    }

    @Override
    public String getRelatedProductInvalidIdString() {
        return RELATED_PRODUCT_INVALID_ID_STRING;
    }

    @Override
    public int getMaxRelatedProductPostDataSizeInBytes() {
        return MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES;
    }

    @Override
    public int getMinRelatedProductPostDataSizeInBytes() {
        return MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES;
    }

    @Override
    public int getRelatedProductAdditionalPropertyKeyLength() {
        return RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH;
    }

    @Override
    public int getRelatedProductAdditionalPropertyValueLength() {
        return RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH;
    }

    @Override
    public boolean isSearchResponseDebugOutputEnabled() {
        return RELATED_PRODUCT_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED;
    }

    @Override
    public int getIndexBatchSize() {
        return BATCH_INDEX_SIZE;
    }

    @Override
    public int getSizeOfBatchIndexingRequestQueue() {
        return SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE;
    }

    @Override
    public int getSizeOfIncomingMessageQueue() {
        return SIZE_OF_INCOMING_REQUEST_QUEUE;
    }

    @Override
    public String getPropertyEncoding() {
        return PROPERTY_ENCODING;
    }

    @Override
    public boolean isIndexNameDateCachingEnabled() {
        return INDEXNAME_DATE_CACHING_ENABLED;
    }

    @Override
    public int getNumberOfIndexNamesToCache() {
        return NUMBER_OF_INDEXNAMES_TO_CACHE;
    }

    @Override
    public boolean getShouldReplaceOldContentIfExists() {
        return REPLACE_OLD_INDEXED_CONTENT;
    }

    @Override
    public boolean getShouldUseSeparateIndexStorageThread() {
        return SEPARATE_INDEXING_THREAD;
    }

    @Override
    public boolean shouldDiscardIndexRequestWithTooManyRelations() {
        return DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS;
    }

    @Override
    public ElasticeSearchClientType getElasticSearchClientType() {
        return esClientType;
    }

    @Override
    public String getElasticSearchTransportHosts() {
        return ELASTIC_SEARCH_TRANSPORT_HOSTS;
    }

    @Override
    public int getDefaultElasticSearchPort() {
        return DEFAULT_ELASTIC_SEARCH_PORT;
    }

    @Override
    public boolean useSharedSearchRepository() {
        return USE_SHARED_SEARCH_REPOSITORY;
    }

    @Override
    public boolean isSafeToOutputRequestData() {
        return SAFE_TO_OUTPUT_REQUEST_DATA;
    }

    @Override
    public String getStorageFacetExecutionHint() {
        return STORAGE_FACET_SEARCH_EXECUTION_HINT;
    }
}
