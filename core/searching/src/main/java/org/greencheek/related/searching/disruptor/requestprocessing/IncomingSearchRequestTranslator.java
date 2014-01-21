package org.greencheek.related.searching.disruptor.requestprocessing;

import com.lmax.disruptor.EventTranslatorThreeArg;
import org.greencheek.related.api.searching.RelatedItemSearchType;
import org.greencheek.related.searching.domain.RelatedItemSearchRequest;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;

import java.util.Map;

/**
 * Converts an incoming user search request into a RelatedItemSearchRequest object.
 */
public interface IncomingSearchRequestTranslator extends EventTranslatorThreeArg<RelatedItemSearchRequest,
        RelatedItemSearchType, Map<String,String>, SearchResponseContext[] > {

}
