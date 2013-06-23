package org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.greencheek.relatedproduct.domain.searching.FrequentlyRelatedSearchResult;
import org.greencheek.relatedproduct.domain.searching.FrequentlyRelatedSearchResults;
import org.greencheek.relatedproduct.util.config.Configuration;

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

    private static final String JSON_CONTENT_TYPE = "application/json";
    private final FrequentlyRelatedSearchResults resultsToTransform;
    private final Configuration configuration;

    public JsonFrequentlyRelatedSearchResultsConverter(Configuration configuration,
                                                       FrequentlyRelatedSearchResults results) {
        this.configuration = configuration;
        this.resultsToTransform = results;
    }

    private Map<String,Object> createJson(FrequentlyRelatedSearchResults results) {
        if(results==null) return createEmptyJson();
        int resultsSize = results.getNumberOfResults();
        if(resultsSize==0) return createEmptyJson();

        Map<String,Object> resultsMap = new HashMap<String,Object>(2 + (resultsSize*2));
        resultsMap.put(configuration.getKeyForFrequencyResultOverallResultsSize(),Integer.toString(resultsSize));

        Map<String, String>[] relatedProducts = new HashMap[resultsSize];
        int i = 0;
        for (FrequentlyRelatedSearchResult res : results.getResults()) {
            Map<String, String> product = new HashMap<String, String>(2);

            product.put(configuration.getKeyForFrequencyResultOccurrence(), Long.toString(res.getFrequency()));
            product.put(configuration.getKeyForFrequencyResultId(), res.getRelatedProductId());
            relatedProducts[i] = product;
            i++;
        }
        resultsMap.put(configuration.getKeyForFrequencyResults(), relatedProducts);

        return resultsMap;

    }

    private Map<String,Object> createEmptyJson() {
        Map<String,Object> resultsMap = new HashMap<String,Object>(2);
        resultsMap.put(configuration.getKeyForFrequencyResultOverallResultsSize(),"0");
        resultsMap.put(configuration.getKeyForFrequencyResults(),new String[0]);
        return resultsMap;
    }

    @Override
    public String contentType() {
        return JSON_CONTENT_TYPE;
    }

    @Override
    public String convertToString() {
        Map<String,Object> object = createJson(this.resultsToTransform);
        return JSONObject.toJSONString(object,JSONStyle.LT_COMPRESS);
    }
}
