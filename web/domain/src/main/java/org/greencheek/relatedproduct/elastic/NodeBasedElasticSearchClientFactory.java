package org.greencheek.relatedproduct.elastic;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.Classes;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 22:18
 * To change this template use File | Settings | File Templates.
 */
public class NodeBasedElasticSearchClientFactory implements ElasticSearchClientFactory {
    private static final Logger log = LoggerFactory.getLogger(NodeBasedElasticSearchClientFactory.class);

    private final Node node;
    private final Client client;
    private final String defaultConfigFileName;
    private final String configFileName;


    public NodeBasedElasticSearchClientFactory(Configuration configuration,
                                               String defaultConfigFileName,
                                               String configFileName) {
        this(ImmutableSettings.EMPTY,configuration,defaultConfigFileName,configFileName);
    }

    public NodeBasedElasticSearchClientFactory(Settings defaultSettings,
                                               Configuration configuration,
                                               String defaultConfigFileName,
                                               String configFileName) {
        this.defaultConfigFileName = defaultConfigFileName;
        this.configFileName = configFileName;
        this.node = createClient(defaultSettings,configuration);
        this.client = node.client();
    }

    public NodeBasedElasticSearchClientFactory(Configuration configuration) {
        this(ImmutableSettings.EMPTY,configuration);
    }

    public NodeBasedElasticSearchClientFactory(Settings defaultSettings,Configuration configuration) {
        this(defaultSettings,configuration,configuration.getElasticSearchClientDefaultSettingFileName(),
                configuration.getElasticSearchClientOverrideSettingFileName());
    }


    private Node createClient(Settings defaultSettings,Configuration config) {

        NodeBuilder builder =  nodeBuilder().clusterName(config.getStorageClusterName()).data(false).client(true);

        // load the pre-packaged defaults
        builder.getSettings().loadFromClasspath(defaultConfigFileName);

        // Set the default cluster name from the configuration, then possibility to override
        // from the given settings object.  Then once again the possibility to override from
        // the xml filenames
        builder.settings(defaultSettings);

        // load the overrides on the classpath
        builder.getSettings().loadFromClasspath(configFileName);


        return builder.node();

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

        try {
            node.close();
        } catch(Exception e) {
            log.warn("Unable to shut down the ElasticSearch client node");
        }
    }
}
