package org.greencheek.related.util.config;

import com.lmax.disruptor.*;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 26/08/2013
 * Time: 20:11
 * To change this template use File | Settings | File Templates.
 */
public class DefaultWaitStrategyFactory implements WaitStrategyFactory {

    public enum WAIT_STRATEGY_TYPE {
        BLOCKING,
        SLEEPING,
        YIELDING,
        BUSY
    }

    public final WAIT_STRATEGY_TYPE waitStrategyType;

    public DefaultWaitStrategyFactory(WAIT_STRATEGY_TYPE type) {
        waitStrategyType = type;
    }

    @Override
    public WaitStrategy createWaitStrategy() {
        switch (waitStrategyType) {
            case BLOCKING:
                return new BlockingWaitStrategy();
            case SLEEPING:
                return new SleepingWaitStrategy();
            case YIELDING:
                return new YieldingWaitStrategy();
            case BUSY:
                return new BusySpinWaitStrategy();
            default:
                return new YieldingWaitStrategy();
        }
    }
}
