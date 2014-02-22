package org.greencheek.related.searching.repository.http;

/**
 * Created by dominictootell on 20/02/2014.
 */
public class FrequentlyRelatedItemSearchResponse {

    public static final TermFacet[] EMPTY_FACETS = new TermFacet[0];
    public static final FrequentlyRelatedItemSearchResponse JSON_RESPONSE_PARSING_ERROR = new FrequentlyRelatedItemSearchResponse(-1,true,"Json Results not parsable",false,EMPTY_FACETS);
    public static final FrequentlyRelatedItemSearchResponse RESPONSE_ERROR = new FrequentlyRelatedItemSearchResponse(-1,true,"Unknown reason for error",false,EMPTY_FACETS);

    private final boolean hasTimedOut;
    private final boolean hasErrored;
    private final String errormsg;
    private final boolean hasTermFacets;
    private final TermFacet[] facetsCounts;
    private final long timeTaken;

    public FrequentlyRelatedItemSearchResponse(long time, boolean errored, String errormsg, boolean timedOut, TermFacet[] terms) {
        this.timeTaken = time;
        this.hasErrored = errored;
        this.errormsg = errormsg;
        this.hasTimedOut = timedOut;
        if(terms==null) terms = EMPTY_FACETS;
        this.facetsCounts = terms;
        if(terms.length==0) {
            hasTermFacets = false;
        } else {
            hasTermFacets = true;
        }
    }

    public boolean hasTimedOut() {
        return hasTimedOut;
    }

    public boolean hasErrored() {
        return hasErrored;
    }

    public String getErrorMessage() {
        return errormsg;
    }

    public boolean hasTermFacets() {
        return hasTermFacets;
    }

    public int getNumberOfFacets() {
        return facetsCounts.length;
    }

    public TermFacet getFacetResult(int resultNumber) {
        return facetsCounts[resultNumber];
    }

    public long getTimeTaken() {
        return timeTaken;
    }
}
