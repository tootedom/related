package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import com.lmax.disruptor.EventHandler;
import org.greencheek.relatedproduct.domain.RelatedProductSearchRequest;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 21:26
 * To change this template use File | Settings | File Templates.
 */
public interface RelatedContentSearchRequestProcessorHandler extends EventHandler<RelatedProductSearchRequest> {
}
