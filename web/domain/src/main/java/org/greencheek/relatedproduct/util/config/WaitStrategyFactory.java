package org.greencheek.relatedproduct.util.config;

import com.lmax.disruptor.WaitStrategy;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 26/08/2013
 * Time: 20:08
 * To change this template use File | Settings | File Templates.
 */
public interface WaitStrategyFactory {
    WaitStrategy createWaitStrategy();
}
