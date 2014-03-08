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

import org.greencheek.related.api.searching.SearchResultsOutcome;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 02/06/2013
 * Time: 09:59
 * To change this template use File | Settings | File Templates.
 */
public interface Configuration {
    public static final String APPLICATION_CONTEXT_ATTRIBUTE_NAME = ConfigurationConstants.APPLICATION_CONTEXT_ATTRIBUTE_NAME;

    public WaitStrategyFactory getWaitStrategyFactory();
    public int getNumberOfIndexingRequestProcessors();

    public int getMaxNumberOfRelatedItemProperties();
    public int getMaxNumberOfRelatedItemsPerItem();
    public int getRelatedItemIdLength();

    public int getMinRelatedItemPostDataSizeInBytes();
    public int getMaxRelatedItemPostDataSizeInBytes();
    public int getRelatedItemAdditionalPropertyKeyLength();
    public int getRelatedItemAdditionalPropertyValueLength();

    /**
     * Can be used as a mechanism of whether or not a
     * @return
     */
    public boolean isSearchResponseDebugOutputEnabled();

    public int getIndexBatchSize();

    /**
     * The size of the ring buffer that batches the storage of index request to the
     * backend storage respository.
     *
     * client.
     * @return
     */
    public int getSizeOfBatchIndexingRequestQueue();

    /**
     * The size of the ring buffer that accepts incoming messages from the
     * client.
     * @return
     */
    public int getSizeOfIncomingMessageQueue();

    public int getMaxNumberOfSearchCriteriaForRelatedContent();
    public int getSizeOfRelatedItemSearchRequestHandlerQueue();
    public int getSizeOfRelatedItemSearchRequestQueue();
    public int getSizeOfRelatedItemSearchRequestAndResponseQueue();
    public int getSizeOfResponseProcessingQueue();
    public int getNumberOfSearchingRequestProcessors();

    public int getNumberOfExpectedLikeForLikeRequests();

    public String getKeyForIndexRequestRelatedWithAttr();

    public String getKeyForFrequencyResults();
    public String getKeyForFrequencyResultOccurrence();
    public String getKeyForFrequencyResultId();
    public String getKeyForFrequencyResultOverallResultsSize();
    public String getKeyForFrequencyResultSource();

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
    public String getStorageFrequentlyRelatedItemsFacetResultsFacetName();

    public String getElasticSearchClientDefaultNodeSettingFileName();
    public String getElasticSearchClientDefaultTransportSettingFileName();
    public String getElasticSearchClientOverrideSettingFileName();


    public String getStorageLocationMapper();

    public long getFrequentlyRelatedItemsSearchTimeoutInMillis();


    public int getResponseCode(SearchResultsOutcome type);

    public int getTimedOutSearchRequestStatusCode();
    public int getFailedSearchRequestStatusCode();
    public int getNoFoundSearchResultsStatusCode();
    public int getFoundSearchResultsStatusCode();
    public int getMissingSearchResultsHandlerStatusCode();


    public String getPropertyEncoding();

    boolean isIndexNameDateCachingEnabled();
    public int getNumberOfIndexNamesToCache();

    public boolean getShouldReplaceOldContentIfExists();

    public boolean getShouldUseSeparateIndexStorageThread();

    boolean shouldDiscardIndexRequestWithTooManyRelations();

    ElasticeSearchClientType getElasticSearchClientType();

    public String getElasticSearchTransportHosts();

    int getDefaultElasticSearchPort();

    public String getElasticSearchHttpHosts();

    public int getElasticSearchHttpPort();

    String getKeyForStorageResponseTime();

    String getKeyForSearchProcessingResponseTime();

    String getKeyForStorageGetResponseTime();

    public enum ElasticeSearchClientType {
        NODE,
        TRANSPORT,
        HTTP;
    }

    public boolean useSharedSearchRepository();

    /**
     * Is it safe to output the indexing request data sent by the client
     * @return
     */
    public boolean isSafeToOutputRequestData();

    /**
     * provides a repository specific execution hint for
     * when faceting is performed.
     */
    public String getStorageFacetExecutionHint();

    public String getElasticSearchMultiSearchEndpoint();
    public int getElasticSearchHttpRequestTimeoutMs();
    public int getElasticSearchHttpConnectionTimeoutMs();
    public int getElasticSearchHttpNoOfRetries();
    public boolean getElasticSearchHttpFollowRedirects();
    public boolean getElasticSearchHttpConnectionPoolingEnabled();
    public boolean getElasticSearchHttpCompressionEnabled();

    public int getElasticSearchNodeSniffingHttpRequestTimeoutMs();
    public int getElasticSearchNodeSniffingHttpConnectionTimeoutMs();
    public int getElasticSearchNodeSniffingHttpNoOfRetries();
    public String getElasticSearchNodesSniffingHttpAdminEndpoint();
    public int getElasticSearchNodesSniffingHttpRetryInterval();
    public TimeUnit getElasticSearchNodesSniffingRetryIntervalUnit();
    public boolean getElasticSearchNodesSniffingEnabled();

    public String getElasticSearchMultiGetEndpoint();

    public int getHttpAsyncIndexingRequestTimeout();
    public int getHttpAsyncSearchingRequestTimeout();

    public String getRelatedItemsDocumentIndexName();
    public String getRelatedItemsDocumentTypeName();
    public String getRelatedItemsDocumentMergingScriptName();
    public boolean getRelatedItemsDocumentIndexingEnabled();
    public String getRelatedItemsDocumentComparisonKeyName();

    public boolean getRemoveRelatedItemsDocumentDateAttribute();

}
