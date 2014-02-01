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

package org.greencheek.related.searching.executor;

import org.greencheek.related.searching.RelatedItemSearchExecutor;
import org.greencheek.related.searching.RelatedItemSearchExecutorFactory;
import org.greencheek.related.searching.RelatedItemSearchResultsToResponseGateway;
import org.greencheek.related.searching.disruptor.searchexecution.DisruptorBasedRelatedItemSearchExecutor;
import org.greencheek.related.searching.disruptor.searchexecution.RelatedItemSearchEventHandler;
import org.greencheek.related.searching.web.bootstrap.ApplicationCtx;
import org.greencheek.related.util.config.Configuration;

/**
 * Created by dominictootell on 18/01/2014.
 */
public class SearchExecutorFactory implements RelatedItemSearchExecutorFactory {

    private final ApplicationCtx applicationCtx;

    public SearchExecutorFactory (ApplicationCtx ctx) {
        applicationCtx = ctx;
    }

    @Override
    public RelatedItemSearchExecutor createSearchExecutor(RelatedItemSearchResultsToResponseGateway gateway) {
        Configuration config = applicationCtx.getConfiguration();
        return new DisruptorBasedRelatedItemSearchExecutor(config,applicationCtx.createRelatedItemSearchEventFactory(),
                new RelatedItemSearchEventHandler(config,applicationCtx.createSearchRepository(),gateway));
    }
}
