package org.greencheek.relatedproduct.api.searching;

import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.api.RelatedProductInfoIdentifier;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKeyFactory;
import org.greencheek.relatedproduct.util.config.Configuration;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 08/06/2013
 * Time: 17:10
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductSearch {

    public static final String RESULTS_SET_SIZE_KEY = "resultssetsize";
    public static final String ID_KEY = "id";
    public static final int RESULTS_SET_SIZE_KEY_LENGTH = RESULTS_SET_SIZE_KEY.length();
    public static final int ID_KEY_LENGTH = ID_KEY.length();

    private int maxResults;
    private RelatedProductSearchType searchType;
    private boolean validMessage;
    private SearchRequestLookupKey lookupKey;

    private final RelatedProductInfoIdentifier relatedContentId;
    private final RelatedProductAdditionalProperties additionalSearchCriteria;


    public RelatedProductSearch(Configuration config) {
        relatedContentId = new RelatedProductInfoIdentifier(config);
        additionalSearchCriteria = new RelatedProductAdditionalProperties(config,config.getMaxNumberOfSearchCriteriaForRelatedContent());
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public boolean isValidMessage() {
        return this.validMessage;
    }

    public void setValidMessage(boolean valid) {
        this.validMessage = valid;
    }

    public RelatedProductAdditionalProperties getAdditionalSearchCriteria() {
        return this.additionalSearchCriteria;
    }

    public void setRelatedContentId(String id) {
        this.relatedContentId.setId(id);
    }

    public String getRelatedContentId() {
        return this.relatedContentId.toString();
    }

    public RelatedProductInfoIdentifier getRelatedContentIdentifier() {
        return this.relatedContentId;
    }

    private void setRelatedContentId(RelatedProductInfoIdentifier id) {
        id.copyTo(this.relatedContentId);
    }

    public RelatedProductSearchType getRelatedProductSearchType() {
        return this.searchType;
    }

    public void setRelatedProductSearchType(RelatedProductSearchType searchType) {
        this.searchType = searchType;
    }

    public RelatedProductSearch copy(Configuration config) {
        RelatedProductSearch newCopy = new RelatedProductSearch(config);
        newCopy.setRelatedContentId(this.relatedContentId);
        newCopy.setMaxResults(this.maxResults);
        newCopy.setRelatedProductSearchType(this.searchType);
        newCopy.setLookupKey(this.getLookupKey());
        this.additionalSearchCriteria.copyTo(newCopy.additionalSearchCriteria);
        return newCopy;
    }

    public void setLookupKey(SearchRequestLookupKey key) {
        this.lookupKey = key;
    }

    public SearchRequestLookupKey getLookupKey() {
        return lookupKey;
    }

}
