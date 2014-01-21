package org.greencheek.related.searching.disruptor.responseprocessing;

import com.lmax.disruptor.EventTranslatorTwoArg;
import org.greencheek.related.searching.domain.api.ResponseEvent;
import org.greencheek.related.searching.domain.api.SearchResultsEvent;
import org.greencheek.related.searching.requestprocessing.SearchResponseContextHolder;

import javax.servlet.AsyncContext;

/**
 * Converts a {@link SearchResultsEvent} into a ResponseEvent that is to be sent to the
 * users waiting on the {@link AsyncContext} objects.
 */
public interface SearchResponseEventTranslator extends EventTranslatorTwoArg<ResponseEvent,
        SearchResponseContextHolder[],SearchResultsEvent>  {
}
