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

package org.greencheek.related.indexing.requestprocessors.multi.disruptor;

import com.lmax.disruptor.EventHandler;
import org.greencheek.related.api.indexing.RelatedItemReference;

/**
 * for use by the {@link RoundRobinRelatedItemIndexingMessageEventHandler} for taking {@link org.greencheek.related.api.indexing.RelatedItemReference}
 * objects and storing them in the.  The RelateProductReference is used a container for RelatedItem objects
 * that are generated from an {@link org.greencheek.related.api.indexing.RelatedItemIndexingMessage}
 */
public interface RelatedItemReferenceEventHandler extends EventHandler<RelatedItemReference> {
    public void shutdown();
}
