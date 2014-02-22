package org.greencheek.related.elastic.http;

import java.util.List;
import java.util.Set;

/**
 * Created by dominictootell on 07/02/2014.
 */
public interface SniffAvailableNodes {

    /**
     * Returns a list of the nodes (in alphabetic order).  Simply returns
     * host:port,host:port as a list of strings containing http://host:port.
     * @return
     */
    Set<String> getAvailableNodes(String[] hosts);


    /**
     * shuts down the sniffing implementation
     */
    void shutdown();
}
