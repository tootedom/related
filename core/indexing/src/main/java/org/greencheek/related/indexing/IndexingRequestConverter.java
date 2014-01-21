package org.greencheek.related.indexing;

import com.lmax.disruptor.EventTranslator;
import org.greencheek.related.api.indexing.RelatedItemIndexingMessage;

/**
 * Parses a json file (send on the http request) to a RelatedItemIndexMessage.
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
public interface IndexingRequestConverter extends EventTranslator<RelatedItemIndexingMessage> {
    public void translateTo(RelatedItemIndexingMessage convertedTo,
                                                  long sequence);
}
