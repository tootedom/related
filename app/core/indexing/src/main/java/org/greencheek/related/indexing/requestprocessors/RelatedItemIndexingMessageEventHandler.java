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

package org.greencheek.related.indexing.requestprocessors;

import com.lmax.disruptor.EventHandler;
import org.greencheek.related.api.indexing.RelatedItemIndexingMessage;

/**
 * Event handler for dealing with RelatedItemIndexingMessage events.
 * This means the request is either sent directly to the repository that deals with
 * the indexing request (i.e. saves it), or passes it on to a further ring buffer for batching
 * and storage further on down the line.
 */
public interface RelatedItemIndexingMessageEventHandler extends EventHandler<RelatedItemIndexingMessage> {

    public void shutdown();
}
