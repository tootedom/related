package org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.relatedproduct.api.searching.FrequentlyRelatedSearchResults;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 08/06/2013
 * Time: 18:54
 * To change this template use File | Settings | File Templates.
 */
public class JsonFrequentlyRelatedSearchResultsConverter implements SearchResultsConverter {

    private static final Logger log = LoggerFactory.getLogger(JsonFrequentlyRelatedSearchResultsConverter.class);


    private static final String JSON_CONTENT_TYPE = "application/json";
    private final Configuration configuration;

    public JsonFrequentlyRelatedSearchResultsConverter(Configuration configuration) {
        this.configuration = configuration;
    }

    private Map<String,Object> createJson(FrequentlyRelatedSearchResults results) {
        if(results==null) return createEmptyJson();
        int resultsSize = results.getNumberOfResults();
        if(resultsSize==0) return createEmptyJson();

        Map<String,Object> resultsMap = new HashMap<String,Object>((int)Math.ceil((2 + (resultsSize*2))/0.75));
        resultsMap.put(configuration.getKeyForFrequencyResultOverallResultsSize(),Integer.toString(resultsSize));

        Map<String, String>[] relatedProducts = new HashMap[resultsSize];
        int i = 0;
        for (FrequentlyRelatedSearchResult res : results.getResults()) {
            Map<String, String> product = new HashMap<String, String>(3);

            product.put(configuration.getKeyForFrequencyResultOccurrence(), Long.toString(res.getFrequency()));
            product.put(configuration.getKeyForFrequencyResultId(), res.getRelatedProductId());
            relatedProducts[i++] = product;

        }
        resultsMap.put(configuration.getKeyForFrequencyResults(), relatedProducts);

        return resultsMap;

    }

    private Map<String,Object> createEmptyJson() {
        Map<String,Object> resultsMap = new HashMap<String,Object>(4);
        resultsMap.put(configuration.getKeyForFrequencyResultOverallResultsSize(),"0");
        resultsMap.put(configuration.getKeyForFrequencyResults(),new String[0]);
        return resultsMap;
    }

    @Override
    public String contentType() {
        return JSON_CONTENT_TYPE;
    }

    @Override
    public String convertToString(SearchResultsEvent results) {
        Map<String,Object> jsonResults = null;

        if(results==null) {
            jsonResults = createEmptyJson();
        }
        else {
            if(results.getSearchType()!= RelatedProductSearchType.FREQUENTLY_RELATED_WITH) {
                jsonResults = createEmptyJson();
            } else {
                jsonResults = createJson(results.getFrequentlyRelatedSearchResults());
            }
        }
        return JSONObject.toJSONString(jsonResults,JSONStyle.LT_COMPRESS);
    }
}
