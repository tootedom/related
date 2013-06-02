package org.greencheek.relatedproduct.indexing.disruptor;

import com.lmax.disruptor.EventTranslator;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.greencheek.relatedproduct.api.indexing.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.indexing.InvalidRelatedProductJsonException;
import org.greencheek.relatedproduct.util.config.Configuration;

import java.util.Set;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductPurchaseFromJson implements EventTranslator<RelatedProductIndexingMessage> {

    private final JSONObject object;
    private final Configuration config;

    public RelatedProductPurchaseFromJson(byte[] data, Configuration configuration) {
        this.config = configuration;
        JSONParser parser = new JSONParser(JSONParser.MODE_RFC4627);
        try
        {
            object = (JSONObject) parser.parse(data);
        }
        catch (Exception e)
        {
            throw new InvalidRelatedProductJsonException(e);
        }
    }


    private void parseAdditionalProperties(RelatedProductAdditionalProperties properties,JSONObject map, short maxPropertiesThanCanBeRead) {

        Set<String> additionalPropertiesSet = map.keySet();
        String[] additionalProperties = additionalPropertiesSet.toArray(new String[additionalPropertiesSet.size()]);
        int minNumberOfAdditionalProperties = Math.min(maxPropertiesThanCanBeRead, additionalProperties.length);

        int i=0;
        int safeNumberOfProperties = minNumberOfAdditionalProperties;
        while(i<minNumberOfAdditionalProperties) {
            String key = additionalProperties[i];
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

    private void parseProductArray(RelatedProductIndexingMessage event, Object products) {

        if(products instanceof JSONArray) {
            Object[] productIdsArray = ((JSONArray)products).toArray();
            int i = 0;
            for(Object product : productIdsArray) {
                if(product instanceof JSONObject) {
                    JSONObject productObj = (JSONObject)product;
                    Object id = productObj.get("id");
                    if(id instanceof String) {
                        event.relatedProducts.relatedProducts[i].id.set((String)id);
                    } else {
                        continue;
                    }

                    productObj.remove("id");
                    parseAdditionalProperties(event.relatedProducts.relatedProducts[i].additionalProperties,productObj,
                            config.getMaxNumberOfRelatedProductProperties());


                } else {
                    try {
                        event.relatedProducts.relatedProducts[i].id.set((String)product);
                    } catch(ClassCastException exception) {
                        continue;
                    }
                }
                i++;
            }
            event.relatedProducts.numberOfRelatedProducts.set((short)i);
        } else {
            event.validMessage.set(false);
            event.relatedProducts.numberOfRelatedProducts.set((short)0);
        }
    }

    @Override
    public void translateTo(RelatedProductIndexingMessage event, long sequence) {

        event.validMessage.set(true);
        event.purchaseDate.set((String) object.remove("date"));
        Object products = object.remove("products");
        parseProductArray(event,products);

        parseAdditionalProperties(event.additionalProperties,object,config.getMaxNumberOfRelatedProductProperties());

    }
}
