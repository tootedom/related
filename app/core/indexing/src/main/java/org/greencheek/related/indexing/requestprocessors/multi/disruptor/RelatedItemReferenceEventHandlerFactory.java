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

/**
 * Responsible for creating a {@linke RelatedItemReferenceEventHandler} that can be used
 * for dealing with {@link org.greencheek.related.api.indexing.RelatedItemReference} objects,
 * and storing them appropriately
 */
public interface RelatedItemReferenceEventHandlerFactory {

    /**
     * Either creates a new handler or reuses a common handler.
     * The handler internally deals with the storage/manipulation of
     * {@link org.greencheek.related.api.indexing.RelatedItemReference} objects
     *
     * @return The handler
     */
    RelatedItemReferenceEventHandler getHandler();
}
