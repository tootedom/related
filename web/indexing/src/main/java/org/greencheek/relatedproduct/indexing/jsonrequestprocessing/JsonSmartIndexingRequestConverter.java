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

    private final JSONObject object;

    public JsonSmartIndexingRequestConverter(Configuration config, ISO8601UTCCurrentDateAndTimeFormatter dateCreator, byte[] requestData) {
        JSONParser parser = new JSONParser(JSONParser.MODE_RFC4627);
        try
        {
            object = (JSONObject) parser.parse(requestData);
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

        String date = (String)object.get(dateKey);
        if(date==null) {
            object.put(dateKey,dateCreator.getCurrentDay());
        } else {
            object.put(dateKey,dateCreator.formatToUTC(date));
        }
    }

    @Override
    public void convertRequestIntoIndexingMessage(RelatedProductIndexingMessage convertedTo,
                                                  short maxNumberOfAdditionalProperties) {
        convertedTo.setValidMessage(true);
        convertedTo.setUTCFormattedDate((String) object.get(dateKey));
        Object products = object.get(productKey);
        parseProductArray(convertedTo,products,maxNumberOfAdditionalProperties);
        Object tmpProduct = object.remove(productKey);
        Object tmpDate = object.remove(dateKey);
        Object tmpId = object.remove(idKey);
        parseAdditionalProperties(convertedTo.additionalProperties, object, maxNumberOfAdditionalProperties);
        object.put(idKey,tmpId);
        object.put(dateKey,tmpDate);
        object.put(productKey, tmpProduct);
        log.debug("valid converted message?: {}",convertedTo.isValidMessage());
    }

    private void parseAdditionalProperties(RelatedProductAdditionalProperties properties,JSONObject map, short maxPropertiesThanCanBeRead) {

//        Set<String> additionalPropertiesSet = map.keySet();
//        String[] additionalProperties = additionalPropertiesSet.toArray(new String[additionalPropertiesSet.size()]);
        int minNumberOfAdditionalProperties = Math.min(maxPropertiesThanCanBeRead, map.size());

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
            i++;
            if(i>=minNumberOfAdditionalProperties) break;
        }

        properties.setNumberOfProperties((short)safeNumberOfProperties);
    }

    private void parseProductArray(RelatedProductIndexingMessage event, Object products,
                                   short maxNumberOfAdditionalProperties) {

        if(products instanceof JSONArray) {
            Object[] productIdsArray = ((JSONArray)products).toArray();
            int i = 0;
            for(Object product : productIdsArray) {
                if(product instanceof JSONObject) {
                    JSONObject productObj = (JSONObject)product;
                    Object id = productObj.get(idKey);
                    if(id instanceof String) {
                        event.relatedProducts.relatedProducts[i].id.setId((String)id);
                    } else {
                        continue;
                    }

                    Object idObj =productObj.remove(idKey);
                    parseAdditionalProperties(event.relatedProducts.relatedProducts[i].additionalProperties,productObj,
                            maxNumberOfAdditionalProperties);
                    productObj.put(idKey,idObj);


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
        } else {
            invalidateMessage(event);
        }
    }

    private void invalidateMessage(RelatedProductIndexingMessage message) {
        message.setValidMessage(false);
        message.relatedProducts.setNumberOfRelatedProducts((short)0);
    }
}
