package org.greencheek.relatedproduct.searching.disruptor.searchexecution;

import com.lmax.disruptor.EventHandler;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;

public interface RelatedProductSearchDisruptorEventHandler extends EventHandler<RelatedProductSearch> {
}