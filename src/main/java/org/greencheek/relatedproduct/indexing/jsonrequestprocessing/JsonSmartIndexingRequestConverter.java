package org.greencheek.relatedproduct.indexing.jsonrequestprocessing;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.greencheek.relatedproduct.api.indexing.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverter;
import org.greencheek.relatedproduct.indexing.InvalidIndexingRequestException;
import org.greencheek.relatedproduct.indexing.InvalidRelatedProductJsonException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 03/06/2013
 * Time: 20:34
 * To change this template use File | Settings | File Templates.
 */
public class JsonSmartIndexingRequestConverter implements IndexingRequestConverter {

    private static String PRODUCT_KEY = "products";
    private static String DATE_KEY = "date";
    private static String ID_KEY = "id";

    private static Map<String,Boolean> PROPERTY_LOOKUP = new HashMap<String,Boolean>(3);
    static {
        PROPERTY_LOOKUP.put(PRODUCT_KEY,Boolean.TRUE);
        PROPERTY_LOOKUP.put(DATE_KEY,Boolean.TRUE);
        PROPERTY_LOOKUP.put(ID_KEY,Boolean.TRUE);
    }


    private final JSONObject object;

    public JsonSmartIndexingRequestConverter(byte[] requestData) {
        JSONParser parser = new JSONParser(JSONParser.MODE_RFC4627);
        try
        {
            object = (JSONObject) parser.parse(requestData);
        }
        catch (Exception e)
        {
            throw new InvalidIndexingRequestException(e);
        }

        if(!object.containsKey(PRODUCT_KEY)) {
            throw new InvalidIndexingRequestException("No products in request");
        }

    }

    @Override
    public void convertRequestIntoIndexingMessage(RelatedProductIndexingMessage convertedTo,
                                                  short maxNumberOfAdditionalProperties) {


        convertedTo.validMessage.set(true);
        convertedTo.purchaseDate.set((String) object.get(DATE_KEY));
        Object products = object.get(PRODUCT_KEY);
        parseProductArray(convertedTo,products,maxNumberOfAdditionalProperties);
        parseAdditionalProperties(convertedTo.additionalProperties,object,maxNumberOfAdditionalProperties);

    }

    private void parseAdditionalProperties(RelatedProductAdditionalProperties properties,JSONObject map, short maxPropertiesThanCanBeRead) {

        Set<String> additionalPropertiesSet = map.keySet();
        String[] additionalProperties = additionalPropertiesSet.toArray(new String[additionalPropertiesSet.size()]);
        int minNumberOfAdditionalProperties = Math.min(maxPropertiesThanCanBeRead, additionalProperties.length);

        int i=0;
        int safeNumberOfProperties = minNumberOfAdditionalProperties;
        while(i<minNumberOfAdditionalProperties) {
            String key = additionalProperties[i];
            if(PROPERTY_LOOKUP.containsKey(key)) {
                i++;
                continue;
            }
            Object value = map.get(key);
            if(value instanceof String) {
                properties.additionalProperties[i].name.set(key);
                properties.additionalProperties[i].value.set((String)value);
            } else {
                safeNumberOfProperties--;
            }
            i++;
        }

        properties.numberOfProperties.set((short)safeNumberOfProperties);
    }

    private void parseProductArray(RelatedProductIndexingMessage event, Object products,
                                   short maxNumberOfAdditionalProperties) {

        if(products instanceof JSONArray) {
            Object[] productIdsArray = ((JSONArray)products).toArray();
            int i = 0;
            for(Object product : productIdsArray) {
                if(product instanceof JSONObject) {
                    JSONObject productObj = (JSONObject)product;
                    Object id = productObj.get(ID_KEY);
                    if(id instanceof String) {
                        event.relatedProducts.relatedProducts[i].id.set((String)id);
                    } else {
                        continue;
                    }

                    productObj.remove(ID_KEY);
                    parseAdditionalProperties(event.relatedProducts.relatedProducts[i].additionalProperties,productObj,
                            maxNumberOfAdditionalProperties);


                } else {
                    try {
                        event.relatedProducts.relatedProducts[i].id.set((String)product);
                    } catch(ClassCastException exception) {
                        continue;
                    }
                }
                i++;
            }

            if(i==0) {
                invalidateMessage(event);
            } else {
                event.relatedProducts.numberOfRelatedProducts.set((short)i);
            }
        } else {
            invalidateMessage(event);
        }
    }

    private void invalidateMessage(RelatedProductIndexingMessage message) {
        message.validMessage.set(false);
        message.relatedProducts.numberOfRelatedProducts.set((short)0);
    }
}
