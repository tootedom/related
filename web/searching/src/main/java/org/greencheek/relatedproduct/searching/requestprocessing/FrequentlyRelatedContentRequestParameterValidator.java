package org.greencheek.relatedproduct.searching.requestprocessing;

import org.greencheek.relatedproduct.util.config.Configuration;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

/**
 * verifies the presence of the field {@link org.greencheek.relatedproduct.util.config.Configuration#getRequestParameterForId()}
 * within the given requests parameters provided by the user.
 */
public class FrequentlyRelatedContentRequestParameterValidator implements SearchRequestParameterValidator {

    public final ValidationMessage VALID_ID_MESSAGE;
    public final ValidationMessage INVALID_ID_MESSAGE;
    private final String idParameter;

    public FrequentlyRelatedContentRequestParameterValidator(Configuration configuration) {
        this.idParameter = configuration.getRequestParameterForId();

        VALID_ID_MESSAGE = new ValidationMessage(true,idParameter,"");
        INVALID_ID_MESSAGE = new ValidationMessage(false,idParameter,"no id present in parameters");
    }

    @Override
    public ValidationMessage validateParameters(Map<String, String> requestParameters) {
        String id = requestParameters.get(idParameter);
        if(id == null || id.length()==0) return INVALID_ID_MESSAGE;
        else return VALID_ID_MESSAGE;
    }
}
