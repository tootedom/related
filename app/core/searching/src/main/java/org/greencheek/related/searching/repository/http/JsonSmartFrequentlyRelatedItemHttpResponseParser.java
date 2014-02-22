package org.greencheek.related.searching.repository.http;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.*;
import org.greencheek.related.util.config.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominictootell on 20/02/2014.
 */
public class JsonSmartFrequentlyRelatedItemHttpResponseParser implements FrequentlyRelatedItemHttpResponseParser {

    private final FrequentlyRelatedItemSearchResponse[] parseError = new FrequentlyRelatedItemSearchResponse[]{FrequentlyRelatedItemSearchResponse.JSON_RESPONSE_PARSING_ERROR};

    private static final String TOOK_KEY = "took";
    private static final String RESPONSES_KEY = "responses";
    private static final String ERROR_KEY = "error";
    private static final String TIMED_OUT_KEY = "timed_out";
    private static final String FACET_KEY = "facets";
    private static final String TERMS_KEY = "terms";
    private static final String TERM_KEY = "term";
    private static final String COUNT_KEY = "count";

    private final String facetName;
    public JsonSmartFrequentlyRelatedItemHttpResponseParser(Configuration configuration) {
        this.facetName = configuration.getStorageFrequentlyRelatedItemsFacetResultsFacetName();

    }

    @Override
    public FrequentlyRelatedItemSearchResponse[] parse(String json) {
        JSONParser parser = new JSONParser(JSONParser.MODE_RFC4627);

        try {
            JSONObject object = (JSONObject)parser.parse(json);


            Object responses = object.get(RESPONSES_KEY);
            if(responses==null) return parseError;
            else {
                JSONArray responsesArray = (JSONArray)responses;
                int numberOfResponses = responsesArray.size();
                FrequentlyRelatedItemSearchResponse[] results = new FrequentlyRelatedItemSearchResponse[numberOfResponses];
                for(int i=0;i<numberOfResponses;i++) {

                    Object response = (responsesArray).get(i);
                    if(response instanceof JSONObject) {
                        results[i] = parseResponseObject((JSONObject)response);
                    } else {
                        results[i] = FrequentlyRelatedItemSearchResponse.JSON_RESPONSE_PARSING_ERROR;
                    }
                }
                return results;
            }

        } catch (Exception e){
            return parseError;
        }
    }


    /**
     * "took":14,
       "timed_out":false,
       "facets":{
            "frequently-related-with":{
                "_type":"terms",
                "missing":0,
                 "total":3,
                 "other":0,
                 "terms":[
                     {
                     "term":"4",
                     "count":1
                     },
                     {
                     "term":"3",
                     "count":1
                     },
                     {
                     "term":"2",
                     "count":1
                     }
                 ]
            }
       }
     * @param object
     * @return
     */
    private FrequentlyRelatedItemSearchResponse parseResponseObject(JSONObject object) {
        Object o = object.get(ERROR_KEY);
        if(o!=null) {
            if(o instanceof String) {
                return new FrequentlyRelatedItemSearchResponse(-1,true,(String)o,false,FrequentlyRelatedItemSearchResponse.EMPTY_FACETS);

            } else {
                return FrequentlyRelatedItemSearchResponse.RESPONSE_ERROR;
            }
        }
        else {
            long taken = getResponseTime(object);
            boolean timedOut = getTimedOut(object);
            TermFacet[] facets = getFacets(object);

            return new FrequentlyRelatedItemSearchResponse(taken,false,"",timedOut,facets);
        }
    }

    private TermFacet[] getFacets(JSONObject object) {
        Object o = object.get(FACET_KEY);
        if(o == null) {
            return FrequentlyRelatedItemSearchResponse.EMPTY_FACETS;
        } else {
            if(o instanceof JSONObject) {
                o = ((JSONObject)o).get(facetName);
                if(o==null || !(o instanceof JSONObject) ) {
                    return FrequentlyRelatedItemSearchResponse.EMPTY_FACETS;
                } else {
                    Object termsfacetarray = ((JSONObject)o).get(TERMS_KEY);
                    if(termsfacetarray==null || !(termsfacetarray instanceof JSONArray)) {
                        return FrequentlyRelatedItemSearchResponse.EMPTY_FACETS;
                    } else {
                        JSONArray terms = (JSONArray)termsfacetarray;
                        int noTerms = terms.size();
                        List<TermFacet> facets = new ArrayList<TermFacet>(noTerms);
                        for(int i=0;i<noTerms;i++) {
                            TermFacet f = parseFacet(terms.get(i));
                            if(f!=null) {
                                facets.add(f);
                            }
                        }

                        return facets.toArray(new TermFacet[facets.size()]);
                    }
                }
            } else {
                return FrequentlyRelatedItemSearchResponse.EMPTY_FACETS;
            }
        }
    }


    private TermFacet parseFacet(Object facet) {
        if(facet instanceof JSONObject) {
            long count;
            String termName;
            JSONObject f = (JSONObject)facet;
            Object key = f.get(TERM_KEY);
            Object value = f.get(COUNT_KEY);

            if(value instanceof Integer) {
                count = ((Integer) value).longValue();
            }
            else if(value instanceof Long) {
                count = ((Long) value).longValue();
            } else {
                try {
                    count = Long.parseLong((String)value);
                } catch(NumberFormatException e) {
                    return null;
                }
            }


            if(key instanceof String) {
                termName = (String)key;
            } else {
                return null;
            }

            return new TermFacet(termName,count);

        } else {
            return null;
        }
    }

    private long getResponseTime(JSONObject object) {
        Object o = object.get(TOOK_KEY);
        if(o==null) { return -1;}
        else {
            if(o instanceof Integer) {
                return ((Integer) o).longValue();
            }
            else if(o instanceof Long) {
                return ((Long) o).longValue();
            } else {
                try {
                    return Long.parseLong((String)o);
                } catch(NumberFormatException e) {
                    return -1;
                }
            }
        }
    }

    private boolean getTimedOut(JSONObject object) {
        Object o = object.get(TIMED_OUT_KEY);
        if(o==null) { return false; }
        else {
            if(o instanceof Boolean) {
                return ((Boolean)o).booleanValue();
            } else {
                return Boolean.parseBoolean((String)o);
            }
        }
    }

}
