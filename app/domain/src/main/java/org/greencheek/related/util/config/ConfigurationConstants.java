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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dominictootell on 19/01/2014.
 */
public class ConfigurationConstants {
    public static final String APPLICATION_CONTEXT_ATTRIBUTE_NAME = "ApplicationContext";

    public static final String PROPNAME_SEARCHING_LOG_FILE = "related-item.searching.log.file";
    public static final String PROPNAME_INDEXING_LOG_FILE = "related-item.indexing.log.file";
    public static final String PROPNAME_INDEXING_LOG_LEVEL = "related-item.indexing.log.level";
    public static final String PROPNAME_SEARCHING_LOG_LEVEL = "related-item.searching.log.level";

    public static final String PROPNAME_SAFE_TO_OUTPUT_REQUEST_DATA = "related-item.safe.to.output.index.request.data";
    public static final String PROPNAME_MAX_NO_OF_RELATED_ITEM_PROPERTES = "related-item.max.number.related.item.properties";
    public static final String PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_INDEX_REQUEST = "related-item.max.number.related.items.per.index.request";
    public static final String PROPNAME_RELATED_ITEM_ID_LENGTH = "related-item.related.item.id.length";
    public static final String PROPNAME_MAX_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES = "related-item.max.related.item.post.data.size.in.bytes";
    public static final String PROPNAME_MIN_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES = "related-item.min.related.item.post.data.size.in.bytes";
    public static final String PROPNAME_RELATED_ITEM_ADDITIONAL_PROPERTY_KEY_LENGTH = "related-item.additional.prop.key.length";
    public static final String PROPNAME_RELATED_ITEM_ADDITIONAL_PROPERTY_VALUE_LENGTH = "related-item.additional.prop.value.length";
    public static final String PROPNAME_SIZE_OF_INCOMING_REQUEST_QUEUE = "related-item.indexing.size.of.incoming.request.queue";
    public static final String PROPNAME_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE  = "related-item.indexing.size.of.batch.indexing.request.queue";
    public static final String PROPNAME_BATCH_INDEX_SIZE = "related-item.indexing.batch.size";
    public static final String PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE = "related-item.searching.size.of.related.content.search.request.queue";
    public static final String PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE = "related-item.searching.size.of.related.content.search.request.handler.queue";
    public static final String PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE = "related-item.searching.size.of.related.content.search.request.and.response.queue";
    public static final String PROPNAME_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT = "related-item.searching.max.number.of.search.criteria.for.related.content";
    public static final String PROPNAME_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS = "related-item.searching.number.of.expected.like.for.like.requests";
    public static final String PROPNAME_KEY_FOR_FREQUENCY_RESULT_ID = "related-item.searching.key.for.frequency.result.id";
    public static final String PROPNAME_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE = "related-item.searching.key.for.frequency.result.occurrence";

