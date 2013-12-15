package org.greencheek.relatedproduct.searching.disruptor.responseprocessing;

import com.lmax.disruptor.EventHandler;
import org.greencheek.relatedproduct.searching.domain.api.ResponseEvent;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 15/12/2013
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 */
public interface ResponseEventHandler extends EventHandler<ResponseEvent> {
}
