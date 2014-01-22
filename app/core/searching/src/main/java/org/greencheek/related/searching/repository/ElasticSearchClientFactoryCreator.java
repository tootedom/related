package org.greencheek.related.searching.repository;

import org.greencheek.related.elastic.ElasticSearchClientFactory;
import org.greencheek.related.util.config.Configuration;

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
