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

package com.github.emboss.siphash;

/**
 * https://github.com/emboss/siphash-java
 * @author <a href="mailto:Martin.Bosslet@googlemail.com">Martin Bosslet</a>
 */
public class SipKey {
    private final byte[] key;
    private final long leftHalf;
    private final long rightHalf;
    
    public SipKey(byte[] key) {
        if (key == null || key.length != 16)
            throw new RuntimeException("SipHash key must be 16 bytes");
        this.key = key;
        leftHalf = createLeftHalf();
        rightHalf = createRightHalf();
    }
    
    long createLeftHalf() {
       return UnsignedInt64.binToIntOffset(key, 0); 
    }

    long getLeftHalf() {
        return leftHalf;
    }

    long getRightHalf() {
        return rightHalf;
    }

    long createRightHalf() {
        return UnsignedInt64.binToIntOffset(key, 8); 
    }
}
