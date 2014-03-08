/*
 *
 *  * Licensed to Relateit under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Relateit licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.greencheek.related.util.config;

import org.elasticsearch.common.unit.TimeValue;
import org.greencheek.related.api.searching.SearchResultsOutcome;
import org.greencheek.related.util.arrayindexing.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.greencheek.related.util.config.ConfigurationConstants.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**

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

    private final boolean SAFE_TO_OUTPUT_REQUEST_DATA ;
    private final int MAX_NUMBER_OF_RELATED_PRODUCT_PROPERTIES ;
    private final int MAX_NUMBER_OF_RELATED_PRODUCTS_PER_PURCHASE ;
    private final int RELATED_PRODUCT_ID_LENGTH ;
    private final int MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES ;
    private final int MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES ;

    private final int RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH ;
    private final int RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH ;
    private final int SIZE_OF_INCOMING_REQUEST_QUEUE ;
    private final int SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE ;
    private final int BATCH_INDEX_SIZE ;

    private final int SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE ;

    private final int SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE ;
    private final int SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE ;

    private final int MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT ;
    private final int NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS ;

    private final String KEY_FOR_FREQUENCY_RESULT_ID ;
    private final String KEY_FOR_FREQUENCY_RESULT_OCCURRENCE ;
    private final String KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_PRODUCTS ;
    private final String KEY_FOR_FREQUENCY_RESULTS ;

    private final String REQUEST_PARAMETER_FOR_SIZE ;
    private final String REQUEST_PARAMETER_FOR_ID ;

    private final int DEFAULT_NUMBER_OF_RESULTS ;
    private final int SIZE_OF_RESPONSE_PROCESSING_QUEUE ;
    private final int NUMBER_OF_INDEXING_REQUEST_PROCESSORS ;
    private final int NUMBER_OF_SEARCHING_REQUEST_PROCESSORS ;

    private final String STORAGE_INDEX_NAME_PREFIX ;
    private final String STORAGE_INDEX_NAME_ALIAS ;
    private final String STORAGE_CONTENT_TYPE_NAME ;
    private final String STORAGE_CLUSTER_NAME ;
    private final String STORAGE_FREQUENTLY_RELATED_PRODUCTS_FACET_RESULTS_FACET_NAME ;
    private final String STORAGE_FACET_SEARCH_EXECUTION_HINT ;

    private final String KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR ;
    private final String KEY_FOR_INDEX_REQUEST_DATE_ATTR ;
    private final String KEY_FOR_INDEX_REQUEST_ID_ATTR ;
    private final String KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR ;
    private final String KEY_FOR_SEARCH_PROCESSING_TIME;
    private final String KEY_FOR_STORAGE_RESPONSE_TIME;
    private final String KEY_FOR_STORAGE_GET_RESPONSE_TIME;
    private final String KEY_FOR_FREQUENCY_RESULT_SOURCE;


    private final String ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME ;
    private final String ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME ;
    private final String ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME ;

    private final long FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS ;

    // can be "day|hour|minute";
    private final String RELATED_PRODUCT_STORAGE_LOCATION_MAPPER ;

    private final int TIMED_OUT_SEARCH_REQUEST_STATUS_CODE ;
    private final int FAILED_SEARCH_REQUEST_STATUS_CODE ;
    private final int NO_FOUND_SEARCH_REQUEST_STATUS_CODE ;
    private final int FOUND_SEARCH_REQUEST_STATUS_CODE ;
    private final int MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE ;

    // string Encoding for the properties;
    private final String PROPERTY_ENCODING ;


    private final String WAIT_STRATEGY ;

    private final String ES_CLIENT_TYPE ;

    private final boolean INDEXNAME_DATE_CACHING_ENABLED ;

    private final int NUMBER_OF_INDEXNAMES_TO_CACHE ;

    private final boolean REPLACE_OLD_INDEXED_CONTENT ;

    private final boolean SEPARATE_INDEXING_THREAD  ;

    private final boolean DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS ;

    private final String ELASTIC_SEARCH_TRANSPORT_HOSTS ;

    private final int DEFAULT_ELASTIC_SEARCH_PORT ;

    private final String ELASTIC_SEARCH_HTTP_HOSTS;

    private final int ELASTIC_SEARCH_HTTP_DEFAULT_PORT;

    private final boolean USE_SHARED_SEARCH_REPOSITORY ;

    private final boolean RELATED_PRODUCT_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED ;


    private final int ELASTIC_SEARCH_HTTP_REQUEST_TIMEOUT_MS;
    private final int ELASTIC_SEARCH_HTTP_CONNECT_TIMEOUT_MS;
    private final int ELASTIC_SEARCH_HTTP_NO_OF_RETRIES;
    private final boolean ELASTIC_SEARCH_HTTP_FOLLOW_REDIRECTS;
    private final boolean ELASTIC_SEARCH_HTTP_CONNECTION_POOL_ENABLED;
    private final boolean ELASTIC_SEARCH_HTTP_COMPRESSION_ENABLED;

    private final int ELASTIC_SEARCH_HTTP_NODE_SNIFFING_REQUEST_TIMEOUT_MS;
    private final int ELASTIC_SEARCH_HTTP_NODE_SNIFFING_CONNECT_TIMEOUT_MS;
    private final int ELASTIC_SEARCH_HTTP_NODE_SNIFFING_NO_OF_RETRIES;

    private final String ELASTIC_SEARCH_HTTP_MULTISEARCH_ENDPOINT;
    private final String ELASTIC_SEARCH_HTTP_MULTIGET_ENDPOINT;


    private final TimeUnit ELASTIC_SEARCH_HTTP_NODE_SNIFFING_RETRY_INTERVAL_UNIT;
    private final String ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENDPOINT;
    private final int ELASTIC_SEARCH_HTTP_NODE_SNIFFING_RETRY_INTERVAL;
    private final boolean ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENABLED;

    private final int HTTP_ASYNC_INDEXING_REQUEST_TIMEOUT_MS;
    private final int HTTP_ASYNC_SEARCHING_REQUEST_TIMEOUT_MS;

    private final String DOCUMENT_INDEX_NAME;
    private final String DOCUMENT_TYPE_NAME;
    private final String DOCUMENT_MERGING_SCRIPT_NAME;
    private final boolean DOCUMENT_INDEXING_ENABLED;
    private final String DOCUMENT_COMPARISON_KEY_NAME;
    private final boolean DOCUMENT_REMOVE_DATE_ATTRIBUTE;



    private final int[] searchRequestResponseCodes = new int[SearchResultsOutcome.values().length];
    private final WaitStrategyFactory waitStrategyFactory;
    private final ElasticeSearchClientType esClientType;
    private static final Logger log = LoggerFactory.getLogger(SystemPropertiesConfiguration.class);


    public SystemPropertiesConfiguration() {
        this(getMergedSystemPropertiesAndDefaults());
    }

    private int getSearchRequestHandlerQueueSize(int searchProcessors,int searchRequestQueue, int searchRequestHandlerQueue) {
        if(searchRequestHandlerQueue==-1) {
            return Util.ceilingNextPowerOfTwo(searchRequestQueue/searchProcessors);
        } else {
            return Util.ceilingNextPowerOfTwo(searchRequestHandlerQueue);
        }
    }

    private int getSearchRequestAndResponseQueueSize(int searchRequestQueue, int searchRequestHandlerQueue) {
        if(searchRequestHandlerQueue==-1) {
            return Util.ceilingNextPowerOfTwo(searchRequestQueue*2);
        } else {
            return Util.ceilingNextPowerOfTwo(searchRequestHandlerQueue);
        }
    }


    /**
     * Initalises the Properties object with the given properties.
     *
     * @param properties
     */
    protected SystemPropertiesConfiguration(Map<String,Object> properties) {
        NUMBER_OF_SEARCHING_REQUEST_PROCESSORS = Util.ceilingNextPowerOfTwo(getInt(properties, PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS, DEFAULT_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS));
        SAFE_TO_OUTPUT_REQUEST_DATA = getBoolean(properties, PROPNAME_SAFE_TO_OUTPUT_REQUEST_DATA, DEFAULT_SAFE_TO_OUTPUT_REQUEST_DATA);
        MAX_NUMBER_OF_RELATED_PRODUCT_PROPERTIES = getInt(properties, PROPNAME_MAX_NO_OF_RELATED_ITEM_PROPERTES, DEFAULT_MAX_NO_OF_RELATED_ITEM_PROPERTES);
        MAX_NUMBER_OF_RELATED_PRODUCTS_PER_PURCHASE = getInt(properties, PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_INDEX_REQUEST, DEFAULT_MAX_NO_OF_RELATED_ITEMS_PER_INDEX_REQUEST);
        RELATED_PRODUCT_ID_LENGTH = getInt(properties, PROPNAME_RELATED_ITEM_ID_LENGTH, DEFAULT_RELATED_ITEM_ID_LENGTH);
        MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES = getInt(properties, PROPNAME_MAX_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES, DEFAULT_MAX_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES);
        MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES = getInt(properties, PROPNAME_MIN_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES, DEFAULT_MIN_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES);
        RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH = getInt(properties, PROPNAME_RELATED_ITEM_ADDITIONAL_PROPERTY_KEY_LENGTH, DEFAULT_RELATED_ITEM_ADDITIONAL_PROPERTY_KEY_LENGTH);
        RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH = getInt(properties, PROPNAME_RELATED_ITEM_ADDITIONAL_PROPERTY_VALUE_LENGTH, DEFAULT_RELATED_ITEM_ADDITIONAL_PROPERTY_VALUE_LENGTH);
        SIZE_OF_INCOMING_REQUEST_QUEUE = getInt(properties,PROPNAME_SIZE_OF_INCOMING_REQUEST_QUEUE,DEFAULT_SIZE_OF_INCOMING_REQUEST_QUEUE);
        SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE = getInt(properties,PROPNAME_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE,DEFAULT_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE);
        BATCH_INDEX_SIZE = getInt(properties, PROPNAME_BATCH_INDEX_SIZE, DEFAULT_BATCH_INDEX_SIZE);
        SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE = Util.ceilingNextPowerOfTwo(getInt(properties, PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE, DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE));
        SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE = getSearchRequestHandlerQueueSize(NUMBER_OF_SEARCHING_REQUEST_PROCESSORS,SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE, getInt(properties, PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE, DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE));
        SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE = getSearchRequestAndResponseQueueSize(SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE, getInt(properties, PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE, DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE));
        MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT =  getInt(properties,PROPNAME_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT,DEFAULT_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT);
        NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS = getInt(properties, PROPNAME_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS, DEFAULT_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS);
        KEY_FOR_FREQUENCY_RESULT_ID = getString(properties,PROPNAME_KEY_FOR_FREQUENCY_RESULT_ID,DEFAULT_KEY_FOR_FREQUENCY_RESULT_ID);
        KEY_FOR_FREQUENCY_RESULT_OCCURRENCE = getString(properties,PROPNAME_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE,DEFAULT_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE);
        KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_PRODUCTS = getString(properties, PROPNAME_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_ITEMS, DEFAULT_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_ITEMS);
        KEY_FOR_FREQUENCY_RESULTS = getString(properties,PROPNAME_KEY_FOR_FREQUENCY_RESULTS,DEFAULT_KEY_FOR_FREQUENCY_RESULTS);
        KEY_FOR_FREQUENCY_RESULT_SOURCE = getString(properties,PROPNAME_KEY_FOR_FREQUENCY_RESULT_SOURCE,DEFAULT_KEY_FOR_FREQUENCY_RESULT_SOURCE);

        KEY_FOR_SEARCH_PROCESSING_TIME = getString(properties,PROPNAME_KEY_FOR_SEARCH_PROCESSING_TIME,DEFAULT_KEY_FOR_SEARCH_PROCESSING_TIME);
        KEY_FOR_STORAGE_RESPONSE_TIME = getString(properties,PROPNAME_KEY_FOR_STORAGE_RESPONSE_TIME,DEFAULT_KEY_FOR_STORAGE_RESPONSE_TIME);
        KEY_FOR_STORAGE_GET_RESPONSE_TIME = getString(properties,PROPNAME_KEY_FOR_STORAGE_GET_RESPONSE_TIME,DEFAULT_KEY_FOR_STORAGE_GET_RESPONSE_TIME);

        REQUEST_PARAMETER_FOR_SIZE = getString(properties,PROPNAME_REQUEST_PARAMETER_FOR_SIZE,DEFAULT_REQUEST_PARAMETER_FOR_SIZE);
        REQUEST_PARAMETER_FOR_ID = getString(properties, PROPNAME_REQUEST_PARAMETER_FOR_ID, DEFAULT_REQUEST_PARAMETER_FOR_ID);
        DEFAULT_NUMBER_OF_RESULTS = getInt(properties,PROPNAME_DEFAULT_NUMBER_OF_RESULTS,DEFAULT_DEFAULT_NUMBER_OF_RESULTS);
        SIZE_OF_RESPONSE_PROCESSING_QUEUE = getInt(properties, PROPNAME_SIZE_OF_RESPONSE_PROCESSING_QUEUE, DEFAULT_SIZE_OF_RESPONSE_PROCESSING_QUEUE);
        NUMBER_OF_INDEXING_REQUEST_PROCESSORS = getInt(properties, PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS, DEFAULT_NUMBER_OF_INDEXING_REQUEST_PROCESSORS);
        STORAGE_INDEX_NAME_PREFIX = getString(properties,PROPNAME_STORAGE_INDEX_NAME_PREFIX,DEFAULT_STORAGE_INDEX_NAME_PREFIX);
        STORAGE_INDEX_NAME_ALIAS = getString(properties,PROPNAME_STORAGE_INDEX_NAME_ALIAS,DEFAULT_STORAGE_INDEX_NAME_ALIAS);
        STORAGE_CONTENT_TYPE_NAME = getString(properties,PROPNAME_STORAGE_CONTENT_TYPE_NAME,DEFAULT_STORAGE_CONTENT_TYPE_NAME);
        STORAGE_CLUSTER_NAME = getString(properties,PROPNAME_STORAGE_CLUSTER_NAME,DEFAULT_STORAGE_CLUSTER_NAME);
        STORAGE_FREQUENTLY_RELATED_PRODUCTS_FACET_RESULTS_FACET_NAME =  getString(properties, PROPNAME_STORAGE_FREQUENTLY_RELATED_ITEMS_FACET_RESULTS_FACET_NAME, DEFAULT_STORAGE_FREQUENTLY_RELATED_ITEMS_FACET_RESULTS_FACET_NAME);
        STORAGE_FACET_SEARCH_EXECUTION_HINT = getString(properties,PROPNAME_STORAGE_FACET_SEARCH_EXECUTION_HINT,DEFAULT_STORAGE_FACET_SEARCH_EXECUTION_HINT);
        KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR = getString(properties,PROPNAME_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR,DEFAULT_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR);
        KEY_FOR_INDEX_REQUEST_DATE_ATTR = getString(properties,PROPNAME_KEY_FOR_INDEX_REQUEST_DATE_ATTR,DEFAULT_KEY_FOR_INDEX_REQUEST_DATE_ATTR);
        KEY_FOR_INDEX_REQUEST_ID_ATTR = getString(properties,PROPNAME_KEY_FOR_INDEX_REQUEST_ID_ATTR,DEFAULT_KEY_FOR_INDEX_REQUEST_ID_ATTR);
        KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR = getString(properties, PROPNAME_KEY_FOR_INDEX_REQUEST_ITEM_ARRAY_ATTR, DEFAULT_KEY_FOR_INDEX_REQUEST_ITEM_ARRAY_ATTR);
        ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME = getString(properties,PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME,DEFAULT_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME);
        ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME = getString(properties,PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME,DEFAULT_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME);
        ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME = getString(properties,PROPNAME_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME,DEFAULT_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME);
        FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS = getLong(properties,PROPNAME_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS,DEFAULT_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS);
        RELATED_PRODUCT_STORAGE_LOCATION_MAPPER = getString(properties, PROPNAME_RELATED_ITEM_STORAGE_LOCATION_MAPPER, DEFAULT_RELATED_ITEM_STORAGE_LOCATION_MAPPER);
        TIMED_OUT_SEARCH_REQUEST_STATUS_CODE = getInt(properties,PROPNAME_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE,DEFAULT_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE);
        FAILED_SEARCH_REQUEST_STATUS_CODE = getInt(properties,PROPNAME_FAILED_SEARCH_REQUEST_STATUS_CODE,DEFAULT_FAILED_SEARCH_REQUEST_STATUS_CODE);
        NO_FOUND_SEARCH_REQUEST_STATUS_CODE = getInt(properties,PROPNAME_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE,DEFAULT_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE);
        FOUND_SEARCH_REQUEST_STATUS_CODE = getInt(properties,PROPNAME_FOUND_SEARCH_REQUEST_STATUS_CODE,DEFAULT_FOUND_SEARCH_REQUEST_STATUS_CODE);
        MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE = getInt(properties,PROPNAME_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE,DEFAULT_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE);

        PROPERTY_ENCODING = getString(properties,PROPNAME_PROPERTY_ENCODING,DEFAULT_PROPERTY_ENCODING);
        WAIT_STRATEGY = getString(properties,PROPNAME_WAIT_STRATEGY,DEFAULT_WAIT_STRATEGY).toLowerCase();
        ES_CLIENT_TYPE = getString(properties,PROPNAME_ES_CLIENT_TYPE,DEFAULT_ES_CLIENT_TYPE).toLowerCase();
        INDEXNAME_DATE_CACHING_ENABLED = getBoolean(properties,PROPNAME_INDEXNAME_DATE_CACHING_ENABLED,DEFAULT_INDEXNAME_DATE_CACHING_ENABLED);
        NUMBER_OF_INDEXNAMES_TO_CACHE = getInt(properties,PROPNAME_NUMBER_OF_INDEXNAMES_TO_CACHE,DEFAULT_NUMBER_OF_INDEXNAMES_TO_CACHE);
        REPLACE_OLD_INDEXED_CONTENT = getBoolean(properties,PROPNAME_REPLACE_OLD_INDEXED_CONTENT,DEFAULT_REPLACE_OLD_INDEXED_CONTENT);
        SEPARATE_INDEXING_THREAD  = getBoolean(properties,PROPNAME_SEPARATE_INDEXING_THREAD,DEFAULT_SEPARATE_INDEXING_THREAD);
        DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS =  getBoolean(properties, PROPNAME_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_ITEMS, DEFAULT_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_ITEMS);
        ELASTIC_SEARCH_TRANSPORT_HOSTS = getString(properties,PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS,DEFAULT_ELASTIC_SEARCH_TRANSPORT_HOSTS);
        DEFAULT_ELASTIC_SEARCH_PORT = getInt(properties,PROPNAME_DEFAULT_ELASTIC_SEARCH_PORT,DEFAULT_DEFAULT_ELASTIC_SEARCH_PORT);
        ELASTIC_SEARCH_HTTP_HOSTS = getString(properties,PROPNAME_ELASTIC_SEARCH_HTTP_HOSTS,DEFAULT_ELASTIC_SEARCH_HTTP_HOSTS);
        ELASTIC_SEARCH_HTTP_DEFAULT_PORT = getInt(properties,PROPNAME_ELASTIC_SEARCH_HTTP_DEFAULT_PORT,DEFAULT_ELASTIC_SEARCH_HTTP_DEFAULT_PORT);
        USE_SHARED_SEARCH_REPOSITORY = getBoolean(properties,PROPNAME_USE_SHARED_SEARCH_REPOSITORY,DEFAULT_USE_SHARED_SEARCH_REPOSITORY);
        RELATED_PRODUCT_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED = getBoolean(properties, PROPNAME_RELATED_ITEM_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED, DEFAULT_RELATED_ITEM_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED);


        ELASTIC_SEARCH_HTTP_REQUEST_TIMEOUT_MS = getInt(properties,PROPNAME_ELASTIC_SEARCH_HTTP_REQUEST_TIMEOUT_MS,DEFAULT_ELASTIC_SEARCH_HTTP_REQUEST_TIMEOUT_MS);
        ELASTIC_SEARCH_HTTP_CONNECT_TIMEOUT_MS = getInt(properties,PROPNAME_ELASTIC_SEARCH_HTTP_CONNECT_TIMEOUT_MS,DEFAULT_ELASTIC_SEARCH_HTTP_CONNECT_TIMEOUT_MS);
        ELASTIC_SEARCH_HTTP_NO_OF_RETRIES = getInt(properties,PROPNAME_ELASTIC_SEARCH_HTTP_NO_OF_RETRIES,DEFAULT_ELASTIC_SEARCH_HTTP_NO_OF_RETRIES);
        ELASTIC_SEARCH_HTTP_FOLLOW_REDIRECTS = getBoolean(properties,PROPNAME_ELASTIC_SEARCH_HTTP_FOLLOW_REDIRECTS,DEFAULT_ELASTIC_SEARCH_HTTP_FOLLOW_REDIRECTS);
        ELASTIC_SEARCH_HTTP_CONNECTION_POOL_ENABLED = getBoolean(properties,PROPNAME_ELASTIC_SEARCH_HTTP_CONNECTION_POOL_ENABLED,DEFAULT_ELASTIC_SEARCH_HTTP_CONNECTION_POOL_ENABLED);
        ELASTIC_SEARCH_HTTP_COMPRESSION_ENABLED = getBoolean(properties,PROPNAME_ELASTIC_SEARCH_HTTP_COMPRESSION_ENABLED,DEFAULT_ELASTIC_SEARCH_HTTP_COMPRESSION_ENABLED);

        ELASTIC_SEARCH_HTTP_NODE_SNIFFING_REQUEST_TIMEOUT_MS = getInt(properties,PROPNAME_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_REQUEST_TIMEOUT_MS,DEFAULT_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_REQUEST_TIMEOUT_MS);
        ELASTIC_SEARCH_HTTP_NODE_SNIFFING_CONNECT_TIMEOUT_MS = getInt(properties,PROPNAME_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_CONNECT_TIMEOUT_MS,DEFAULT_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_CONNECT_TIMEOUT_MS);
        ELASTIC_SEARCH_HTTP_NODE_SNIFFING_NO_OF_RETRIES = getInt(properties,PROPNAME_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_NO_OF_RETRIES,DEFAULT_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_NO_OF_RETRIES);

        ELASTIC_SEARCH_HTTP_MULTISEARCH_ENDPOINT = getString(properties,PROPNAME_ELASTIC_SEARCH_HTTP_MULTISEARCH_ENDPOINT,DEFAULT_ELASTIC_SEARCH_HTTP_MULTISEARCH_ENDPOINT);
        ELASTIC_SEARCH_HTTP_MULTIGET_ENDPOINT = getString(properties,PROPNAME_ELASTIC_SEARCH_HTTP_MULTIGET_ENDPOINT,DEFAULT_ELASTIC_SEARCH_HTTP_MULTIGET_ENDPOINT);


        ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENDPOINT = getString(properties, PROPNAME_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENDPOINT, DEFAULT_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENDPOINT);
        ELASTIC_SEARCH_HTTP_NODE_SNIFFING_RETRY_INTERVAL = getInt(properties, PROPNAME_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_RETRY_INTERVAL, DEFAULT_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_RETRY_INTERVAL);
        ELASTIC_SEARCH_HTTP_NODE_SNIFFING_RETRY_INTERVAL_UNIT = parseIntervalUnit(getString(properties, PROPNAME_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_RETRY_INTERVAL_UNIT, DEFAULT_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_RETRY_INTERVAL_UNIT));
        ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENABLED = getBoolean(properties, PROPNAME_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENABLED, DEFAULT_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENABLED);

        HTTP_ASYNC_INDEXING_REQUEST_TIMEOUT_MS = getInt(properties,PROPNAME_HTTP_ASYNC_INDEXING_REQUEST_TIMEOUT_MS,DEFAULT_HTTP_ASYNC_INDEXING_REQUEST_TIMEOUT_MS);
        HTTP_ASYNC_SEARCHING_REQUEST_TIMEOUT_MS = getInt(properties,PROPNAME_HTTP_ASYNC_SEARCHING_REQUEST_TIMEOUT_MS,DEFAULT_HTTP_ASYNC_SEARCHING_REQUEST_TIMEOUT_MS);

        DOCUMENT_INDEX_NAME = getString(properties, PROPNAME_DOCUMENT_INDEX_NAME,DEFAULT_DOCUMENT_INDEX_NAME);
        DOCUMENT_TYPE_NAME = getString(properties, PROPNAME_DOCUMENT_TYPE_NAME,DEFAULT_DOCUMENT_TYPE_NAME);;
        DOCUMENT_MERGING_SCRIPT_NAME = getString(properties, PROPNAME_DOCUMENT_MERGING_SCRIPT_NAME,DEFAULT_DOCUMENT_MERGING_SCRIPT_NAME);
        DOCUMENT_INDEXING_ENABLED = getBoolean(properties, PROPNAME_DOCUMENT_INDEXING_ENABLED,DEFAULT_DOCUMENT_INDEXING_ENABLED);
        DOCUMENT_COMPARISON_KEY_NAME = getString(properties, PROPNAME_DOCUMENT_COMPARISON_KEY_NAME,DEFAULT_DOCUMENT_COMPARISON_KEY_NAME);

        DOCUMENT_REMOVE_DATE_ATTRIBUTE = getBoolean(properties, PROPNAME_DOCUMENT_REMOVE_DATE_ATTRIBUTE, DEFAULT_DOCUMENT_REMOVE_DATE_ATTRIBUTE);


        setResponseCodes(NO_FOUND_SEARCH_REQUEST_STATUS_CODE, FAILED_SEARCH_REQUEST_STATUS_CODE,
                TIMED_OUT_SEARCH_REQUEST_STATUS_CODE, MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE,
                FOUND_SEARCH_REQUEST_STATUS_CODE);

        waitStrategyFactory = parseWaitStrategy(WAIT_STRATEGY);
        esClientType = parseEsClientType(ES_CLIENT_TYPE);
    }

    private static Map<String,Object> getMergedSystemPropertiesAndDefaults() {
        Map<String,Object> defaultProperties = new HashMap(ConfigurationConstants.DEFAULT_SETTINGS);
        Map<String,Object> systemProperties = parseSystemProperties();

        return replaceProperties(defaultProperties, systemProperties);
    }

    private static boolean getBoolean(Map<String,Object> properties,String prop, boolean defaultValue) {
        Object value = properties.get(prop);
        if(value==null) return defaultValue;
        else {
            try {
                return ((Boolean)value).booleanValue();
            } catch(ClassCastException e) {
                log.warn("Failed to cast value for property {} to a boolean",prop);
                return defaultValue;
            }
        }
    }

    private static int getInt(Map<String,Object> properties,String prop, int defaultValue) {
        Object value = properties.get(prop);
        if(value==null) return defaultValue;
        else {
            try {
                return ((Integer)value).intValue();
            } catch(ClassCastException e) {
                log.warn("Failed to cast value for property {} to a int",prop);
                return defaultValue;
            }
        }
    }

    private static long getLong(Map<String,Object> properties,String prop, long defaultValue) {
        Object value = properties.get(prop);
        if(value==null) return defaultValue;
        else {
            try {
                return ((Long)value).longValue();
            } catch(ClassCastException e) {
                log.warn("Failed to cast value for property {} to a int",prop);
                return defaultValue;
            }
        }
    }

    private static String getString(Map<String,Object> properties,String prop, String defaultValue) {
        Object value = properties.get(prop);
        if(value==null) return defaultValue;
        else {
            try {
                return (String)value;
            } catch(ClassCastException e) {
                log.warn("Failed to cast value for property {} to a int",prop);
                return defaultValue;
            }
        }
    }


    /**
     * returns a new map that is a combination of the values from the first parameter {@see defaultProperties}
     * with the values override by the one that are specified in {@see overrides}
     *
     * @param defaultProperties The map will contain all these keys.
     * @param overrides the properties that are to replace those in the defaults.  This may be a sub set of the
     *                  properties in the defaultProperties, or new values.
     * @return map with replaced properties
     */
    public static Map<String,Object> mergeProperties(Map<String,Object> defaultProperties,
                                              Map<String,Object> overrides) {
        Map<String,Object> props = new HashMap<String,Object>(defaultProperties);
        if(overrides==null || overrides.size()==0) return props;
        for(String prop : overrides.keySet()) {
            props.put(prop,overrides.get(prop));
        }
        return props;
    }

    /**
     * returns a new map that is a combination of the values from the first parameter {@see defaultProperties}
     * with the values override by the one that are specified in {@see overrides}.  Only if the property exists in the
     * defaultProperties will if be replaced.  In other words. any new property in the overrides that is not in
     * defaultProperties will not be present in the returned array
     *
     * @param defaultProperties The map will contain all these keys.
     * @param overrides the properties that are to replace those in the defaults.  This may be a sub set of the
     *                  properties in the defaultProperties, or new values.
     * @return map with replaced properties
     */
    public static Map<String,Object> replaceProperties(Map<String,Object> defaultProperties,
                                              Map<String,Object> overrides) {
        Map<String,Object> props = new HashMap<String,Object>(defaultProperties);
        if(overrides==null || overrides.size()==0) return props;
        for(String prop : overrides.keySet()) {
            if(props.containsKey(prop)) props.put(prop,overrides.get(prop));
        }
        return props;
    }


    private static Map<String,String> readSystemProperties() {
        Map<String,String> props = new HashMap<String,String>(100);
        for(String key : ConfigurationConstants.DEFAULT_SETTINGS.keySet()) {
            String value = System.getProperty(key);
            if(value!=null) {
                props.put(key,value);
            }
        }
        return props;
    }





    /**
     * Looks for System properties with the names defined in {@link org.greencheek.related.util.config.ConfigurationConstants}
     * Parsing the resulting values in to appropriate types.  If the system property is not defined an
     * entry in the return map (with the same name as the constant in ConfigurationContants), is not created.
     * If a system property is defined an entry in the map will exist.
     *
     * @return
     */
    public static Map<String,Object> parseSystemProperties() {
        Map<String,String> stringSystemProperties = readSystemProperties();
        return parseProperties(stringSystemProperties);
    }

    public static Map<String,Object> parseProperties(Map<String,String> propertiesToConvert) {
        Map<String,Object> parsedProperties = new HashMap<String,Object>();

        parseBoolean(parsedProperties, propertiesToConvert, PROPNAME_SAFE_TO_OUTPUT_REQUEST_DATA);
        parseInt(parsedProperties,propertiesToConvert, PROPNAME_MAX_NO_OF_RELATED_ITEM_PROPERTES);
        parseInt(parsedProperties, propertiesToConvert, PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_INDEX_REQUEST);
        parseInt(parsedProperties, propertiesToConvert, PROPNAME_RELATED_ITEM_ID_LENGTH);
        parseInt(parsedProperties,propertiesToConvert, PROPNAME_MAX_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES);
        parseInt(parsedProperties,propertiesToConvert, PROPNAME_MIN_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES);
        parseInt(parsedProperties,propertiesToConvert, PROPNAME_RELATED_ITEM_ADDITIONAL_PROPERTY_KEY_LENGTH);
        parseInt(parsedProperties,propertiesToConvert, PROPNAME_RELATED_ITEM_ADDITIONAL_PROPERTY_VALUE_LENGTH);
        parseInt(parsedProperties,propertiesToConvert,PROPNAME_SIZE_OF_INCOMING_REQUEST_QUEUE);
        parseInt(parsedProperties,propertiesToConvert,PROPNAME_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE);
        parseInt(parsedProperties,propertiesToConvert,PROPNAME_BATCH_INDEX_SIZE);
        parseInt(parsedProperties,propertiesToConvert,PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE);
        parseInt(parsedProperties,propertiesToConvert,PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE);
        parseInt(parsedProperties,propertiesToConvert,PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE);
        parseInt(parsedProperties,propertiesToConvert,PROPNAME_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT);
        parseInt(parsedProperties, propertiesToConvert, PROPNAME_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_KEY_FOR_FREQUENCY_RESULT_ID);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE);
        parseString(parsedProperties,propertiesToConvert, PROPNAME_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_ITEMS);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_KEY_FOR_FREQUENCY_RESULTS);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_REQUEST_PARAMETER_FOR_SIZE);
        parseString(parsedProperties, propertiesToConvert, PROPNAME_REQUEST_PARAMETER_FOR_ID);
        parseInt(parsedProperties,propertiesToConvert,PROPNAME_DEFAULT_NUMBER_OF_RESULTS);
        parseInt(parsedProperties,propertiesToConvert,PROPNAME_SIZE_OF_RESPONSE_PROCESSING_QUEUE);
        parseInt(parsedProperties,propertiesToConvert,PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS);
        parseInt(parsedProperties, propertiesToConvert, PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_STORAGE_INDEX_NAME_PREFIX);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_STORAGE_INDEX_NAME_ALIAS);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_STORAGE_CONTENT_TYPE_NAME);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_STORAGE_CLUSTER_NAME);
        parseString(parsedProperties,propertiesToConvert, PROPNAME_STORAGE_FREQUENTLY_RELATED_ITEMS_FACET_RESULTS_FACET_NAME);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_STORAGE_FACET_SEARCH_EXECUTION_HINT);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_KEY_FOR_INDEX_REQUEST_DATE_ATTR);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_KEY_FOR_INDEX_REQUEST_ID_ATTR);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_KEY_FOR_SEARCH_PROCESSING_TIME);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_KEY_FOR_STORAGE_RESPONSE_TIME);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_KEY_FOR_INDEX_REQUEST_ITEM_ARRAY_ATTR);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_KEY_FOR_FREQUENCY_RESULT_SOURCE);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_KEY_FOR_STORAGE_GET_RESPONSE_TIME);

        parseString(parsedProperties,propertiesToConvert,PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME);
        parseString(parsedProperties, propertiesToConvert, PROPNAME_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME);
        parseLong(parsedProperties, propertiesToConvert, PROPNAME_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS);
        parseString(parsedProperties, propertiesToConvert, PROPNAME_RELATED_ITEM_STORAGE_LOCATION_MAPPER);
        parseInt(parsedProperties,propertiesToConvert,PROPNAME_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE);
        parseInt(parsedProperties,propertiesToConvert,PROPNAME_FAILED_SEARCH_REQUEST_STATUS_CODE);
        parseInt(parsedProperties,propertiesToConvert,PROPNAME_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE);
        parseInt(parsedProperties,propertiesToConvert,PROPNAME_FOUND_SEARCH_REQUEST_STATUS_CODE);
        parseInt(parsedProperties, propertiesToConvert, PROPNAME_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_PROPERTY_ENCODING);
        parseString(parsedProperties,propertiesToConvert,PROPNAME_WAIT_STRATEGY);
        parseString(parsedProperties, propertiesToConvert, PROPNAME_ES_CLIENT_TYPE);
        parseBoolean(parsedProperties, propertiesToConvert, PROPNAME_INDEXNAME_DATE_CACHING_ENABLED);
        parseInt(parsedProperties, propertiesToConvert, PROPNAME_NUMBER_OF_INDEXNAMES_TO_CACHE);
        parseBoolean(parsedProperties,propertiesToConvert,PROPNAME_REPLACE_OLD_INDEXED_CONTENT);
        parseBoolean(parsedProperties,propertiesToConvert,PROPNAME_SEPARATE_INDEXING_THREAD);
        parseBoolean(parsedProperties, propertiesToConvert, PROPNAME_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_ITEMS);
        parseString(parsedProperties, propertiesToConvert, PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS);
        parseInt(parsedProperties, propertiesToConvert, PROPNAME_DEFAULT_ELASTIC_SEARCH_PORT);
        parseString(parsedProperties, propertiesToConvert, PROPNAME_ELASTIC_SEARCH_HTTP_HOSTS);
        parseInt(parsedProperties, propertiesToConvert, PROPNAME_ELASTIC_SEARCH_HTTP_DEFAULT_PORT);
        parseBoolean(parsedProperties,propertiesToConvert,PROPNAME_USE_SHARED_SEARCH_REPOSITORY);
        parseBoolean(parsedProperties,propertiesToConvert, PROPNAME_RELATED_ITEM_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED);


        parseInt(parsedProperties, propertiesToConvert, PROPNAME_ELASTIC_SEARCH_HTTP_REQUEST_TIMEOUT_MS);
        parseInt(parsedProperties, propertiesToConvert, PROPNAME_ELASTIC_SEARCH_HTTP_CONNECT_TIMEOUT_MS);
        parseInt(parsedProperties, propertiesToConvert, PROPNAME_ELASTIC_SEARCH_HTTP_NO_OF_RETRIES);
        parseBoolean(parsedProperties, propertiesToConvert, PROPNAME_ELASTIC_SEARCH_HTTP_FOLLOW_REDIRECTS);
        parseBoolean(parsedProperties, propertiesToConvert, PROPNAME_ELASTIC_SEARCH_HTTP_CONNECTION_POOL_ENABLED);
        parseBoolean(parsedProperties, propertiesToConvert, PROPNAME_ELASTIC_SEARCH_HTTP_COMPRESSION_ENABLED);

        parseInt(parsedProperties, propertiesToConvert, PROPNAME_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_REQUEST_TIMEOUT_MS);
        parseInt(parsedProperties, propertiesToConvert, PROPNAME_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_CONNECT_TIMEOUT_MS);
        parseInt(parsedProperties, propertiesToConvert, PROPNAME_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_NO_OF_RETRIES);
        parseBoolean(parsedProperties, propertiesToConvert, PROPNAME_ELASTIC_SEARCH_HTTP_MULTISEARCH_ENDPOINT);
        parseBoolean(parsedProperties, propertiesToConvert, PROPNAME_ELASTIC_SEARCH_HTTP_MULTIGET_ENDPOINT);



        parseString(parsedProperties, propertiesToConvert,PROPNAME_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENDPOINT);
        parseInt(parsedProperties, propertiesToConvert, PROPNAME_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_RETRY_INTERVAL);
        parseString(parsedProperties, propertiesToConvert,PROPNAME_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_RETRY_INTERVAL_UNIT);
        parseBoolean(parsedProperties, propertiesToConvert,PROPNAME_ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENABLED);

        parseInt(parsedProperties,propertiesToConvert,PROPNAME_HTTP_ASYNC_INDEXING_REQUEST_TIMEOUT_MS);
        parseInt(parsedProperties,propertiesToConvert,PROPNAME_HTTP_ASYNC_SEARCHING_REQUEST_TIMEOUT_MS);

        parseInt(parsedProperties,propertiesToConvert,PROPNAME_DOCUMENT_INDEX_NAME);
        parseInt(parsedProperties,propertiesToConvert,PROPNAME_DOCUMENT_TYPE_NAME);
        parseInt(parsedProperties,propertiesToConvert,PROPNAME_DOCUMENT_MERGING_SCRIPT_NAME);
        parseBoolean(parsedProperties,propertiesToConvert,PROPNAME_DOCUMENT_INDEXING_ENABLED);
        parseInt(parsedProperties,propertiesToConvert,PROPNAME_DOCUMENT_COMPARISON_KEY_NAME);

        parseBoolean(parsedProperties,propertiesToConvert,PROPNAME_DOCUMENT_COMPARISON_KEY_NAME);

        return parsedProperties;
    }

    protected void setResponseCodes(int notfound,int failedSearch, int timedOut,int missingHandler, int found) {
        searchRequestResponseCodes[SearchResultsOutcome.EMPTY_RESULTS.getIndex()] = notfound;
        searchRequestResponseCodes[SearchResultsOutcome.FAILED_REQUEST.getIndex()] = failedSearch;
        searchRequestResponseCodes[SearchResultsOutcome.REQUEST_TIMEOUT.getIndex()] = timedOut;
        searchRequestResponseCodes[SearchResultsOutcome.HAS_RESULTS.getIndex()] = found;
        searchRequestResponseCodes[SearchResultsOutcome.MISSING_SEARCH_RESULTS_HANDLER.getIndex()] = missingHandler;

    }


    private static void parseBoolean(Map<String,Object> parsedValues, Map<String,String> sysValues, String property) {
        String value = sysValues.get(property);
        Boolean b =null;
        if(value!=null && value.trim().length()!=0) {
            b = Boolean.parseBoolean(value);
        }

        if(b!=null) parsedValues.put(property,b);
    }

    private static void parseString(Map<String,Object> parsedValues, Map<String,String> sysValues, String property) {
        String value = sysValues.get(property);
        String s =null;
        if(value!=null) {
            s = value;
        }

        if(s!=null) parsedValues.put(property,s);
    }

    private static void parseInt(Map<String,Object> parsedValues, Map<String,String> sysValues, String property) {
        String value = sysValues.get(property);
        Integer i =null;
        if(value!=null && value.trim().length()!=0) {
            try {
                i= Integer.valueOf(value);
            } catch(NumberFormatException e) {

            }
        }

        if(i!=null) parsedValues.put(property,i);
    }

    private static void parseLong(Map<String,Object> parsedValues, Map<String,String> sysValues, String property) {
        String value = sysValues.get(property);
        Long l =null;
        if(value!=null && value.trim().length()!=0) {
            try {
                l = Long.valueOf(value);
            } catch(NumberFormatException e) {

            }
        }

        if(l!=null) parsedValues.put(property,l);
    }



    protected DefaultWaitStrategyFactory parseWaitStrategy(String type) {
        if(type.contains("yield")) {
            return new DefaultWaitStrategyFactory(DefaultWaitStrategyFactory.WAIT_STRATEGY_TYPE.YIELDING);
        }
        else if(type.contains("block")) {
            return new DefaultWaitStrategyFactory(DefaultWaitStrategyFactory.WAIT_STRATEGY_TYPE.BLOCKING);
        }
        else if(type.contains("sleep")) {
            return new DefaultWaitStrategyFactory(DefaultWaitStrategyFactory.WAIT_STRATEGY_TYPE.SLEEPING);
        }
        else if (type.contains("busy")) {
            return new DefaultWaitStrategyFactory(DefaultWaitStrategyFactory.WAIT_STRATEGY_TYPE.BUSY);
        }
        else {
            return new DefaultWaitStrategyFactory(DefaultWaitStrategyFactory.WAIT_STRATEGY_TYPE.YIELDING);
        }
    }

    protected ElasticeSearchClientType parseEsClientType(String type) {
        if(type.equals("transport")) {
            return ElasticeSearchClientType.TRANSPORT;
        } else if(type.equals("node")) {
            return ElasticeSearchClientType.NODE;
        } else if(type.equals("http")) {
            return ElasticeSearchClientType.HTTP;
        } else {
            return ElasticeSearchClientType.TRANSPORT;
        }
    }

    protected TimeUnit parseIntervalUnit(String unit) {
        if(unit.equalsIgnoreCase("seconds") || unit.equalsIgnoreCase("s") ||
                unit.equalsIgnoreCase("sec") || unit.equalsIgnoreCase("secs")) {
            return TimeUnit.SECONDS;
        } else if(unit.equalsIgnoreCase("minutes") || unit.equalsIgnoreCase("m") ||
                unit.equalsIgnoreCase("min") || unit.equalsIgnoreCase("mins")) {
            return TimeUnit.MINUTES;
        } else if(unit.equalsIgnoreCase("milliseconds") || unit.equalsIgnoreCase("ms") ||
                unit.equalsIgnoreCase("milli") || unit.equalsIgnoreCase("millis")) {
            return TimeUnit.MILLISECONDS;
        } else if(unit.equalsIgnoreCase("hours") || unit.equalsIgnoreCase("h")
                || unit.equalsIgnoreCase("hour")) {
            return TimeUnit.HOURS;
        } else if(unit.equalsIgnoreCase("days") || unit.equalsIgnoreCase("d")
                || unit.equalsIgnoreCase("day")) {
            return TimeUnit.DAYS;
        } else {
            return TimeUnit.MINUTES;
        }
    }



    public WaitStrategyFactory getWaitStrategyFactory() {
        return waitStrategyFactory;
    }

    public int getMaxNumberOfSearchCriteriaForRelatedContent() {
        return MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT;
    }

    public int getSizeOfRelatedItemSearchRequestHandlerQueue() {
        return SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE;
    }

    public int getSizeOfRelatedItemSearchRequestQueue() {
        return SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE;
    }

    @Override
    public int getSizeOfRelatedItemSearchRequestAndResponseQueue() {
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
    public String getKeyForFrequencyResultSource() {
        return KEY_FOR_FREQUENCY_RESULT_SOURCE;
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
    public String getStorageFrequentlyRelatedItemsFacetResultsFacetName() {
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
    public long getFrequentlyRelatedItemsSearchTimeoutInMillis() {
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
    public int getMaxNumberOfRelatedItemProperties() {
        return MAX_NUMBER_OF_RELATED_PRODUCT_PROPERTIES;
    }

    @Override
    public int getMaxNumberOfRelatedItemsPerItem() {
        return MAX_NUMBER_OF_RELATED_PRODUCTS_PER_PURCHASE;
    }

    @Override
    public int getRelatedItemIdLength() {
        return RELATED_PRODUCT_ID_LENGTH;
    }

    @Override
    public int getMaxRelatedItemPostDataSizeInBytes() {
        return MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES;
    }

    @Override
    public int getMinRelatedItemPostDataSizeInBytes() {
        return MIN_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES;
    }

    @Override
    public int getRelatedItemAdditionalPropertyKeyLength() {
        return RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH;
    }

    @Override
    public int getRelatedItemAdditionalPropertyValueLength() {
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
    public String getElasticSearchHttpHosts() {
        return ELASTIC_SEARCH_HTTP_HOSTS;
    }

    @Override
    public int getElasticSearchHttpPort() {
        return ELASTIC_SEARCH_HTTP_DEFAULT_PORT;
    }

    @Override
    public String getKeyForStorageResponseTime() {
        return KEY_FOR_STORAGE_RESPONSE_TIME;
    }

    @Override
    public String getKeyForStorageGetResponseTime() {
        return KEY_FOR_STORAGE_GET_RESPONSE_TIME;
    }

    @Override
    public String getKeyForSearchProcessingResponseTime() {
        return KEY_FOR_SEARCH_PROCESSING_TIME;
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

    public int getElasticSearchHttpRequestTimeoutMs() {
        return ELASTIC_SEARCH_HTTP_REQUEST_TIMEOUT_MS;
    }

    public int getElasticSearchHttpConnectionTimeoutMs() {
        return ELASTIC_SEARCH_HTTP_CONNECT_TIMEOUT_MS;
    }

    public int getElasticSearchHttpNoOfRetries() {
        return ELASTIC_SEARCH_HTTP_NO_OF_RETRIES;
    }

    public boolean getElasticSearchHttpFollowRedirects() {
        return ELASTIC_SEARCH_HTTP_FOLLOW_REDIRECTS;
    }

    public boolean getElasticSearchHttpConnectionPoolingEnabled() {
        return ELASTIC_SEARCH_HTTP_CONNECTION_POOL_ENABLED;
    }

    public boolean getElasticSearchHttpCompressionEnabled() {
        return ELASTIC_SEARCH_HTTP_COMPRESSION_ENABLED;
    }

    public int getElasticSearchNodeSniffingHttpRequestTimeoutMs() {
        return ELASTIC_SEARCH_HTTP_NODE_SNIFFING_REQUEST_TIMEOUT_MS;
    }

    public int getElasticSearchNodeSniffingHttpConnectionTimeoutMs() {
        return ELASTIC_SEARCH_HTTP_NODE_SNIFFING_CONNECT_TIMEOUT_MS;
    }

    public int getElasticSearchNodeSniffingHttpNoOfRetries() {
        return ELASTIC_SEARCH_HTTP_NODE_SNIFFING_NO_OF_RETRIES;
    }

    @Override
    public String getElasticSearchNodesSniffingHttpAdminEndpoint() {
        return ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENDPOINT;
    }

    @Override
    public int getElasticSearchNodesSniffingHttpRetryInterval() {
        return ELASTIC_SEARCH_HTTP_NODE_SNIFFING_RETRY_INTERVAL;
    }

    @Override
    public TimeUnit getElasticSearchNodesSniffingRetryIntervalUnit() {
        return ELASTIC_SEARCH_HTTP_NODE_SNIFFING_RETRY_INTERVAL_UNIT;
    }

    @Override
    public boolean getElasticSearchNodesSniffingEnabled() {
        return ELASTIC_SEARCH_HTTP_NODE_SNIFFING_ENABLED;
    }

    @Override
    public int getHttpAsyncIndexingRequestTimeout() {
        return HTTP_ASYNC_INDEXING_REQUEST_TIMEOUT_MS;
    }

    @Override
    public int getHttpAsyncSearchingRequestTimeout() {
        return HTTP_ASYNC_SEARCHING_REQUEST_TIMEOUT_MS;
    }

    @Override
    public String getRelatedItemsDocumentIndexName() {
        return DOCUMENT_INDEX_NAME;
    }

    @Override
    public String getRelatedItemsDocumentTypeName() {
        return DOCUMENT_TYPE_NAME;
    }

    @Override
    public String getRelatedItemsDocumentMergingScriptName() {
        return DOCUMENT_MERGING_SCRIPT_NAME;
    }

    @Override
    public boolean getRelatedItemsDocumentIndexingEnabled() {
        return DOCUMENT_INDEXING_ENABLED;
    }

    @Override
    public String getRelatedItemsDocumentComparisonKeyName() {
        return DOCUMENT_COMPARISON_KEY_NAME;
    }

    @Override
    public boolean getRemoveRelatedItemsDocumentDateAttribute() {
        return DOCUMENT_REMOVE_DATE_ATTRIBUTE;
    }


    public String getElasticSearchMultiSearchEndpoint() { return ELASTIC_SEARCH_HTTP_MULTISEARCH_ENDPOINT; }

    public String getElasticSearchMultiGetEndpoint() { return ELASTIC_SEARCH_HTTP_MULTIGET_ENDPOINT; }


}