    public static final String PROPNAME_KEY_FOR_STORAGE_RESPONSE_TIME = "related-item.searching.key.for.storage.response.time";
    public static final String PROPNAME_KEY_FOR_SEARCH_PROCESSING_TIME = "related-item.searching.key.for.search.processing.time";
    public static final String PROPNAME_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_ITEMS = "related-item.searching.key.for.frequency.result.overall.no.of.related.items";
    public static final String PROPNAME_KEY_FOR_FREQUENCY_RESULTS ="related-item.searching.key.for.frequency.results";
    public static final String PROPNAME_REQUEST_PARAMETER_FOR_SIZE = "related-item.searching.request.parameter.for.size";
    public static final String PROPNAME_REQUEST_PARAMETER_FOR_ID =  "related-item.searching.request.parameter.for.id";
    public static final String PROPNAME_DEFAULT_NUMBER_OF_RESULTS= "related-item.searching.default.number.of.results";
    public static final String PROPNAME_SIZE_OF_RESPONSE_PROCESSING_QUEUE = "related-item.searching.size.of.response.processing.queue";
    public static final String PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS = "related-item.indexing.number.of.indexing.request.processors";
    public static final String PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS = "related-item.searching.number.of.searching.request.processors";
    public static final String PROPNAME_STORAGE_INDEX_NAME_PREFIX = "related-item.storage.index.name.prefix";
    public static final String PROPNAME_STORAGE_INDEX_NAME_ALIAS = "related-item.storage.index.name.alias";
    public static final String PROPNAME_STORAGE_CONTENT_TYPE_NAME = "related-item.storage.content.type.name";
    public static final String PROPNAME_STORAGE_CLUSTER_NAME = "related-item.storage.cluster.name";
    public static final String PROPNAME_STORAGE_FREQUENTLY_RELATED_ITEMS_FACET_RESULTS_FACET_NAME = "related-item.storage.frequently.related.items.facet.results.facet.name";
    public static final String PROPNAME_STORAGE_FACET_SEARCH_EXECUTION_HINT  = "related-item.storage.searching.facet.search.execution.hint";
    public static final String PROPNAME_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR =  "related-item.indexing.key.for.index.request.related.with.attr";
    public static final String PROPNAME_KEY_FOR_INDEX_REQUEST_DATE_ATTR = "related-item.indexing.key.for.index.request.date.attr";
    public static final String PROPNAME_KEY_FOR_INDEX_REQUEST_ID_ATTR = "related-item.indexing.key.for.index.request.id.attr";
    public static final String PROPNAME_KEY_FOR_INDEX_REQUEST_ITEM_ARRAY_ATTR = "related-item.indexing.key.for.index.request.item.array.attr";
    public static final String PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME = "related-item.elastic.search.client.default.transport.settings.file.name";
    public static final String PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME =  "related-item.elastic.search.client.default.node.settings.file.name";
    public static final String PROPNAME_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME  = "related-item.elastic.search.client.override.settings.file.name";
    public static final String PROPNAME_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS = "related-item.searching.frequently.related.search.timeout.in.millis";
    public static final String PROPNAME_RELATED_ITEM_STORAGE_LOCATION_MAPPER =  "related-item.storage.location.mapper";
    public static final String PROPNAME_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE = "related-item.searching.timed.out.search.request.status.code";
    public static final String PROPNAME_FAILED_SEARCH_REQUEST_STATUS_CODE =  "related-item.searching.failed.search.request.status.code";
    public static final String PROPNAME_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE = "related-item.searching.not.found.search.request.status.code";
    public static final String PROPNAME_FOUND_SEARCH_REQUEST_STATUS_CODE = "related-item.searching.found.search.results.handler.status.code";
    public static final String PROPNAME_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE = "related-item.searching.missing.search.results.handler.status.code";
    public static final String PROPNAME_PROPERTY_ENCODING = "related-item.additional.prop.string.encoding";
    public static final String PROPNAME_WAIT_STRATEGY = "related-item.wait.strategy";
    public static final String PROPNAME_ES_CLIENT_TYPE  = "related-item.es.client.type";
    public static final String PROPNAME_INDEXNAME_DATE_CACHING_ENABLED = "related-item.indexing.indexname.date.caching.enabled";
    public static final String PROPNAME_NUMBER_OF_INDEXNAMES_TO_CACHE = "related-item.indexing.number.of.indexname.to.cache";
    public static final String PROPNAME_REPLACE_OLD_INDEXED_CONTENT = "related-item.indexing.replace.old.indexed.content";
    public static final String PROPNAME_SEPARATE_INDEXING_THREAD = "related-item.use.separate.repository.storage.thread";
    public static final String PROPNAME_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_ITEMS ="related-item.indexing.discard.storage.requests.with.too.many.relations";
    public static final String PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS = "related-item.elastic.search.transport.hosts";
    public static final String PROPNAME_DEFAULT_ELASTIC_SEARCH_PORT = "related-item.elastic.search.default.port";
    public static final String PROPNAME_USE_SHARED_SEARCH_REPOSITORY = "related-item.searching.use.shared.search.repository";
    public static final String PROPNAME_RELATED_ITEM_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED = "related-item.searching.response.debug.output.enabled";

