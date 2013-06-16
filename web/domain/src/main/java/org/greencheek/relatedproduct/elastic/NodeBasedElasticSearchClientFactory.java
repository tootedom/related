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

    public NodeBasedElasticSearchClientFactory() {
        this.node = createClient();
        this.client = node.client();
        this.node.start();
    }

    private Node createClient() {

       NodeBuilder builder =  nodeBuilder().client(true);
        if(shouldLoadDefaults()) {
            builder.getSettings().loadFromClasspath(DEFAULT_CONFIG_FILE);
        } else {
            builder.getSettings().loadFromClasspath(CONFIG_FILE);
        }

        return builder.build();

    }

    public boolean shouldLoadDefaults() {
        ClassLoader  classLoader = Classes.getDefaultClassLoader();

        InputStream is = classLoader.getResourceAsStream(CONFIG_FILE);

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
            node.stop();
        } catch(Exception e) {
            log.warn("Unable to shut down the ElasticSearch client node");
        }
    }
}
