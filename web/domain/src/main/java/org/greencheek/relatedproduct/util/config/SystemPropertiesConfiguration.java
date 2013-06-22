package org.greencheek.relatedproduct.util.config;

import javax.inject.Named;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 14:52
 * To change this template use File | Settings | File Templates.
 */
@Named
public class SystemPropertiesConfiguration implements Configuration {
    private final static short MAX_NUMBER_OF_RELATED_PRODUCT_PROPERTIES = Short.valueOf(System.getProperty("related-product.max.number.related.product.properties", "10"));


    private final static short MAX_NUMBER_OF_RELATED_PRODUCTS_PER_PURCHASE = Short.valueOf(System.getProperty("related-product.max.number.related.products.per.product", "10"));
    private final static short RELATED_PRODUCT_ID_LENGTH = Short.valueOf(System.getProperty("related-product.related.product.id.length", "36"));
    private final static String RELATED_PRODUCT_INVALID_ID_STRING = System.getProperty("related-product.related.product.invalid.id.string", "INVALID_ID");
    private final static int MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES = Integer.valueOf(System.getProperty("related-product.max.related.product.post.data.size.in.bytes","10240"));

    private final static short RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH = Short.valueOf(System.getProperty("related-product.related.product.additional.key.length", "30"));
    private final static short RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH = Short.valueOf(System.getProperty("related-product.related.product.additional.value.length", "30"));
    private final static int SIZE_OF_INDEX_REQUEST_QUEUE = Integer.valueOf(System.getProperty("related-product.size.of.index.request.queue", "2048"));

    private final static int SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE = Integer.valueOf(System.getProperty("related-product.size.of.related.content.search.request.queue", "2048"));
    private final static int SIZE_OF_RELATED_CONTENT_SEARCH_RESULTS_QUEUE = Integer.valueOf(System.getProperty("related-product.size.of.related.content.search.results.queue", "2048"));
    private final static int SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE = Integer.valueOf(System.getProperty("related-product.size.of.related.content.search.request.handler.queue", "2048"));
    private final static int SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE = Integer.valueOf(System.getProperty("related-product.size.of.related.content.search.request.and.response.queue", "2048"));

    private final static short MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT =  Short.valueOf(System.getProperty("related-product.max.number.of.search.criteria.for.related.content", "10"));
    private final static int NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS = Integer.valueOf(System.getProperty("related-product.number.of.expected.like.for.like.requests", "10"));

    private final static String RELATED_WITH_FACET_NAME = System.getProperty("related-product.related.with.facet.name","related-with");
    private final static String KEY_FOR_FREQUENCY_RESULT_NAME = System.getProperty("related-product.key.for.frequency.result.name","id");
    private final static String KEY_FOR_FREQUENCY_RESULT_SIZE = System.getProperty("related-product.key.for.frequency.result.name","frequency");
    private final static String KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_REALTED_PRODUCTS = System.getProperty("related-product.key.for.frequency.result.name","size");
    private final static String KEY_FOR_FREQUENCY_RESULTS = System.getProperty("related-product.key.for.frequency.results","results");

    private final static String REQUEST_PARAMETER_FOR_SIZE = System.getProperty("related-product.request.parameter.for.size","maxresults");
    private final static String REQUEST_PARAMETER_FOR_ID = System.getProperty("related-product.request.parameter.for.id","id");

    private final static int DEFAULT_NUMBER_OF_RESULTS = Integer.valueOf(System.getProperty("related-product.default.number.of.results","4"));

    private final static int SIZE_OF_RESPONSE_PROCESSING_QUEUE = Integer.valueOf(System.getProperty("related-product.size.of.response.processing.queue","2048"));

    private final static short NUMBER_OF_INDEXING_REQUEST_PROCESSORS = Short.valueOf(System.getProperty("related-product.number.of.indexing.request.processors","2"));

    private final static short NUMBER_OF_SEARCHING_REQUEST_PROCESSORS = Short.valueOf(System.getProperty("related-product.number.of.searching.request.processors","2"));

