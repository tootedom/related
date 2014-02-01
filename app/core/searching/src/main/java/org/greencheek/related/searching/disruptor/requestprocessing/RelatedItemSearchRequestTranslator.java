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

package org.greencheek.related.searching.disruptor.requestprocessing;

import org.greencheek.related.api.searching.RelatedItemSearchFactory;
import org.greencheek.related.api.searching.RelatedItemSearchType;
import org.greencheek.related.searching.domain.RelatedItemSearchRequest;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Implementation that converts an
 * incoming user search request into a RelatedItemSearchRequest object.
 */
public class RelatedItemSearchRequestTranslator implements IncomingSearchRequestTranslator {

    private static final Logger log = LoggerFactory.getLogger(RelatedItemSearchRequestTranslator.class);

    private final RelatedItemSearchFactory relatedItemSearchFactory;

    public RelatedItemSearchRequestTranslator(RelatedItemSearchFactory relatedItemSearchFactory) {
        this.relatedItemSearchFactory = relatedItemSearchFactory;
    }

    @Override
    public void translateTo(RelatedItemSearchRequest event, long sequence,
                            RelatedItemSearchType type, Map<String,String> params,
                            SearchResponseContext[] contexts) {
        log.debug("Creating Related Product Search Request {}, {}",event.getSearchRequest().getLookupKey(),params);
        event.setRequestContexts(contexts);

        relatedItemSearchFactory.populateSearchObject(event.getSearchRequest(), type,params);
    }
}
