package org.greencheek.related.searching.repository.http;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.related.elastic.http.*;
import org.greencheek.related.searching.RelatedItemGetRepository;
import org.greencheek.related.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.related.searching.domain.api.SearchResultsEvent;
import org.greencheek.related.searching.repository.RelatedItemNoopGetRepository;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dominictootell on 05/03/2014.
 */
public class ElasticSearchRelatedItemHttpGetRepository implements RelatedItemGetRepository {
    private static final Logger log = LoggerFactory.getLogger(ElasticSearchRelatedItemHttpGetRepository.class);



    private final String url;
    private final HttpElasticClient elasticClient;
    private final String keyForTiming;

    public ElasticSearchRelatedItemHttpGetRepository(Configuration configuration,
                                                     HttpElasticSearchClientFactory factory) {

        this.url = HttpUtil.createMGetHttpEnpointUrlPath(configuration);
        this.elasticClient = factory.getClient();
        this.keyForTiming = configuration.getKeyForStorageGetResponseTime();

    }

    private String createArray(String[] ids) {
        StringBuilder b = new StringBuilder((3*ids.length)+1);
        b.append('[');
        for(int i=0;i<ids.length;i++) {
            b.append('"').append(ids[i]).append('"').append(',');
        }
        b.setCharAt(b.length()-1,']');
        return b.toString();
    }

    @Override
    public Map<String, String> getRelatedItemDocument(String[] ids) {
        Map<String,String> getResults = RelatedItemNoopGetRepository.getEmptyResults(ids);
        log.debug("MGET request to execute {} get request", ids.length);

        String arrayString = createArray(ids);
        String jsonStart = "{\"ids\":";
        StringBuilder b = new StringBuilder(arrayString.length()+jsonStart.length()+1);
        b.append(jsonStart).append(arrayString).append('}');

        String searchJson = b.toString();

        log.debug("MGET request json is {}",searchJson);

        SearchResultEventWithSearchRequestKey[] results;
        HttpResult sr;
        long startNanos = System.nanoTime();

        sr = elasticClient.executeSearch(HttpMethod.POST,url,searchJson);

        HttpSearchExecutionStatus searchRequestStatus = sr.getStatus();
        if (searchRequestStatus == HttpSearchExecutionStatus.OK) {
            log.debug("MGET Processing results for get request(s)");
            String responseBody = sr.getResult();
            Map<String,String> mappedResults = processResults(responseBody);
            getResults.putAll(mappedResults);
            log.debug("MGET Completed, returning processed results.");
        } else if (searchRequestStatus == HttpSearchExecutionStatus.REQUEST_FAILURE) {
            log.warn("MGET Exception executing get request");
        } else {
            if (searchRequestStatus == HttpSearchExecutionStatus.REQUEST_TIMEOUT) {
                log.warn("Request timeout executing search request");
            } else {
                log.warn("Connection timeout executing search request");
            }

        }
        long time = (System.nanoTime() - startNanos) / 1000000;
        getResults.put(keyForTiming,Long.toString(time));

        return getResults;
    }



    @Override
    public void shutdown() {
        try {
            elasticClient.shutdown();
        } catch(Exception e) {
            log.warn("unable to stop ");
        }
    }

    /*
     * If there's another implementation in the future, then we can extract the
     * interface at that point.
     */

    public static Map<String,String> processResults(String responseBody) {
        JSONParser parser = new JSONParser(JSONParser.MODE_RFC4627);

        try {
            JSONObject object = (JSONObject)parser.parse(responseBody);
            Object results = object.get("docs");
            if(results!=null && results instanceof JSONArray) {
                return parseDocs((JSONArray)results);
            }else {
                return Collections.EMPTY_MAP;
            }
        } catch (Exception e){
            return Collections.EMPTY_MAP;
        }
    }

    public static Map<String,String> parseDocs(JSONArray array) {
        Map<String,String> map = new HashMap<String,String>(array.size());
        for(Object o : array) {
            if(o instanceof JSONObject) {
                parseDoc((JSONObject)o,map);
            }
        }
        return map;
    }

    private static void parseDoc(JSONObject doc, Map<String,String> results) {
        Object id = doc.get("_id");
        Object source = doc.get("_source");
        Object error = doc.get("error");

        if(id==null) {
            return;
        }

        if(error!=null) {
            log.error("MGET error obtaining content for id {} : {}", id,error);
        } else {
            if(source==null) {
                log.warn("MGET unable to find content for id {}",id);
            } else {
                if(source instanceof JSONObject) {
                    JSONObject sourceObj = (JSONObject)source;
                    String sourceStr = sourceObj.toJSONString();
                    if(!sourceStr.trim().isEmpty()) {
                        results.put(id.toString(),sourceStr);
                    }
                } else {
                    log.error("Unable to decode source for id {}",id);
                }
            }
        }
    }
}
