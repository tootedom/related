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

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.greencheek.related.api.RelatedItemAdditionalProperties;
import org.greencheek.related.api.indexing.RelatedItemIndexingMessage;
import org.greencheek.related.api.indexing.RelatedItemInfo;
import org.greencheek.related.indexing.*;
import org.greencheek.related.indexing.util.ISO8601UTCCurrentDateAndTimeFormatter;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Converts a byte[] (which is a json payload) into a {@link org.greencheek.related.api.indexing.RelatedItemIndexingMessage}
 * The constructor actually invokes the parsing of the json data.
 *
 * Once the translateTo method has been called there is NO going back.  The object has
 * to be thrown away.  If there's a failure to convert from a user supplied json object to
 * a internal {@link org.greencheek.related.api.indexing.RelatedItemIndexingMessage}, the chances are trying that parsing again,
 * will fail for the same reason.
 */
public class JsonSmartIndexingRequestConverter implements IndexingRequestConverter {

    private static final Logger log = LoggerFactory.getLogger(JsonSmartIndexingRequestConverter.class);

    private final int maxNumberOfAdditionalProperties;
    private final String productKey;
    private final String dateKey;
    private final String idKey;

    private final String date;
    private final Object[] products;

    private final JSONObject object;

    public JsonSmartIndexingRequestConverter(Configuration config, ISO8601UTCCurrentDateAndTimeFormatter dateCreator, ByteBuffer requestData) throws InvalidIndexingRequestException {
        this(config,dateCreator,requestData,config.getMaxNumberOfRelatedItemProperties(), config.getMaxNumberOfRelatedItemsPerItem());
    }

    public JsonSmartIndexingRequestConverter(Configuration config, ISO8601UTCCurrentDateAndTimeFormatter dateCreator, ByteBuffer requestData,
        int maxNumberOfAllowedProperties,int maxNumberOfRelatedItems) throws InvalidIndexingRequestException {

        this.maxNumberOfAdditionalProperties = maxNumberOfAllowedProperties;

        JSONParser parser = new JSONParser(JSONParser.MODE_RFC4627);
        try
        {
            object = (JSONObject) parser.parse(new ByteBufferBackedInputStream(requestData));
        }
        catch (Exception e)
        {
            throw new InvalidIndexingRequestParsingException(e);
        }

        productKey = config.getKeyForIndexRequestProductArrayAttr();
        dateKey = config.getKeyForIndexRequestDateAttr();
        idKey = config.getKeyForIndexRequestIdAttr();

        Object products = object.remove(productKey);
        if(products == null) {
            throw new InvalidIndexingRequestNoProductsFoundException("No products in request data");
        }

        if(!(products instanceof JSONArray)) {
            throw new InvalidIndexingRequestNoProductsFoundException("No parsable products in request.  Product list must be an array of related products");
        } else {
            Object[] relatedItems = ((JSONArray)products).toArray();

            if(relatedItems.length==0) {
                throw new InvalidIndexingRequestNoProductsFoundException("No products in request data");
            }

            if(relatedItems.length>maxNumberOfRelatedItems) {
                if(config.shouldDiscardIndexRequestWithTooManyRelations()) {
                    throw new InvalidIndexingRequestTooManyProductsFoundException("Too many related products in request.  Not Parsing.");
                }
                else {
                    log.warn("Too many related products in request.  Ignored later related prodcuts");
                }
            }
            int numberOfRelatedItems = Math.min(relatedItems.length,maxNumberOfRelatedItems);

            this.products = new Object[numberOfRelatedItems];
            for(int i=0;i<numberOfRelatedItems;i++) {
                this.products[i] = relatedItems[i];
            }
        }

        String date = (String)object.remove(dateKey);
        if(date==null) {
            this.date = dateCreator.getCurrentDay();
        } else {
            this.date = dateCreator.formatToUTC(date);
        }
    }

    @Override
    public void translateTo(RelatedItemIndexingMessage convertedTo,
                            long sequence) {
        convertedTo.setValidMessage(true);
        convertedTo.setUTCFormattedDate(date);
        // parses the product array
        parseProductArray(convertedTo,maxNumberOfAdditionalProperties);
        // parses the properties that were associated to the wrapper, as a result are common to all the related products
        // ie. the site one which they were purchased together
        parseAdditionalProperties(convertedTo.getIndexingMessageProperties(), object, maxNumberOfAdditionalProperties);
    }


    private void parseAdditionalProperties(RelatedItemAdditionalProperties properties,JSONObject map, int maxPropertiesThanCanBeRead) {
        int mapSize = map.size();
        if(mapSize==0) {
            properties.setNumberOfProperties(0);
            return;
        }

        int minNumberOfAdditionalProperties = Math.min(maxPropertiesThanCanBeRead, mapSize);

        int i=0;
        int safeNumberOfProperties = minNumberOfAdditionalProperties;

        for(String key : map.keySet()) {
            Object value = map.get(key);
            // currently only support string values.. future... more
            if(value instanceof String) {
                properties.setProperty(key,(String)value,i);
            } else {
                safeNumberOfProperties--;
            }
            if(++i==minNumberOfAdditionalProperties) break;
        }

        properties.setNumberOfProperties(safeNumberOfProperties);
    }

    private void parseProductArray(RelatedItemIndexingMessage event,
                                   int maxNumberOfAdditionalProperties) {


        int i = 0;
        RelatedItemInfo[] relatedItemInfos = event.getRelatedItems().getListOfRelatedItemInfomation();
        for (Object product : this.products) {
            if (product instanceof JSONObject) {
                JSONObject productObj = (JSONObject) product;
                Object id = productObj.remove(idKey);
                if (id instanceof String) {
                    relatedItemInfos[i].setId((String) id);
                    parseAdditionalProperties(relatedItemInfos[i].getAdditionalProperties(), productObj, maxNumberOfAdditionalProperties);
                } else {
                    continue;
                }
            } else {
                if(product instanceof String) {
                    relatedItemInfos[i].setId((String) product);
                } else {
                    continue;
                }
            }
            i++;
        }

        if (i == 0) {
            invalidateMessage(event);
        } else {
            event.getRelatedItems().setNumberOfRelatedItems(i);
        }

    }

    private void invalidateMessage(RelatedItemIndexingMessage message) {
        message.setValidMessage(false);
        message.getRelatedItems().setNumberOfRelatedItems(0);
    }

    class ByteBufferBackedInputStream extends InputStream {

        ByteBuffer buf;

        public ByteBufferBackedInputStream(ByteBuffer buf) {
            this.buf = buf;
        }

        public int read() throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }
            return buf.get() & 0xFF;
        }

        public int read(byte[] bytes, int off, int len)
                throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }

            len = Math.min(len, buf.remaining());
            buf.get(bytes, off, len);
            return len;
        }


    }
}
