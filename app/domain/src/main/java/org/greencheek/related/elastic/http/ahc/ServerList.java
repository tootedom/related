package org.greencheek.related.elastic.http.ahc;

import com.ning.http.client.AsyncHttpClient;

/**
 * Represents a list of es servers that are being connected to.
 *
 * Created by dominictootell on 19/02/2014.
 */
public interface ServerList {


    /**
     * The identifier that uniquely identifies that list of hosts
     * the are in the list of servers.  This is used to compare
     * if the list of servers already held by the class are the same as
     * those sniffed from es.
     *
     * For example if the list of servers is:  server1:9200, server2:9200
     * If a sniff operation is performed on ES, and this results in no new servers: server1:9200, server2:9200
     * Then we do not need to update the server list.
     *
     * @return
     */
    String getHostsStringIndentifier();

    /**
     * Get the next host that should be sent any request.
     * @return
     */
    String getHost();

    /**
     * Get the http client that should be used for talking to the hosts.
     * @return
     */
    AsyncHttpClient getClient();

    /**
     * The number of hosts that are contained within the
     * server list
     * @return
     */
    int getNumberOfHosts();


    /**
     * Returns the hosts in the server list.
     * This should not leak the internal list.
     * @return
     */
    String[] getHostList();
}
