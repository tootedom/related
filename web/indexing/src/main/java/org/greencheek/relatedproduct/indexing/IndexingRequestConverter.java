package org.greencheek.relatedproduct.indexing;

import com.lmax.disruptor.EventTranslator;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;

/**
 * Parses a json file (send on the http request) to a RelatedProductIndexMessage.
 * The json payload looks like:
 * <pre>
 * {
 *   "channel" : "uk",
 *   "site"    : "amazon",
 *   "products":[
 *               {
 *                "id"   : "10",
 *                "type" : "map"
 *               },
 *               {
 *                "id"   : "11",
 *                "type" : "compass"
 *               },
 *               {
 *                "id"   : "12",
 *                "type" : "torch"
 *               }
 *             ]
 *  }
 * </pre>
 */
public interface IndexingRequestConverter extends EventTranslator<RelatedProductIndexingMessage> {
    public void translateTo(RelatedProductIndexingMessage convertedTo,
                                                  long sequence);
}
