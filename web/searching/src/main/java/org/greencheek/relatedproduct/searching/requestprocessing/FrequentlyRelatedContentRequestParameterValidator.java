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
@Named
public class FrequentlyRelatedContentRequestParameterValidator implements SearchRequestParameterValidator {

    private final Configuration configuration;

    @Inject
    public FrequentlyRelatedContentRequestParameterValidator(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public ValidationMessage validateParameters(Map<String, String> requestParameters) {
        String id = requestParameters.get(configuration.getRequestParameterForId());
        if(id == null) {
            return new ValidationMessage(false,configuration.getRequestParameterForId(),"no id");
        }
        else {

            return new ValidationMessage(true);
        }
    }
}
