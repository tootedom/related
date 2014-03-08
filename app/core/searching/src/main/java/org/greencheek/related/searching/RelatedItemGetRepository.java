package org.greencheek.related.searching;

import java.util.Map;

/**
 * Created by dominictootell on 05/03/2014.
 */
public interface RelatedItemGetRepository {

    /**
     * Returns the related item documents from the backend repository, as a String.
     * @param ids The list of ids
     * @return a map containing all the keys, against the source document, if the source document is
     * not found, or an exception occurred when finding the doc the value must be "{}"
     */
    public Map<String,String> getRelatedItemDocument(String[] ids);
    public void shutdown();
}
