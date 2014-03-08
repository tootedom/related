package org.greencheek.related.searching.repository;

import org.greencheek.related.searching.RelatedItemGetRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dominictootell on 05/03/2014.
 */
public class RelatedItemNoopGetRepository implements RelatedItemGetRepository {

    private final static String EMPTY_RESULT = "{}";

    public static Map<String,String> getEmptyResults(String[] ids) {
        Map<String,String> results = new HashMap<String,String>(ids.length);

        for(String id: ids) {
            results.put(id,EMPTY_RESULT);
        }
        return results;
    }

    @Override
    public Map<String, String> getRelatedItemDocument(String[] ids) {
        return getEmptyResults(ids);
    }

    @Override
    public void shutdown() {

    }
}
