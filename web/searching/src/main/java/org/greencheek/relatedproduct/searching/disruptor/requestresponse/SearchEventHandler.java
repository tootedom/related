package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import com.lmax.disruptor.EventHandler;
import org.greencheek.relatedproduct.searching.domain.api.SearchResponseEvent;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 16/06/2013
 * Time: 08:06
 * To change this template use File | Settings | File Templates.
 */
public interface SearchEventHandler extends EventHandler<SearchResponseEvent> {
    public void shutdown();
}
