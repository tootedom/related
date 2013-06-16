package org.greencheek.relatedproduct.elastic;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.Classes;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
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

    public static final String CONFIG_FILE="elasticsearch.yml";
    public static final String DEFAULT_CONFIG_FILE = "default-elasticsearch.yml";


    private final Node node;
    private final Client client;
    private final String defaultConfigFileName;
    private final String configFileName;

    public NodeBasedElasticSearchClientFactory(String defaultConfigFileName, String configFileName) {
        this.defaultConfigFileName = defaultConfigFileName;
        this.configFileName = configFileName;
        this.node = createClient();
        this.client = node.client();
//        this.node.start();
    }

    public NodeBasedElasticSearchClientFactory() {
        this(DEFAULT_CONFIG_FILE,CONFIG_FILE);
    }

    private Node createClient() {

       NodeBuilder builder =  nodeBuilder().data(false).client(true);
        if(shouldLoadDefaults()) {
            builder.getSettings().loadFromClasspath(defaultConfigFileName);
        } else {
            builder.getSettings().loadFromClasspath(configFileName);
        }

        return builder.node();

    }

    public boolean shouldLoadDefaults() {
        ClassLoader  classLoader = Classes.getDefaultClassLoader();

        InputStream is = classLoader.getResourceAsStream(configFileName);

        if (is == null) {
            return true;
        }

        try {
            is.close();
        } catch(IOException e) {

        }

        return false;
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
