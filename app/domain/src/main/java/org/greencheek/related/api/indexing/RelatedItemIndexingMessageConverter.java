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

package org.greencheek.related.api.indexing;

/**
 * Converts an {@link RelatedItemIndexingMessage} into an array of
 * {@link RelatedItem} objects.  Each returned RelatedItem
 * has a link to the ids of the other RelatedItem objects that are represented
 * in the {@link RelatedItemIndexingMessage} the method
 * {@link RelatedItem#getRelatedItemPids()} returns the
 * related product ids
 */
public interface RelatedItemIndexingMessageConverter {

    /**
     * There is no guarantee of the ordering of the RelatedItem array is in the
     * same order as {@link RelatedItemSet#relatedItems}
     *
     * @param message
     * @return an array of RelatedItems
     */
    public RelatedItem[] convertFrom(RelatedItemIndexingMessage message);
}
