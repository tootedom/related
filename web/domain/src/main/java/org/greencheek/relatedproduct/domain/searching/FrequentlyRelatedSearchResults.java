package org.greencheek.relatedproduct.domain.searching;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 08/06/2013
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
public class FrequentlyRelatedSearchResults {


    private final List<FrequentlyRelatedSearchResult> results;
    private final int numberOfResults;

    public FrequentlyRelatedSearchResults(List<FrequentlyRelatedSearchResult> results) {
        this.results = new ArrayList<FrequentlyRelatedSearchResult>(results);
        this.numberOfResults = results.size();
    }

    public List<FrequentlyRelatedSearchResult> getResults() {
        return results;
    }

    public int getNumberOfResults() {
        return numberOfResults;
    }

    public String toString() {
        return results.toString();
    }



}
