package org.greencheek.relatedproduct.indexing.jsonrequestprocessing;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverter;
import org.greencheek.relatedproduct.indexing.InvalidIndexingRequestException;
import org.greencheek.relatedproduct.indexing.util.ISO8601UTCCurrentDateAndTimeFormatter;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 03/06/2013
 * Time: 20:34
 * To change this template use File | Settings | File Templates.
 */
public class JsonSmartIndexingRequestConverter implements IndexingRequestConverter {

    private static final Logger log = LoggerFactory.getLogger(JsonSmartIndexingRequestConverter.class);

    private final String productKey;
    private final String dateKey;
    private final String idKey;

    private final String date;
    private final Object[] products;

    private final JSONObject object;

    public JsonSmartIndexingRequestConverter(Configuration config, ISO8601UTCCurrentDateAndTimeFormatter dateCreator, ByteBuffer requestData) {
        JSONParser parser = new JSONParser(JSONParser.MODE_RFC4627);
        try
        {
            object = (JSONObject) parser.parse(new ByteBufferBackedInputStream(requestData));
        }
        catch (Exception e)
        {
            throw new InvalidIndexingRequestException(e);
        }

        productKey = config.getKeyForIndexRequestProductArrayAttr();
        dateKey = config.getKeyForIndexRequestDateAttr();
        idKey = config.getKeyForIndexRequestIdAttr();

        if(!object.containsKey(productKey)) {
            throw new InvalidIndexingRequestException("No products in request");
        }

        Object products = object.remove(productKey);
        if(!(products instanceof JSONArray)) {
            throw new InvalidIndexingRequestException("No parsable products in request.  Product list must be an array of related products");
        } else {
            this.products = ((JSONArray)products).toArray();
        }

        String date = (String)object.remove(dateKey);
        if(date==null) {
            this.date = dateCreator.getCurrentDay();
        } else {
            this.date = dateCreator.formatToUTC(date);
        }
    }

    @Override
    public void convertRequestIntoIndexingMessage(RelatedProductIndexingMessage convertedTo,
                                                  short maxNumberOfAdditionalProperties) {
        convertedTo.setValidMessage(true);
        convertedTo.setUTCFormattedDate(date);
        parseProductArray(convertedTo,maxNumberOfAdditionalProperties);
        parseAdditionalProperties(convertedTo.additionalProperties, object, maxNumberOfAdditionalProperties);
        log.debug("valid converted message?: {}",convertedTo.isValidMessage());
    }

    private void parseAdditionalProperties(RelatedProductAdditionalProperties properties,JSONObject map, short maxPropertiesThanCanBeRead) {
        int mapSize = map.size();
        if(mapSize==0) {
            properties.setNumberOfProperties((short)0);
            return;
        }

//        Set<String> additionalPropertiesSet = map.keySet();
//        String[] additionalProperties = additionalPropertiesSet.toArray(new String[additionalPropertiesSet.size()]);
        int minNumberOfAdditionalProperties = Math.min(maxPropertiesThanCanBeRead, mapSize);

        int i=0;
        int safeNumberOfProperties = minNumberOfAdditionalProperties;
//        while(i<minNumberOfAdditionalProperties) {

        for(String key : map.keySet()) {
            Object value = map.get(key);
            if(value instanceof String) {
                try {
                    properties.additionalProperties[i].setName(key);
                    properties.additionalProperties[i].setValue((String)value);
                } catch (Exception e) {
                    log.error("map: {}",map.toJSONString());
                    log.error("additional property: {}, {}",new Object[]{key,value,e});
                }
            } else {
                safeNumberOfProperties--;
            }
            if(++i==minNumberOfAdditionalProperties) break;
        }

        properties.setNumberOfProperties((short)safeNumberOfProperties);
    }

    private void parseProductArray(RelatedProductIndexingMessage event,
                                   short maxNumberOfAdditionalProperties) {

            int i = 0;
            for(Object product : this.products) {
                if(product instanceof JSONObject) {
                    JSONObject productObj = (JSONObject)product;
                    Object id = productObj.remove(idKey);
                    if(id instanceof String) {
                        event.relatedProducts.relatedProducts[i].id.setId((String)id);
                    } else {
                        productObj.put(idKey,id);
                        continue;
                    }

                    parseAdditionalProperties(event.relatedProducts.relatedProducts[i].additionalProperties,productObj,maxNumberOfAdditionalProperties);
                    productObj.put(idKey,id);

                } else {
                    try {
                        event.relatedProducts.relatedProducts[i].id.setId((String)product);
                    } catch(ClassCastException exception) {
                        continue;
                    }
                }
                i++;
            }

            if(i==0) {
                invalidateMessage(event);
            } else {
                event.relatedProducts.setNumberOfRelatedProducts((short)i);
            }

    }

    private void invalidateMessage(RelatedProductIndexingMessage message) {
        message.setValidMessage(false);
        message.relatedProducts.setNumberOfRelatedProducts((short)0);
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
