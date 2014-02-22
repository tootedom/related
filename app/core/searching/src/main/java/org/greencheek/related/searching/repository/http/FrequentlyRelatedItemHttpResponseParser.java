package org.greencheek.related.searching.repository.http;

/**
 * Created by dominictootell on 20/02/2014.
 */
public interface FrequentlyRelatedItemHttpResponseParser {
    public FrequentlyRelatedItemSearchResponse[] parse(String json);
}
