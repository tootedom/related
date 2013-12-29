package org.greencheek.relatedproduct.api.searching;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 08/06/2013
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
public class FrequentlyRelatedSearchResults {

    public static final FrequentlyRelatedSearchResults EMPTY_RESULTS = new FrequentlyRelatedSearchResults(new FrequentlyRelatedSearchResult[0]);

    private final FrequentlyRelatedSearchResult[] results;
    private final int numberOfResults;

    public FrequentlyRelatedSearchResults(FrequentlyRelatedSearchResult[] results) {
        this.results = results;
        this.numberOfResults = results.length;
    }

    public FrequentlyRelatedSearchResult[] getResults() {
        return results;
    }

    public int getNumberOfResults() {
        return numberOfResults;
    }

    public String toString() {
        return results.toString();
    }

}
