package org.greencheek.related.searching.disruptor.searchexecution;

import com.lmax.disruptor.EventHandler;
import org.greencheek.related.api.searching.RelatedItemSearch;

public interface RelatedItemSearchDisruptorEventHandler extends EventHandler<RelatedItemSearch> {
    public void shutdown();
}