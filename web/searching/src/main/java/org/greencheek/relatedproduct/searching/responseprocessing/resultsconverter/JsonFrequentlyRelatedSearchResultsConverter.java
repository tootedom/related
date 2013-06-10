package org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.greencheek.relatedproduct.domain.searching.FrequentlyRelatedSearchResult;
import org.greencheek.relatedproduct.domain.searching.FrequentlyRelatedSearchResults;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 08/06/2013
 * Time: 18:54
 * To change this template use File | Settings | File Templates.
 */
public class JsonFrequentlyRelatedSearchResultsConverter implements SearchResultsConverter {

    private static final String JSON_CONTENT_TYPE = "application/json";
    private final FrequentlyRelatedSearchResults resultsToTransform;
    private final Configuration configuration;

    public JsonFrequentlyRelatedSearchResultsConverter(Configuration configuration,
                                                       FrequentlyRelatedSearchResults results) {
        this.configuration = configuration;
        this.resultsToTransform = results;
    }

    public JSONObject createJson(FrequentlyRelatedSearchResults results) {
        JSONObject object = new JSONObject();
        int resultsSize = results.getNumberOfResults();
        object.put(configuration.getKeyForFrequencyResultOverallResultsSize(),resultsSize);

        if(resultsSize==0) {
            object.put(configuration.getKeyForFrequencyResults(),new JSONArray());
        } else {
            JSONArray array = new JSONArray();
            for(FrequentlyRelatedSearchResult res : results.getResults()) {
                JSONObject result = new JSONObject();
                result.put(configuration.getKeyForFrequencyResultSize(),res.getFrequency());
                result.put(configuration.getKeyForFrequencyResultName(),res.getRelatedProductId());
                array.add(result);
            }
            object.put(configuration.getKeyForFrequencyResults(),new JSONArray());
        }
        return object;

    }

    @Override
    public String contentType() {
        return JSON_CONTENT_TYPE;
    }

    @Override
    public String convertToString() {
        JSONObject object = createJson(this.resultsToTransform);
        return object.toJSONString(JSONStyle.MAX_COMPRESS);
    }
}
