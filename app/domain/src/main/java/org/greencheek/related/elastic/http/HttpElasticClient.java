package org.greencheek.related.elastic.http;


/**
 * Created by dominictootell on 06/02/2014.
 */
public interface HttpElasticClient {
    /**
     * Performs the given search query against the backend
     *
     * @param searchQuery The search query to execute against the backend
     * @return The results of the search query
     */
    public HttpResult executeSearch(HttpMethod method,String path, String searchQuery);


    /**
     * performs a request against the elasticsearch cluster, with the give path.
     * No search body is passed.
     *
     * @param method
     * @param path
     * @return
     */
    public HttpResult executeSearch(HttpMethod method,String path);

    /**
     * shutsdown the client.
     */
    public void shutdown();
}
