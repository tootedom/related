package org.greencheek.related.searching.requestprocessing;

import org.greencheek.related.api.searching.RelatedItemSearchType;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 10/06/2013
 * Time: 22:16
 * To change this template use File | Settings | File Templates.
 */
public interface SearchRequestParameterValidatorLocator {
    SearchRequestParameterValidator getValidatorForType(RelatedItemSearchType type);
}