    public static final boolean DEFAULT_SAFE_TO_OUTPUT_REQUEST_DATA = false;
    public static final int DEFAULT_MAX_NO_OF_RELATED_ITEM_PROPERTES = 10;
    public static final int DEFAULT_MAX_NO_OF_RELATED_ITEMS_PER_INDEX_REQUEST = 10;
    public static final int DEFAULT_RELATED_ITEM_ID_LENGTH = 36;
    public static final int DEFAULT_MAX_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES = 10240;
    public static final int DEFAULT_MIN_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES = 4096;
    public static final int DEFAULT_RELATED_ITEM_ADDITIONAL_PROPERTY_KEY_LENGTH = 30;
    public static final int DEFAULT_RELATED_ITEM_ADDITIONAL_PROPERTY_VALUE_LENGTH = 30;
    public static final int DEFAULT_SIZE_OF_INCOMING_REQUEST_QUEUE = 16384;
    public static final int DEFAULT_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE = -1;
    public static final int DEFAULT_BATCH_INDEX_SIZE = 625;
    public static final int DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE = 16384;
    public static final int DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE = -1;
    public static final int DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE = -1;
    public static final int DEFAULT_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT = 10;
    public static final int DEFAULT_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS = 10;
    public static final String DEFAULT_KEY_FOR_FREQUENCY_RESULT_ID = "id";
    public static final String DEFAULT_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE = "frequency";
    public static final String DEFAULT_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_ITEMS = "size";
    public static final String DEFAULT_KEY_FOR_FREQUENCY_RESULTS = "results";
    public static final String DEFAULT_REQUEST_PARAMETER_FOR_SIZE = "maxresults";
    public static final String DEFAULT_REQUEST_PARAMETER_FOR_ID = "id";
    public static final int DEFAULT_DEFAULT_NUMBER_OF_RESULTS = 4;
    public static final int DEFAULT_SIZE_OF_RESPONSE_PROCESSING_QUEUE = -1;
    public static final int DEFAULT_NUMBER_OF_INDEXING_REQUEST_PROCESSORS = 2;
    public static final int DEFAULT_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS = 2;
    public static final String DEFAULT_STORAGE_INDEX_NAME_PREFIX = "relateditems";
    public static final String DEFAULT_STORAGE_INDEX_NAME_ALIAS = "";
    public static final String DEFAULT_STORAGE_CONTENT_TYPE_NAME = "related";
    public static final String DEFAULT_STORAGE_CLUSTER_NAME = "relateditems";
    public static final String DEFAULT_STORAGE_FREQUENTLY_RELATED_ITEMS_FACET_RESULTS_FACET_NAME = "frequently-related-with";
    public static final String DEFAULT_STORAGE_FACET_SEARCH_EXECUTION_HINT = "map";
    public static final String DEFAULT_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR ="related-with";
    public static final String DEFAULT_KEY_FOR_INDEX_REQUEST_DATE_ATTR = "date";
    public static final String DEFAULT_KEY_FOR_INDEX_REQUEST_ID_ATTR = "id";
    public static final String DEFAULT_KEY_FOR_INDEX_REQUEST_ITEM_ARRAY_ATTR ="items";

    public static final String DEFAULT_KEY_FOR_STORAGE_RESPONSE_TIME = "storage_response_time";
    public static final String DEFAULT_KEY_FOR_SEARCH_PROCESSING_TIME = "response_time";

