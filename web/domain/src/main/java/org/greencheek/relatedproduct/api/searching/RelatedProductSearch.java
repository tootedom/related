package org.greencheek.relatedproduct.api.searching;

import javolution.io.Struct;
import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperty;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.util.config.Configuration;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 08/06/2013
 * Time: 17:10
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductSearch extends Struct {

    public static final String RESULTS_SET_SIZE_KEY = "resultssetsize";
    public static final String ID_KEY = "id";
    private static final int RESULTS_SET_SIZE_KEY_LENGTH = RESULTS_SET_SIZE_KEY.length();
    private static final int ID_KEY_LENGTH = ID_KEY.length();

    public final Signed32 maxResults;
    public final UTF8String relatedContentId;
    public final RelatedProductAdditionalProperties additionalSearchCriteria;
    public final Enum32<RelatedProductSearchType> searchType;
    public final Bool validMessage;


    public RelatedProductSearch(Configuration config) {
        validMessage = new Bool();
        maxResults = new Signed32();
        relatedContentId = new UTF8String(config.getRelatedProductIdLength());
        additionalSearchCriteria = new RelatedProductAdditionalProperties(config,config.getMaxNumberOfSearchCriteriaForRelatedContent());
        searchType = new Enum32<RelatedProductSearchType>(RelatedProductSearchType.values());

    }


    private int getStringLength(Configuration configuration) {
        return additionalSearchCriteria.getStringLength(configuration) + configuration.getRelatedProductIdLength() + RESULTS_SET_SIZE_KEY_LENGTH +
                ID_KEY_LENGTH + 4;

    }

    public SearchRequestLookupKey getLookupKey(Configuration configuration) {
        StringBuilder string = new StringBuilder(getStringLength(configuration));
        string.append(ID_KEY).append('=').append(relatedContentId.get()).append('&');
        string.append(RESULTS_SET_SIZE_KEY).append('=').append(maxResults.get()).append('&');
        string.append(additionalSearchCriteria.toString(configuration));
        return new SearchRequestLookupKey(string.toString());
    }

    public RelatedProductSearch copy(Configuration config) {
        RelatedProductSearch newCopy = new RelatedProductSearch(config);
        newCopy.setByteBuffer(ByteBuffer.allocate(newCopy.size()),0);
        newCopy.relatedContentId.set(this.relatedContentId.get());
        newCopy.maxResults.set(this.maxResults.get());
        newCopy.searchType.set(this.searchType.get());
        short numOfProps = this.additionalSearchCriteria.getNumberOfProperties();
        newCopy.additionalSearchCriteria.setNumberOfProperties(numOfProps);
        for(int i=0;i<numOfProps;i++) {
            RelatedProductAdditionalProperty oldProp = this.additionalSearchCriteria.additionalProperties[i];
            RelatedProductAdditionalProperty newProp = newCopy.additionalSearchCriteria.additionalProperties[i];

            oldProp.copyTo(newProp);
        }
        return newCopy;
    }

}
