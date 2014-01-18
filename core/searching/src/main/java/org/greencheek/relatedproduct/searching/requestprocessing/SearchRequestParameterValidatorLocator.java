package org.greencheek.relatedproduct.searching.requestprocessing;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 10/06/2013
 * Time: 22:16
 * To change this template use File | Settings | File Templates.
 */
public interface SearchRequestParameterValidatorLocator {
    SearchRequestParameterValidator getValidatorForType(RelatedProductSearchType type);
}
