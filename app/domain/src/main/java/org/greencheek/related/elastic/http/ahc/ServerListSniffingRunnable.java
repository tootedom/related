package org.greencheek.related.elastic.http.ahc;

import com.ning.http.client.AsyncHttpClient;
import org.greencheek.related.elastic.http.SniffAvailableNodes;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.parsing.HostParsingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * For a given {@link org.greencheek.related.elastic.http.ahc.ServerList} it
 * uses the given {@link org.greencheek.related.elastic.http.SniffAvailableNodes} implementation
 * to determine if ServerList current contains the correct list of nodes that should be used
 * to make http requests.
 */
public class ServerListSniffingRunnable implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(AHCRoundRobinHttpElasticClient.class);

    private final SniffAvailableNodes nodeSniffer;
    private final AtomicReference<ServerList> serverListReferenceToUpdate;
    private final Configuration configuration;

    public ServerListSniffingRunnable(SniffAvailableNodes nodesSniffer,
                                      AtomicReference<ServerList> serverListReferenceToUpdate,
                                      Configuration configuration) {
        this.nodeSniffer = nodesSniffer;
        this.serverListReferenceToUpdate = serverListReferenceToUpdate;
        this.configuration = configuration;
    }

    @Override
    public void run() {
        ServerList list = serverListReferenceToUpdate.get();
        Set<String> sniffedHosts = nodeSniffer.getAvailableNodes(list.getHostList());
        String[] sniffedHostsArray = sniffedHosts.toArray(new String[sniffedHosts.size()]);
        String hostsAsString = HostParsingUtil.toString(sniffedHosts);

        if (!list.getHostsStringIndentifier().equals(hostsAsString)) {
            AsyncHttpClient oldClient = list.getClient();
            ServerList newServerList = new PowerOfTwoServerList(AHCFactory.createClient(configuration, sniffedHostsArray.length), sniffedHostsArray);
            serverListReferenceToUpdate.set(newServerList);

            try {
                oldClient.close();
            } catch (Exception e) {
                log.warn("Problem closing old client for http es round robin");
            }
        }
    }
}