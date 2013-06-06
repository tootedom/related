package org.greencheek.relatedproduct.indexing.disruptor;

import com.lmax.disruptor.EventTranslator;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.greencheek.relatedproduct.api.indexing.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverter;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverterFactory;
import org.greencheek.relatedproduct.indexing.InvalidIndexingRequestException;
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
public class RelatedProductIndexingRequestHandler implements EventTranslator<RelatedProductIndexingMessage> {

    private IndexingRequestConverter requestConverter;
    private final Configuration config;

    public RelatedProductIndexingRequestHandler(Configuration configuration, IndexingRequestConverter converter) {
        this.config = configuration;
        this.requestConverter = converter;
    }


    @Override
    public void translateTo(RelatedProductIndexingMessage event, long sequence) {

        event.validMessage.set(true);
        try {
            requestConverter.convertRequestIntoIndexingMessage(event,config.getMaxNumberOfRelatedProductProperties());
        } catch (InvalidIndexingRequestException e) {
            event.validMessage.set(false);
        }


    }
}
