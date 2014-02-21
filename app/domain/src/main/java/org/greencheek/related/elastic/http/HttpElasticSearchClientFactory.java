package org.greencheek.related.elastic.http;

/**
 * Creates a client that uses http to talk to the elastic search cluster
 *
 * Created by dominictootell on 06/02/2014.
 */
public interface HttpElasticSearchClientFactory {
    // Return a new client that can be used to talk http to the
    // elasticsearch cluster.
    HttpElasticClient getClient();

}
