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

package org.greencheek.related.indexing.elasticsearch;

import org.greencheek.related.elastic.ElasticSearchClientFactory;
import org.greencheek.related.elastic.NodeBasedElasticSearchClientFactory;
import org.greencheek.related.elastic.TransportBasedElasticSearchClientFactory;
import org.greencheek.related.elastic.http.HttpElasticSearchClientFactory;
import org.greencheek.related.indexing.RelatedItemStorageRepository;
import org.greencheek.related.indexing.RelatedItemStorageRepositoryFactory;
import org.greencheek.related.util.config.Configuration;


/**
 * Creates a new elastic search storage repository (client connection to an elastic search cluster) when
 * {@link #getRepository(org.greencheek.related.util.config.Configuration)} is called.
 */
public class ElasticSearchRelatedItemStorageRepositoryFactory implements RelatedItemStorageRepositoryFactory {

    private final Configuration configuration;
    private final  HttpElasticSearchClientFactory httpFactory;

    public ElasticSearchRelatedItemStorageRepositoryFactory(Configuration configuration, HttpElasticSearchClientFactory httpFactory) {
        this.configuration = configuration;
        this.httpFactory = httpFactory;
    }

    @Override
    public RelatedItemStorageRepository getRepository(Configuration configuration) {
        RelatedItemStorageRepository storageRepository;
        ElasticSearchClientFactory factory = null;
        switch(configuration.getElasticSearchClientType()) {
            case HTTP:
                storageRepository = new ElasticSearchRelatedItemHttpIndexingRepository(configuration,httpFactory);
                break;
            case NODE:
                factory = new NodeBasedElasticSearchClientFactory(configuration);
                storageRepository = new ElasticSearchRelatedItemIndexingRepository(configuration,factory);
                break;
            case TRANSPORT:
                factory = new TransportBasedElasticSearchClientFactory(configuration);
            default:
                if(factory == null) {
                    factory = new TransportBasedElasticSearchClientFactory(configuration);
                }
                storageRepository = new ElasticSearchRelatedItemIndexingRepository(configuration,factory);
                break;
        }

        return storageRepository;
    }
}