    private final static String STORAGE_INDEX_NAME_PREFIX = System.getProperty("related-product.storage.index.name.prefix","relatedproducts-");
    private final static String STORAGE_CONTENT_TYPE_NAME = System.getProperty("related-product.storage.content.type.name","relatedproduct");
    private final static String STORAGE_CLUSTER_NAME = System.getProperty("related-product.storage.cluster.name","relatedproducts");

    private final static String KEY_FOR_INDEX_REQUEST_DATE_ATTR = System.getProperty("related-product.key.for.index.request.date.attr","date");
    private final static String KEY_FOR_INDEX_REQUEST_ID_ATTR = System.getProperty("related-product.key.for.index.request.id.attr","id");
    private final static String KEY_FOR_INDEX_REQUEST_PRODUCT_ARRAY_ATTR = System.getProperty("related-product.key.for.index.request.product.array.attr","products");

    private final static String ELASTIC_SEARCH_CLIENT_DEFAULT_SETTINGS_FILE_NAME = System.getProperty("related-product.elastic.search.client.default.settings.fila.name","default-elasticsearch.yml");
    private final static String ELASTIC_SEARCH_CLIENT_OVERRIDE_SETTINGS_FILE_NAME = System.getProperty("related-product.elastic.search.client.override.settings.fila.name","elasticsearch.yml");

    // can be "day|hour"
    private final static String RELATED_PRODUCT_STORAGE_LOCATION_MAPPER = System.getProperty("related-product.storage.location.mapper","day");


    public short getMaxNumberOfSearchCriteriaForRelatedContent() {
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
    public short getNumberOfSearchingRequestProcessors() {
        return NUMBER_OF_SEARCHING_REQUEST_PROCESSORS;
    }

    @Override
    public int getNumberOfExpectedLikeForLikeRequests() {
        return NUMBER_OF_EXPECTED_LIKE_FOR_LIKE_REQUESTS;
    }

    @Override
    public String getRelatedWithFacetName() {
        return RELATED_WITH_FACET_NAME;
    }

    @Override
    public String getKeyForFrequencyResultSize() {
        return KEY_FOR_FREQUENCY_RESULT_SIZE;
    }

    @Override
    public String getKeyForFrequencyResultName() {
        return KEY_FOR_FREQUENCY_RESULT_NAME;
    }

    @Override
    public String getKeyForFrequencyResults() {
        return KEY_FOR_FREQUENCY_RESULTS;
    }


    @Override
    public String getKeyForFrequencyResultOverallResultsSize() {
        return KEY_FOR_FREQUENCY_RESULT_OVERALL_NO_OF_REALTED_PRODUCTS;
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
    public String getStorageContentTypeName() {
        return STORAGE_CONTENT_TYPE_NAME;
    }

    @Override
    public String getStorageClusterName() {
        return STORAGE_CLUSTER_NAME;
    }

    @Override
    public String getElasticSearchClientDefaultSettingFileName() {
        return ELASTIC_SEARCH_CLIENT_DEFAULT_SETTINGS_FILE_NAME;
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
    public short getNumberOfIndexingRequestProcessors() {
        return NUMBER_OF_INDEXING_REQUEST_PROCESSORS;
    }

    @Override
    public short getMaxNumberOfRelatedProductProperties() {
        return MAX_NUMBER_OF_RELATED_PRODUCT_PROPERTIES;
    }

    @Override
    public short getMaxNumberOfRelatedProductsPerPurchase() {
        return MAX_NUMBER_OF_RELATED_PRODUCTS_PER_PURCHASE;
    }

    @Override
    public short getRelatedProductIdLength() {
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
    public short getRelatedProductAdditionalPropertyKeyLength() {
        return RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH;
    }

    @Override
    public short getRelatedProductAdditionalPropertyValueLength() {
        return RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH;
    }

    @Override
    public int getSizeOfIndexRequestQueue() {
        return SIZE_OF_INDEX_REQUEST_QUEUE;
    }
}
