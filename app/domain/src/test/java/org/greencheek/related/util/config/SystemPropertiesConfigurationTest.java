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

import junit.framework.TestCase;
import org.greencheek.related.util.arrayindexing.Util;
import org.junit.Test;

/**
 * Created by dominictootell on 25/01/2014.
 */
public class SystemPropertiesConfigurationTest extends TestCase {

    @Test
    public void testSearchHandlerSize() {
        System.setProperty(ConfigurationConstants.PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE, "32768");
        SystemPropertiesConfiguration config = new SystemPropertiesConfiguration();

        System.out.println(Util.ceilingNextPowerOfTwo(config.getSizeOfRelatedItemSearchRequestHandlerQueue()));
    }
}
