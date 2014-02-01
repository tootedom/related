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

package org.greencheek.related.indexing.requestprocessorfactory;

import org.greencheek.related.indexing.RelatedItemIndexRequestProcessor;
import org.greencheek.related.util.config.Configuration;

/**
 *
 */
public interface IndexRequestProcessorFactory {

    /**
     * Returns the processor that will take user requests, convert them to RelatedItem objects
     * and index/store them.
     *
     * @param configuration
     * @return
     */
    public RelatedItemIndexRequestProcessor createProcessor(Configuration configuration);

    /**
     * Shuts down the factory.
     */
    public void shutdown();
}
