package org.greencheek.relatedproduct.searching.repository;

import org.greencheek.relatedproduct.elastic.ElasticSearchClientFactory;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 15/12/2013
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
public interface ElasticSearchClientFactoryCreator {
    ElasticSearchClientFactory getElasticSearchClientConnectionFactory(Configuration configuration);
}
