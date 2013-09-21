package org.greencheek.relatedproduct.elastic;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 22:18
 * To change this template use File | Settings | File Templates.
 */
public final class TransportBasedElasticSearchClientFactory implements ElasticSearchClientFactory {
    private static final Logger log = LoggerFactory.getLogger(TransportBasedElasticSearchClientFactory.class);

    private final Client client;
    private final String defaultConfigFileName;
    private final String configFileName;


    public TransportBasedElasticSearchClientFactory(Configuration configuration,
                                                    String defaultConfigFileName,
                                                    String configFileName) {
        this(ImmutableSettings.EMPTY,configuration,defaultConfigFileName,configFileName);
    }

    public TransportBasedElasticSearchClientFactory(Settings defaultSettings,
                                                    Configuration configuration,
                                                    String defaultConfigFileName,
                                                    String configFileName) {
        this.defaultConfigFileName = defaultConfigFileName;
        this.configFileName = configFileName;
        this.client = createClient(defaultSettings, configuration);
    }

    public TransportBasedElasticSearchClientFactory(Configuration configuration) {
        this(ImmutableSettings.EMPTY,configuration);
    }

    public TransportBasedElasticSearchClientFactory(Settings defaultSettings, Configuration configuration) {
        this(defaultSettings,configuration,configuration.getElasticSearchClientDefaultSettingFileName(),
                configuration.getElasticSearchClientOverrideSettingFileName());
    }


    private Client createClient(Settings defaultSettings,Configuration config) {

        ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder().put("cluster.name",config.getStorageClusterName());
//                .put("node.local","true").put("node.client","true").put("http.enabled","false");


        // load the pre-packaged defaults
//        settings.loadFromClasspath(defaultConfigFileName);

        // Set the default cluster name from the configuration, then possibility to override
        // from the given settings object.  Then once again the possibility to override from
        // the xml filenames
//        settings.put(defaultSettings);

        // load the overrides on the classpath
//        settings.loadFromClasspath(configFileName);

        TransportClient client = new TransportClient(settings);

        addHostsFromConfiguration(client, config);

        return client;
    }

    private void addHostsFromConfiguration(TransportClient client, Configuration config) {
        int defaultPort =  config.getDefaultElasticSearchPort();
        String s = config.getElasticSearchTransportHosts();
        if(s!=null && s.length()>0) {
            String[] hosts = s.split(",");
            for(String host: hosts) {
                int portSep = host.indexOf(':');
                if(portSep>-1) {
                    try {
                        String hostName = host.substring(0,portSep);
                        String port = host.substring(portSep+1);
                        log.debug("adding ES Transport Addresss: {} {}", hostName,port);
                        client.addTransportAddress(new InetSocketTransportAddress(hostName,Integer.valueOf(port)));
                    } catch(NumberFormatException e) {
                        client.addTransportAddress(new InetSocketTransportAddress(host,defaultPort));
                    }
                } else {
                    client.addTransportAddress(new InetSocketTransportAddress(host,defaultPort));
                }
            }
        }
    }

    @Override
    public Client getClient() {
        return client;
    }

    @Override
    @PreDestroy
    public void shutdown() {
        log.debug("Shutting down ElasticSearch client");
        try {
            client.close();
        } catch(Exception e) {
            log.warn("Unable to shut down the ElasticSearch client");
        }

    }
}
