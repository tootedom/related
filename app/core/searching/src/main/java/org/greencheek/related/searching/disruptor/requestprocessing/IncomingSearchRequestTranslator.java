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

import com.lmax.disruptor.EventTranslatorThreeArg;
import org.greencheek.related.api.searching.RelatedItemSearchType;
import org.greencheek.related.searching.domain.RelatedItemSearchRequest;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;

import java.util.Map;

/**
 * Converts an incoming user search request into a RelatedItemSearchRequest object.
 */
public interface IncomingSearchRequestTranslator extends EventTranslatorThreeArg<RelatedItemSearchRequest,
        RelatedItemSearchType, Map<String,String>, SearchResponseContext[] > {

}
