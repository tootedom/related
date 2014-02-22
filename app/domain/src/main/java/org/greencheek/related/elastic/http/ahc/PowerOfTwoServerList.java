package org.greencheek.related.elastic.http.ahc;

import com.ning.http.client.AsyncHttpClient;
import org.greencheek.related.util.parsing.HostParsingUtil;


/**
 * Given a unique array of hosts.  Sizes that array so that it is a power of two
 * The getHost method will use bitwise & to loop what is then effectively a ringbuffer.
 * Created by dominictootell on 19/02/2014.
 */
public class PowerOfTwoServerList implements ServerList {
    private final AsyncHttpClient client;
    private final String[] hostList;
    private final String[] originalHostsList;
    private final int numberOfHosts;
    private final String allHosts;
    private final int mask;

    // padded to avoid false sharing.
    private final int[] currentHost = new int[30];
    private static final int COUNTER_POS = 14;


    public PowerOfTwoServerList(AsyncHttpClient client, String[] hostList) {
        this.client = client;
        this.hostList = HostParsingUtil.getHostsToPowerOfTwo(hostList);
        this.originalHostsList = hostList;
        this.allHosts = HostParsingUtil.toString(hostList);
        this.numberOfHosts = hostList.length;
        this.mask = numberOfHosts-1;
    }

    @Override
    public String getHostsStringIndentifier() {
        return allHosts;
    }

    @Override
    public String getHost() {
        return hostList[currentHost[COUNTER_POS]++ & mask];
    }

    @Override
    public AsyncHttpClient getClient() {
        return client;
    }

    @Override
    public int getNumberOfHosts() {
        return numberOfHosts;
    }

    @Override
    public String[] getHostList() {
        return originalHostsList.clone();
    }


}
