package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import com.lmax.disruptor.EventTranslatorThreeArg;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;

import javax.servlet.AsyncContext;
import java.util.Map;

/**
 * Converts an incoming user search request into a RelatedProductSearchRequest object.
 */
public interface IncomingSearchRequestTranslator extends EventTranslatorThreeArg<RelatedProductSearchRequest,
        RelatedProductSearchType, Map<String,String>, SearchResponseContext[] > {

}
