package org.greencheek.relatedproduct.api.searching;

import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperty;
import org.greencheek.relatedproduct.api.RelatedProductInfoIdentifier;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
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
    private static final int RESULTS_SET_SIZE_KEY_LENGTH = RESULTS_SET_SIZE_KEY.length();
    private static final int ID_KEY_LENGTH = ID_KEY.length();

    private int maxResults;
    private RelatedProductSearchType searchType;
    private boolean validMessage;

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


    private int getStringLength(Configuration configuration) {
        return additionalSearchCriteria.getStringLength(configuration) + configuration.getRelatedProductIdLength() + RESULTS_SET_SIZE_KEY_LENGTH +
                ID_KEY_LENGTH + 4;

    }

    public SearchRequestLookupKey getLookupKey(Configuration configuration) {
        StringBuilder string = new StringBuilder(getStringLength(configuration));
        string.append(ID_KEY).append('=').append(relatedContentId.toString()).append('&');
        string.append(RESULTS_SET_SIZE_KEY).append('=').append(maxResults).append('&');
        string.append(additionalSearchCriteria.toString(configuration));
        return new SearchRequestLookupKey(string.toString());
    }

    public RelatedProductSearch copy(Configuration config) {
        RelatedProductSearch newCopy = new RelatedProductSearch(config);
        newCopy.setRelatedContentId(this.relatedContentId);
        newCopy.setMaxResults(this.maxResults);
        newCopy.setRelatedProductSearchType(this.searchType);
        this.additionalSearchCriteria.copyTo(newCopy.additionalSearchCriteria);
        return newCopy;
    }

}
