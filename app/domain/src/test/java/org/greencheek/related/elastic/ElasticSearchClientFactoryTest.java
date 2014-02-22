/*
 *
 *  * Licensed to Relateit under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Relateit licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.greencheek.related.elastic;

import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.client.Client;
import org.greencheek.related.elastic.util.ElasticSearchServer;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 16/06/2013
 * Time: 23:12
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchClientFactoryTest {


    ElasticSearchServer node1;
    ElasticSearchServer node2;

    private Configuration configuration;
    private ElasticSearchClientFactory clientFactory;


    @Before
    public void setUp() {
        node1 = new ElasticSearchServer("default-name",false,false);
        node2 = new ElasticSearchServer("custom-name",false,false);
        configuration = new SystemPropertiesConfiguration();
    }
    @After
    public void tearDown() {
        node1.shutdown();
        node2.shutdown();
        if(clientFactory!=null) clientFactory.shutdown();
    }

    @Test
    public void testConnectToDefaultElasticNode() {
        clientFactory = new NodeBasedElasticSearchClientFactory(configuration);
        Client client = clientFactory.getClient();
        assertEquals("default-name",getClusterName(client));
    }

    @Test
    public void testOverridesStandardConfiguration() {
        clientFactory = new NodeBasedElasticSearchClientFactory(configuration,"default-elasticsearch.yml","elastic.yml");
        Client client = clientFactory.getClient();
        assertEquals("custom-name",getClusterName(client));
    }


    private String getClusterName(Client client) {

        NodesInfoResponse response = client.admin().cluster().nodesInfo(new NodesInfoRequest()).actionGet();
        return response.getClusterName().value();
    }

}
