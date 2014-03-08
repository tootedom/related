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

package org.greencheek.related.searching.responseprocessing.resultsconverter;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.greencheek.related.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.related.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.related.searching.domain.api.SearchResultsEvent;
import org.greencheek.related.util.config.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts a {@link org.greencheek.related.searching.domain.api.SearchResultEventWithSearchRequestKey} with an array of {@link org.greencheek.related.api.searching.FrequentlyRelatedSearchResult}
 * results to a json like the following:
 *
 * <pre>
 * {
 *     "size": "1",
        "storage_response_time": 1,
        "response_time": 3,
        "results": [
                {
                    "frequency": "1",
                    "id": "4"
                },
                ....
                ..
        ]
   }
 * </pre>
 *
 *
 *
 */
public class StringBasedJsonFrequentlyRelatedSearchResultsConverter implements SearchResultsConverter<FrequentlyRelatedSearchResult[]> {

    private final String sizeKey;
    private final String sourceKey;
    private final String responseTimeKey;
    private final String repoResponseTimeKey;
    private final String resultsKey;
    private final String resultFrequencyKey;
    private final int resultFrequencyKeySize;
    private final String resultIdKey;
    private final int resultIdKeySize;

    private final int estimatedResultsSize;
    private final int resultItemSize;
    private final int idSize;
    private final boolean relateDocumentIndexingEnabled;

    private final String EMPTY_JSON_RESULTS;
    private final String EMPTY_JSON_WITHOUT_RESPONSE_TIMES;


    private static final String JSON_CONTENT_TYPE = "application/json";

    public StringBasedJsonFrequentlyRelatedSearchResultsConverter(Configuration configuration) {
        resultIdKey = configuration.getKeyForFrequencyResultId();
        resultIdKeySize = resultIdKey.length();
        resultFrequencyKey = configuration.getKeyForFrequencyResultOccurrence();
        resultFrequencyKeySize = resultFrequencyKey.length();
        relateDocumentIndexingEnabled = configuration.getRelatedItemsDocumentIndexingEnabled();
        resultsKey = configuration.getKeyForFrequencyResults();
        repoResponseTimeKey = configuration.getKeyForStorageResponseTime();
        responseTimeKey = configuration.getKeyForSearchProcessingResponseTime();
        sizeKey = configuration.getKeyForFrequencyResultOverallResultsSize();
        sourceKey = configuration.getKeyForFrequencyResultSource();

        idSize = configuration.getRelatedItemIdLength();
        estimatedResultsSize = 35 + responseTimeKey.length()+repoResponseTimeKey.length()+resultsKey.length()+sizeKey.length()+sourceKey.length();
        resultItemSize = resultFrequencyKeySize + resultIdKeySize + 13;

        StringBuilder b = new StringBuilder(estimatedResultsSize);
        b.append('{');
        addKeyItem(b, resultIdKey);
        b.append("\"0\",");
        addKeyItem(b,sizeKey);
        b.append("\"0\",");
        addKeyItem(b,resultsKey);
        b.append("[],");

        EMPTY_JSON_WITHOUT_RESPONSE_TIMES = b.toString();

        addKeyItem(b,repoResponseTimeKey);
        b.append("\"0\",");
        addKeyItem(b,responseTimeKey);
        b.append("\"0\"");

        EMPTY_JSON_RESULTS = b.toString();
    }

    /**
     * returns an estimate in the size of characters that the response will take.
     * This is for sizing the initial StringBuilder used for building the results.
     * @param numResults
     * @return
     */
    private int estimateResponseSize(int numResults) {
        return estimatedResultsSize + ((resultItemSize + idSize + 6) * numResults) + (numResults-1);
    }

    private void addKeyItem(StringBuilder result, String item) {
        result.append('"').append(item).append('"').append(':');
    }

    private void addValueItem(StringBuilder result, String item) {
        result.append('"').append(item).append('"');
    }

    private String createJson(SearchResultEventWithSearchRequestKey<FrequentlyRelatedSearchResult[]> searchResultsEvent) {
        SearchResultsEvent<FrequentlyRelatedSearchResult[]> event = searchResultsEvent.getResponse();
        if(event==null) return createEmptyJson(searchResultsEvent);
        FrequentlyRelatedSearchResult[] results = event.getSearchResults();
        int resultsSize = results.length;
        if(resultsSize==0) return createEmptyJson(searchResultsEvent);

        StringBuilder b = new StringBuilder(estimateResponseSize(resultsSize));
        b.append('{');
        addKeyItem(b, sizeKey);
        addValueItem(b,Integer.toString(resultsSize));
        b.append(',');
        addKeyItem(b, resultsKey);
        b.append('[');


        int resultsMinusOne = resultsSize -1;
        for (int i=0;i<resultsMinusOne;i++) {
            b.append('{');
            addKeyItem(b, resultIdKey);
            FrequentlyRelatedSearchResult res = results[i];
            addValueItem(b, res.getRelatedItemId());
            b.append(',');
            addKeyItem(b, resultFrequencyKey);
            addValueItem(b, Long.toString(res.getFrequency()));
            String sourceDoc = res.getSourceDoc();
            if(relateDocumentIndexingEnabled && sourceDoc != null) {
                b.append(',');
                addKeyItem(b,sourceKey);
                b.append(sourceDoc);
            }
            b.append('}').append(',');
        }
        FrequentlyRelatedSearchResult res = results[resultsMinusOne];
        b.append('{');
        addKeyItem(b, resultIdKey);
        addValueItem(b, res.getRelatedItemId());
        b.append(',');
        addKeyItem(b, resultFrequencyKey);
        addValueItem(b, Long.toString(res.getFrequency()));

        String sourceDoc = res.getSourceDoc();
        if(relateDocumentIndexingEnabled && sourceDoc != null) {
            b.append(',');
            addKeyItem(b,sourceKey);
            b.append(sourceDoc);
        }

        b.append('}').append(']').append(',');

        addKeyItem(b,repoResponseTimeKey);
        addValueItem(b, Long.toString(searchResultsEvent.getSearchExecutionTime()));
        b.append(',');
        addKeyItem(b, responseTimeKey);
        addValueItem(b, Long.toString((System.nanoTime() - searchResultsEvent.getStartOfSearchRequestProcessing()) / 1000000));
        b.append('}');

        return b.toString();

    }

    private String createEmptyJson(SearchResultEventWithSearchRequestKey<FrequentlyRelatedSearchResult[]> searchResultsEvent) {

        StringBuilder b = new StringBuilder(estimatedResultsSize);
        b.append(EMPTY_JSON_WITHOUT_RESPONSE_TIMES);

        addKeyItem(b,repoResponseTimeKey);
        addValueItem(b,Long.toString(searchResultsEvent.getSearchExecutionTime()));
        b.append(',');
        addKeyItem(b,responseTimeKey);
        addValueItem(b,Long.toString((System.nanoTime() - searchResultsEvent.getStartOfSearchRequestProcessing())/1000000));
        b.append('}');
        return b.toString();
    }

    @Override
    public String contentType() {
        return JSON_CONTENT_TYPE;
    }

    @Override
    public String convertToString(SearchResultEventWithSearchRequestKey<FrequentlyRelatedSearchResult[]> results) {

        if(results==null) {
            return EMPTY_JSON_RESULTS;
        }
        else {
            return createJson(results);
        }
    }
}
