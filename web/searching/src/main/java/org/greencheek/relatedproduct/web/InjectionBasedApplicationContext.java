package org.greencheek.relatedproduct.web;

import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestProcessor;
import org.greencheek.relatedproduct.util.config.Configuration;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 10/06/2013
 * Time: 21:44
 * To change this template use File | Settings | File Templates.
 */
@Named
public class InjectionBasedApplicationContext implements ApplicationCtx{

    private final Configuration config;
    private final RelatedProductSearchRequestProcessor searchRequestProcessor;

    @Inject
    public InjectionBasedApplicationContext(Configuration configuration,
                                            RelatedProductSearchRequestProcessor searchProcessor) {
        this.config = configuration;
        this.searchRequestProcessor = searchProcessor;

    }

    @Override
    public RelatedProductSearchRequestProcessor getRequestProcessor() {
        return searchRequestProcessor;
    }

    @Override
    public Configuration getConfiguration() {
        return config;
    }
}
