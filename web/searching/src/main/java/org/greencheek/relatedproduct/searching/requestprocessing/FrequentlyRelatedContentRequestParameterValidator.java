package org.greencheek.relatedproduct.searching.requestprocessing;

import org.greencheek.relatedproduct.util.config.Configuration;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 23:21
 * To change this template use File | Settings | File Templates.
 */
public class FrequentlyRelatedContentRequestParameterValidator implements SearchRequestParameterValidator {

    public final ValidationMessage VALID_ID_MESSAGE;
    public final ValidationMessage INVALID_ID_MESSAGE;

    private final Configuration configuration;

    public FrequentlyRelatedContentRequestParameterValidator(Configuration configuration) {
        this.configuration = configuration;
        VALID_ID_MESSAGE = new ValidationMessage(true,configuration.getRequestParameterForId(),"");
        INVALID_ID_MESSAGE = new ValidationMessage(false,configuration.getRequestParameterForId(),"no id present in parameters");
    }

    @Override
    public ValidationMessage validateParameters(Map<String, String> requestParameters) {
        String idKey = configuration.getKeyForFrequencyResultId();
        String id = requestParameters.get(idKey);
        if(id == null || id.length()==0) return INVALID_ID_MESSAGE;
        else return VALID_ID_MESSAGE;
    }
}
