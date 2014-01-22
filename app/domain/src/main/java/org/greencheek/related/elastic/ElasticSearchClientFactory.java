package org.greencheek.related.elastic;

import org.elasticsearch.client.Client;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 21:23
 * To change this template use File | Settings | File Templates.
 */
public interface ElasticSearchClientFactory {
    public Client getClient();
    public void shutdown();
}
