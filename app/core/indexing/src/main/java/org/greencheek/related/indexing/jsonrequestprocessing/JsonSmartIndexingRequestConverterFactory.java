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

package org.greencheek.related.indexing.jsonrequestprocessing;

import org.greencheek.related.indexing.IndexingRequestConverter;
import org.greencheek.related.indexing.IndexingRequestConverterFactory;
import org.greencheek.related.indexing.InvalidIndexingRequestException;
import org.greencheek.related.indexing.util.ISO8601UTCCurrentDateAndTimeFormatter;
import org.greencheek.related.util.config.Configuration;

import java.nio.ByteBuffer;

/**
 * Creates a Json Smart based IndexingRequestConverter, that will transform json into
 * RelatedItemIndexingMessage objects.
 */
public class JsonSmartIndexingRequestConverterFactory implements IndexingRequestConverterFactory {

    private final ISO8601UTCCurrentDateAndTimeFormatter dateCreator;

    public JsonSmartIndexingRequestConverterFactory(ISO8601UTCCurrentDateAndTimeFormatter formatter) {
        this.dateCreator = formatter;
    }

    @Override
    public IndexingRequestConverter createConverter(Configuration configuration, ByteBuffer convertFrom) throws InvalidIndexingRequestException
    {
        return new JsonSmartIndexingRequestConverter(configuration,dateCreator,convertFrom);
    }


}
