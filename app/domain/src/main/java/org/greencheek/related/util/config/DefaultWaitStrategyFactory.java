/*
 *
 *  * Licensed to Relateit under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Relateit licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

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
