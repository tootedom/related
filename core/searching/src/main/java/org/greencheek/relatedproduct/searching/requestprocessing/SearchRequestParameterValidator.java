package org.greencheek.relatedproduct.searching.requestprocessing;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 23:09
 * To change this template use File | Settings | File Templates.
 */
public interface SearchRequestParameterValidator {

    /**
     * The requestParameters can be modified or added to by the validator.
     * @param requestParameters
     * @return
     */
    public ValidationMessage validateParameters(Map<String,String> requestParameters);
}