    public static final String DEFAULT_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME = "default-transport-elasticsearch.yml";
    public static final String DEFAULT_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME = "default-node-elasticsearch.yml";
    public static final String DEFAULT_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME = "elasticsearch.yml";
    public static final long DEFAULT_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS = 5000;
    public static final String DEFAULT_RELATED_ITEM_STORAGE_LOCATION_MAPPER = "day";
    public static final int DEFAULT_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE = 504;
    public static final int DEFAULT_FAILED_SEARCH_REQUEST_STATUS_CODE = 502;
    public static final int DEFAULT_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE = 404;
    public static final int DEFAULT_FOUND_SEARCH_REQUEST_STATUS_CODE = 200;
    public static final int DEFAULT_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE = 500;
    public static final String DEFAULT_PROPERTY_ENCODING = "UTF-8";
    public static final String DEFAULT_WAIT_STRATEGY = "yield";
    public static final String DEFAULT_ES_CLIENT_TYPE = "transport";
    public static final boolean DEFAULT_INDEXNAME_DATE_CACHING_ENABLED = true;
    public static final int DEFAULT_NUMBER_OF_INDEXNAMES_TO_CACHE = 365;
    public static final boolean DEFAULT_REPLACE_OLD_INDEXED_CONTENT = false;
    public static final boolean DEFAULT_SEPARATE_INDEXING_THREAD = false;
    public static final boolean DEFAULT_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_ITEMS = false;
    public static final String DEFAULT_ELASTIC_SEARCH_TRANSPORT_HOSTS = "127.0.0.1:9300";
    public static final int DEFAULT_DEFAULT_ELASTIC_SEARCH_PORT = 9300;
    public static final boolean DEFAULT_USE_SHARED_SEARCH_REPOSITORY = false;
    public static final boolean DEFAULT_RELATED_ITEM_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED = false;

//    public static final String[] PROPERTY_NAMES = new String[]{PROPNAME_SAFE_TO_OUTPUT_REQUEST_DATA, PROPNAME_MAX_NO_OF_RELATED_ITEM_PROPERTES,
//            PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_INDEX_REQUEST, PROPNAME_RELATED_ITEM_ID_LENGTH, PROPNAME_RELATED_ITEM_INVALID_ID_STRING,
//            PROPNAME_MAX_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES, PROPNAME_MIN_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES,
//            PROPNAME_RELATED_ITEM_ADDITIONAL_PROPERTY_KEY_LENGTH, PROPNAME_RELATED_ITEM_ADDITIONAL_PROPERTY_VALUE_LENGTH,
//            PROPNAME_SIZE_OF_INCOMING_REQUEST_QUEUE, PROPNAME_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE, PROPNAME_BATCH_INDEX_SIZE,
//            PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE, PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE,
//            PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE, PROPNAME_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT,
//            PROPNAME_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS, PROPNAME_KEY_FOR_FREQUENCY_RESULT_ID, PROPNAME_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE,
//            PROPNAME_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_ITEMS, PROPNAME_KEY_FOR_FREQUENCY_RESULTS, PROPNAME_REQUEST_PARAMETER_FOR_SIZE,
//            PROPNAME_REQUEST_PARAMETER_FOR_ID, PROPNAME_DEFAULT_NUMBER_OF_RESULTS =, PROPNAME_SIZE_OF_RESPONSE_PROCESSING_QUEUE,
//            PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS, PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS, PROPNAME_STORAGE_INDEX_NAME_PREFIX,
//            PROPNAME_STORAGE_INDEX_NAME_ALIAS, PROPNAME_STORAGE_CONTENT_TYPE_NAME, PROPNAME_STORAGE_CLUSTER_NAME,
//            PROPNAME_STORAGE_FREQUENTLY_RELATED_ITEMS_FACET_RESULTS_FACET_NAME =, PROPNAME_STORAGE_FACET_SEARCH_EXECUTION_HINT,
//            PROPNAME_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR, PROPNAME_KEY_FOR_INDEX_REQUEST_DATE_ATTR, PROPNAME_KEY_FOR_INDEX_REQUEST_ID_ATTR,
//            PROPNAME_KEY_FOR_INDEX_REQUEST_ITEM_ARRAY_ATTR, PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME,
//            PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME, PROPNAME_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME,
//            PROPNAME_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS, PROPNAME_RELATED_ITEM_STORAGE_LOCATION_MAPPER,
//            PROPNAME_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE, PROPNAME_FAILED_SEARCH_REQUEST_STATUS_CODE, PROPNAME_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE,
//            PROPNAME_FOUND_SEARCH_REQUEST_STATUS_CODE, PROPNAME_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE, PROPNAME_PROPERTY_ENCODING, PROPNAME_WAIT_STRATEGY,
//            PROPNAME_ES_CLIENT_TYPE, PROPNAME_INDEXNAME_DATE_CACHING_ENABLED, PROPNAME_NUMBER_OF_INDEXNAMES_TO_CACHE, PROPNAME_REPLACE_OLD_INDEXED_CONTENT,
//            PROPNAME_SEPARATE_INDEXING_THREAD, PROPNAME_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_ITEMS, PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS,
//            PROPNAME_DEFAULT_ELASTIC_SEARCH_PORT, PROPNAME_USE_SHARED_SEARCH_REPOSITORY, PROPNAME_RELATED_ITEM_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED};

