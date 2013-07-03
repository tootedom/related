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

    private final Configuration configuration;

    public FrequentlyRelatedContentRequestParameterValidator(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public ValidationMessage validateParameters(Map<String, String> requestParameters) {
        String id = requestParameters.get(configuration.getRequestParameterForId());
        return new ValidationMessage(id.length()!=0,configuration.getRequestParameterForId(),"no id");
    }
}
