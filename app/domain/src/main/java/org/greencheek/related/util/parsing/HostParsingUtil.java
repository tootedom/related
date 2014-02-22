package org.greencheek.related.util.parsing;

import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.greencheek.related.util.arrayindexing.Util;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dominictootell on 07/02/2014.
 */
public class HostParsingUtil {

    private static final Logger log = LoggerFactory.getLogger(HostParsingUtil.class);

    /**
     * Given a string in the format host[:port],host[:port].  The string is split on comma (,),
     * returning a list of String[].  The String[] is a two element array containing:
     * <ul>
     * <li>String[0] : hostname</li>
     * <li>String[1] : portnumber</li>
     * </ul>
     *
     * The port element is optional, i.e. host,host,host.  In this case the defaultPort is used.
     *
     * @param hostsStr The string to parse and obtain the host names
     * @param defaultPort The default port if one is not specified.
     * @return
     */
    public static List<String[]> parseHosts(String hostsStr, String defaultPort) {
        List<String[]> parsedHosts;
        if(hostsStr!=null && hostsStr.length()>0) {
            String[] hosts = hostsStr.split(",");
            parsedHosts = new ArrayList<>(hosts.length);
            for(String host: hosts) {
                int portSep = host.indexOf(':');
                if(portSep>-1) {
                        String hostName = host.substring(0,portSep);
                        String port = host.substring(portSep+1);
                        log.debug("adding E.S. host Addresss: {}:{}", hostName,port);
                        parsedHosts.add(new String[]{hostName,port});
                } else {
                    log.debug("adding E.S. host Addresss, with default port: {}:{}", host,defaultPort);
                    parsedHosts.add(new String[]{host,defaultPort});
                }
            }
            return parsedHosts;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public static String[] parseHttpHosts(Configuration configuration) {

        String hostsStr = configuration.getElasticSearchHttpHosts();
        String defaultPort = ""+configuration.getElasticSearchHttpPort();

        Set<String> parsedHosts;
        if(hostsStr!=null && hostsStr.length()>0) {
            String[] hosts = hostsStr.split(",");
            parsedHosts = new TreeSet<>();
            for(String host: hosts) {
                StringBuilder hostString = new StringBuilder(30);
                int portSep = host.indexOf(':');
                if(portSep>-1) {

                    String hostName = host.substring(0,portSep);
                    String port = host.substring(portSep+1);
                    hostString.append(hostName).append(':').append(port);
                    log.debug("adding E.S. host Addresss: {}:{}", hostName,port);
                    parsedHosts.add(hostString.toString());
                } else {
                    log.debug("adding E.S. host Addresss, with default port: {}:{}", host,defaultPort);
                    hostString.append(host).append(':').append(defaultPort);
                    parsedHosts.add(hostString.toString());
                }
            }
            return parsedHosts.toArray(new String[parsedHosts.size()]);
        } else {
            return new String[0];
        }
    }

    /**
     * Given an array of host names, makes the host list into a array that has
     * a power of two length.  It does this by reusing existing strings to form the
     * new string.
     *
     * for example.  Given : String[] { "localhost:9999","localhost:8888","localhost:8080"}
     * The resulting array is : String[] { "localhost:9999","localhost:8888","localhost:8080","localhost:9999"}
     *
     * for example.  Given : String[] { "localhost:9999","localhost:8888","localhost:8080","localhost:80","localhost:9200"}
     *
     * The resulting array is : String[] { "localhost:9999","localhost:8888","localhost:8080","localhost:80",
     *                                     "localhost:9200","localhost:9999","localhost:8888","localhost:8080",}
     *
     * @param numberOfHosts
     * @return
     */
    public static String[] getHostsToPowerOfTwo(String[] numberOfHosts) {
        int definedHosts = numberOfHosts.length;
        int numOfHosts = Util.ceilingNextPowerOfTwo(definedHosts);

        Set<String> newHosts = new HashSet<String>(numOfHosts);
        if(definedHosts < numOfHosts) {
            for(int i =0;i < numOfHosts;i++) {
                newHosts.add(numberOfHosts[i%definedHosts]);
            }
        } else {
            return numberOfHosts;
        }
        return newHosts.toArray(new String[newHosts.size()]);
    }

    public static String toString(String[] hosts) {
        StringBuilder hostString = new StringBuilder((hosts.length*18)+hosts.length);
        for(String host : hosts) {
            hostString.append(host).append(',');
        }
        return hostString.toString();
    }

    public static String toString(Set<String> hosts) {
        return toString(hosts.toArray(new String[hosts.size()]));
    }

    /**
     *
     * http://10.0.1.19:9201/_nodes/http
     *
     * From the given json, it extracts the "publish_address":"inet[/10.0.1.19:9201]" elemeent.
     * This is simply done using a regexp.
     * {
     "ok":true,
     "cluster_name":"e09",
     "nodes":{
     "TvWpoPp4Tc2H-e2PR-i-Ng":{
     "name":"Beetle II",
     "transport_address":"inet[/10.0.1.19:9301]",
     "version":"0.90.11",
     "http_address":"inet[/10.0.1.19:9201]",
     "http":{
     "bound_address":"inet[/0:0:0:0:0:0:0:0%0:9201]",
     "publish_address":"inet[/10.0.1.19:9201]",
     "max_content_length":"100mb",
     "max_content_length_in_bytes":104857600
     }
     },
     "JyoPrsJSR4K1VQxEG2O1aQ":{
     "name":"Barracuda",
     "transport_address":"inet[/10.0.1.9:9302]",
     "hostname":"Dominics-MacBook-Pro.local",
     "version":"0.90.11",
     "http_address":"inet[/10.0.1.9:9202]",
     "http":{
     "bound_address":"inet[/0:0:0:0:0:0:0:0:9202]",
     "publish_address":"inet[/10.0.1.9:9202]",
     "max_content_length":"100mb",
     "max_content_length_in_bytes":104857600
     }
     }
     }
     }
     */
    public static Set<String> parseAvailablePublishedHttpServerAddresses(String json) {
        if(json==null) {
            return Collections.EMPTY_SET;
        }
        Set<String> servers = new TreeSet<String>();
        Matcher m = PUBLISH_ADDRESS.matcher(json);
        while(m.find()) {
            servers.add("http://"+m.group(1));
        }
        return servers;
    }

    public static final String[] EMPTY_ARRAY = new String[0];

    public static final Pattern PUBLISH_ADDRESS = Pattern.compile("\"publish_address\":\"[^/]+/([^\\]]+)]");

    public static void main(String[] args) {
        System.out.println(parseAvailablePublishedHttpServerAddresses("{\n" +
                "   \"ok\":true,\n" +
                "   \"cluster_name\":\"e09\",\n" +
                "   \"nodes\":{\n" +
                "      \"TvWpoPp4Tc2H-e2PR-i-Ng\":{\n" +
                "         \"name\":\"Beetle II\",\n" +
                "         \"transport_address\":\"inet[/10.0.1.19:9301]\",\n" +
                "         \"version\":\"0.90.11\",\n" +
                "         \"http_address\":\"inet[/10.0.1.19:9201]\",\n" +
                "         \"http\":{\n" +
                "            \"bound_address\":\"inet[/0:0:0:0:0:0:0:0%0:9201]\",\n" +
                "            \"publish_address\":\"inet[/10.0.1.19:9201]\",\n" +
                "            \"max_content_length\":\"100mb\",\n" +
                "            \"max_content_length_in_bytes\":104857600\n" +
                "         }\n" +
                "      },\n" +
                "      \"JyoPrsJSR4K1VQxEG2O1aQ\":{\n" +
                "         \"name\":\"Barracuda\",\n" +
                "         \"transport_address\":\"inet[/10.0.1.9:9302]\",\n" +
                "         \"hostname\":\"Dominics-MacBook-Pro.local\",\n" +
                "         \"version\":\"0.90.11\",\n" +
                "         \"http_address\":\"inet[/10.0.1.9:9202]\",\n" +
                "         \"http\":{\n" +
                "            \"bound_address\":\"inet[/0:0:0:0:0:0:0:0:9202]\",\n" +
                "            \"publish_address\":\"inet[/10.0.1.9:9202]\",\n" +
                "            \"max_content_length\":\"100mb\",\n" +
                "            \"max_content_length_in_bytes\":104857600\n" +
                "         }\n" +
                "      }\n" +
                "   }\n" +
                "}"));

        System.out.println(parseAvailablePublishedHttpServerAddresses("{}"));

        System.out.println(parseAvailablePublishedHttpServerAddresses("{\"ok\":true,\"cluster_name\":\"relateditems\",\"nodes\":{\"6i5B4tq7RgWo8rP0dt7RCg\":{\"name\":\"Skinhead\",\"transport_address\":\"inet[Dominics-MacBook-Pro.local/127.0.0.1:9300]\",\"hostname\":\"Dominics-MacBook-Pro.local\",\"version\":\"0.90.9\",\"http_address\":\"inet[Dominics-MacBook-Pro.local/127.0.0.1:9200]\",\"http\":{\"bound_address\":\"inet[/0:0:0:0:0:0:0:0:9200]\",\"publish_address\":\"inet[Dominics-MacBook-Pro.local/127.0.0.1:9200]\",\"max_content_length\":\"100mb\",\"max_content_length_in_bytes\":104857600}}}}"));

        System.out.println(parseAvailablePublishedHttpServerAddresses(null));

        System.out.println(parseAvailablePublishedHttpServerAddresses(""));



    }
}