    public static final Map<String,Object> DEFAULT_SETTINGS;
    static {
        Map<String,Object> configuration = new HashMap<String,Object>(100);
        configuration.put(PROPNAME_WAIT_STRATEGY,DEFAULT_WAIT_STRATEGY);
        configuration.put(PROPNAME_SAFE_TO_OUTPUT_REQUEST_DATA,DEFAULT_SAFE_TO_OUTPUT_REQUEST_DATA);
        configuration.put(PROPNAME_MAX_NO_OF_RELATED_ITEM_PROPERTES, DEFAULT_MAX_NO_OF_RELATED_ITEM_PROPERTES);
        configuration.put(PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_INDEX_REQUEST, DEFAULT_MAX_NO_OF_RELATED_ITEMS_PER_INDEX_REQUEST);
        configuration.put(PROPNAME_RELATED_ITEM_ID_LENGTH, DEFAULT_RELATED_ITEM_ID_LENGTH);
        configuration.put(PROPNAME_MAX_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES, DEFAULT_MAX_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES);
        configuration.put(PROPNAME_MIN_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES, DEFAULT_MIN_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES);
        configuration.put(PROPNAME_RELATED_ITEM_ADDITIONAL_PROPERTY_KEY_LENGTH, DEFAULT_RELATED_ITEM_ADDITIONAL_PROPERTY_KEY_LENGTH);
        configuration.put(PROPNAME_RELATED_ITEM_ADDITIONAL_PROPERTY_VALUE_LENGTH, DEFAULT_RELATED_ITEM_ADDITIONAL_PROPERTY_VALUE_LENGTH);
        configuration.put(PROPNAME_SIZE_OF_INCOMING_REQUEST_QUEUE,DEFAULT_SIZE_OF_INCOMING_REQUEST_QUEUE);
        configuration.put(PROPNAME_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE,DEFAULT_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE);
        configuration.put(PROPNAME_BATCH_INDEX_SIZE,DEFAULT_BATCH_INDEX_SIZE);
        configuration.put(PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE,DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE);
        configuration.put(PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE,DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE);
        configuration.put(PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE,DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE);
        configuration.put(PROPNAME_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT,DEFAULT_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT);
        configuration.put(PROPNAME_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS,DEFAULT_NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS);
        configuration.put(PROPNAME_KEY_FOR_FREQUENCY_RESULT_ID,DEFAULT_KEY_FOR_FREQUENCY_RESULT_ID);
        configuration.put(PROPNAME_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE,DEFAULT_KEY_FOR_FREQUENCY_RESULT_OCCURRENCE);
        configuration.put(PROPNAME_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_ITEMS, DEFAULT_KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_RELATED_ITEMS);
        configuration.put(PROPNAME_KEY_FOR_FREQUENCY_RESULTS,DEFAULT_KEY_FOR_FREQUENCY_RESULTS);

        configuration.put(PROPNAME_KEY_FOR_SEARCH_PROCESSING_TIME,DEFAULT_KEY_FOR_SEARCH_PROCESSING_TIME);
        configuration.put(PROPNAME_KEY_FOR_STORAGE_RESPONSE_TIME,DEFAULT_KEY_FOR_STORAGE_RESPONSE_TIME);

        configuration.put(PROPNAME_REQUEST_PARAMETER_FOR_SIZE,DEFAULT_REQUEST_PARAMETER_FOR_SIZE);
        configuration.put(PROPNAME_REQUEST_PARAMETER_FOR_ID,DEFAULT_REQUEST_PARAMETER_FOR_ID);
        configuration.put(PROPNAME_DEFAULT_NUMBER_OF_RESULTS,DEFAULT_DEFAULT_NUMBER_OF_RESULTS);
        configuration.put(PROPNAME_SIZE_OF_RESPONSE_PROCESSING_QUEUE,DEFAULT_SIZE_OF_RESPONSE_PROCESSING_QUEUE);
        configuration.put(PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS,DEFAULT_NUMBER_OF_INDEXING_REQUEST_PROCESSORS);
        configuration.put(PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS,DEFAULT_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS);
        configuration.put(PROPNAME_STORAGE_INDEX_NAME_PREFIX,DEFAULT_STORAGE_INDEX_NAME_PREFIX);
        configuration.put(PROPNAME_STORAGE_INDEX_NAME_ALIAS,DEFAULT_STORAGE_INDEX_NAME_ALIAS);
        configuration.put(PROPNAME_STORAGE_CONTENT_TYPE_NAME,DEFAULT_STORAGE_CONTENT_TYPE_NAME);
        configuration.put(PROPNAME_STORAGE_CLUSTER_NAME,DEFAULT_STORAGE_CLUSTER_NAME);
        configuration.put(PROPNAME_STORAGE_FREQUENTLY_RELATED_ITEMS_FACET_RESULTS_FACET_NAME, DEFAULT_STORAGE_FREQUENTLY_RELATED_ITEMS_FACET_RESULTS_FACET_NAME);
        configuration.put(PROPNAME_STORAGE_FACET_SEARCH_EXECUTION_HINT,DEFAULT_STORAGE_FACET_SEARCH_EXECUTION_HINT);
        configuration.put(PROPNAME_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR,DEFAULT_KEY_FOR_INDEX_REQUEST_RELATED_WITH_ATTR);
        configuration.put(PROPNAME_KEY_FOR_INDEX_REQUEST_DATE_ATTR,DEFAULT_KEY_FOR_INDEX_REQUEST_DATE_ATTR);
        configuration.put(PROPNAME_KEY_FOR_INDEX_REQUEST_ID_ATTR,DEFAULT_KEY_FOR_INDEX_REQUEST_ID_ATTR);
        configuration.put(PROPNAME_KEY_FOR_INDEX_REQUEST_ITEM_ARRAY_ATTR, DEFAULT_KEY_FOR_INDEX_REQUEST_ITEM_ARRAY_ATTR);
        configuration.put(PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME,DEFAULT_ELASTIC_SEARCH_CLIENT_DEFAULT_TRANSPORT_SETTINGS_FILE_NAME);
        configuration.put(PROPNAME_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME,DEFAULT_ELASTIC_SEARCH_CLIENT_DEFAULT_NODE_SETTINGS_FILE_NAME);
        configuration.put(PROPNAME_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME,DEFAULT_ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME);
        configuration.put(PROPNAME_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS,DEFAULT_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS);
        configuration.put(PROPNAME_RELATED_ITEM_STORAGE_LOCATION_MAPPER, DEFAULT_RELATED_ITEM_STORAGE_LOCATION_MAPPER);
        configuration.put(PROPNAME_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE,DEFAULT_TIMED_OUT_SEARCH_REQUEST_STATUS_CODE);
        configuration.put(PROPNAME_FAILED_SEARCH_REQUEST_STATUS_CODE,DEFAULT_FAILED_SEARCH_REQUEST_STATUS_CODE);
        configuration.put(PROPNAME_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE,DEFAULT_NOT_FOUND_SEARCH_REQUEST_STATUS_CODE);
        configuration.put(PROPNAME_FOUND_SEARCH_REQUEST_STATUS_CODE,DEFAULT_FOUND_SEARCH_REQUEST_STATUS_CODE);
        configuration.put(PROPNAME_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE,DEFAULT_MISSING_SEARCH_RESULTS_HANDLER_STATUS_CODE);
        configuration.put(PROPNAME_PROPERTY_ENCODING,DEFAULT_PROPERTY_ENCODING);
        configuration.put(PROPNAME_WAIT_STRATEGY,DEFAULT_WAIT_STRATEGY);
        configuration.put(PROPNAME_ES_CLIENT_TYPE,DEFAULT_ES_CLIENT_TYPE);
        configuration.put(PROPNAME_INDEXNAME_DATE_CACHING_ENABLED,DEFAULT_INDEXNAME_DATE_CACHING_ENABLED);
        configuration.put(PROPNAME_NUMBER_OF_INDEXNAMES_TO_CACHE,DEFAULT_NUMBER_OF_INDEXNAMES_TO_CACHE);
        configuration.put(PROPNAME_REPLACE_OLD_INDEXED_CONTENT,DEFAULT_REPLACE_OLD_INDEXED_CONTENT);
        configuration.put(PROPNAME_SEPARATE_INDEXING_THREAD,DEFAULT_SEPARATE_INDEXING_THREAD);
        configuration.put(PROPNAME_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_ITEMS, DEFAULT_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_ITEMS);
        configuration.put(PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS,DEFAULT_ELASTIC_SEARCH_TRANSPORT_HOSTS);
        configuration.put(PROPNAME_DEFAULT_ELASTIC_SEARCH_PORT,DEFAULT_DEFAULT_ELASTIC_SEARCH_PORT);
        configuration.put(PROPNAME_USE_SHARED_SEARCH_REPOSITORY,DEFAULT_USE_SHARED_SEARCH_REPOSITORY);
        configuration.put(PROPNAME_RELATED_ITEM_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED, DEFAULT_RELATED_ITEM_SEARCH_REPONSE_DEBUG_OUTPUT_ENABLED);


        DEFAULT_SETTINGS = configuration;
    }


    public static void setLoggingProperties(String logFilePropertyName,String loglevelPropertyName,
                                            String defaultLogName, String defaultLevel ) {
        String logfile = System.getProperty(logFilePropertyName);
        if(logfile==null || logfile.trim().length()==0) {
            String logfileLocation;
            String tomcat = System.getProperty("CATALINA.BASE",System.getProperty("catalina.base"));
            if(tomcat==null || tomcat.trim().length()==0) {
                logfileLocation = System.getProperty("java.io.tmpdir");
                if (!logfileLocation.endsWith(System.getProperty("file.separator"))) {
                    logfileLocation += System.getProperty("file.separator");
                }
                logfileLocation += defaultLogName;

            } else {
                logfileLocation = tomcat + "/logs/"+defaultLogName;
            }

            System.setProperty(logFilePropertyName,logfileLocation);
        }

        String loglevel = System.getProperty(loglevelPropertyName);
        if(loglevel == null || loglevel.trim().length()==0) {
            System.setProperty(loglevelPropertyName,defaultLevel);
        }
    }



}
