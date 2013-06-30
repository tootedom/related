package org.greencheek.relatedproduct.util.config;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 02/06/2013
 * Time: 09:59
 * To change this template use File | Settings | File Templates.
 */
public interface Configuration {
    public static final String APPLICATION_CONTEXT_ATTRIBUTE_NAME = "ApplicationContext";

    public short getNumberOfIndexingRequestProcessors();

    public short getMaxNumberOfRelatedProductProperties();
    public short getMaxNumberOfRelatedProductsPerPurchase();
    public short getRelatedProductIdLength();
    public String getRelatedProductInvalidIdString();
    public int getMaxRelatedProductPostDataSizeInBytes();
    public short getRelatedProductAdditionalPropertyKeyLength();
    public short getRelatedProductAdditionalPropertyValueLength();
    public int getSizeOfIndexRequestQueue();
    public short getMaxNumberOfSearchCriteriaForRelatedContent();
    public int getSizeOfRelatedContentSearchRequestHandlerQueue();
    public int getSizeOfRelatedContentSearchRequestQueue();
    public int getSizeOfRelatedContentSearchRequestAndResponseQueue();
    public int getSizeOfResponseProcessingQueue();
    public short getNumberOfSearchingRequestProcessors();

    public int getNumberOfExpectedLikeForLikeRequests();

    public String getKeyForIndexRequestRelatedWithAttr();

    public String getKeyForFrequencyResults();
    public String getKeyForFrequencyResultOccurrence();
    public String getKeyForFrequencyResultId();
    public String getKeyForFrequencyResultOverallResultsSize();

    public String getKeyForIndexRequestDateAttr();
    public String getKeyForIndexRequestIdAttr();
    public String getKeyForIndexRequestProductArrayAttr();


    public String getRequestParameterForSize();
    public String getRequestParameterForId();
    public int getDefaultNumberOfResults();

    public String getStorageIndexNamePrefix();
    public String getStorageIndexNameAlias();
    public String getStorageContentTypeName();
    public String getStorageClusterName();
    public String getStorageFrequentlyRelatedProductsFacetResultsFacetName();

    public String getElasticSearchClientDefaultSettingFileName();
    public String getElasticSearchClientOverrideSettingFileName();


    public String getStorageLocationMapper();

    public long getFrequentlyRelatedProductsSearchTimeoutInMillis();


    public int getTimedOutSearchRequestStatusCode();
    public int getFailedSearchRequestStatusCode();
    public int getNoFoundSearchResultsStatusCode();
    public int getFoundSearchResultsStatusCode();


}
