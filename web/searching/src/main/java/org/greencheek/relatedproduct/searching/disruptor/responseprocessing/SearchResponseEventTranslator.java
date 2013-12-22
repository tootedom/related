package org.greencheek.relatedproduct.searching.disruptor.responseprocessing;

import com.lmax.disruptor.EventTranslatorTwoArg;
import org.greencheek.relatedproduct.searching.domain.api.ResponseEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;

import javax.servlet.AsyncContext;

/**
 * Converts a {@link SearchResultsEvent} into a ResponseEvent that is to be sent to the
 * users waiting on the {@link AsyncContext} objects.
 */
public interface SearchResponseEventTranslator extends EventTranslatorTwoArg<ResponseEvent,
        AsyncContext[],SearchResultsEvent>  {
}
