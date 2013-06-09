package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import com.lmax.disruptor.EventHandler;
import org.greencheek.relatedproduct.domain.api.SearchEvent;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */
public interface SearchEventHandler extends EventHandler<SearchEvent> {
}
