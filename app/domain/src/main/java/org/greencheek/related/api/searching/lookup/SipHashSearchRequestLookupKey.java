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

package org.greencheek.related.api.searching.lookup;

import com.github.emboss.siphash.SipHash;
import com.github.emboss.siphash.SipKey;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 12/10/2013
 * Time: 12:09
 *
 * See:
 * https://devcentral.f5.com/weblogs/david/archive/2012/01/20/hashdos-ndash-the-post-of-doom-explained.aspx
 *
 * This is an implemention siphash that avoids the issues of the random hash function that is used
 * in the previous posts.  SipHash was defined to combat the issues with matching hash keys for different strings.
 * And as a result when used as a hash key, the distribution of the keys is wider and the chance of hash collisions
 * on strings is less.
 */
public class SipHashSearchRequestLookupKey implements SearchRequestLookupKey {

    private static final SipKey SPEC_KEY;

    static {
        Random r = new Random();
        byte[] key = new byte[16];
        r.nextBytes(key);
        SPEC_KEY = new SipKey(key);
    }

    private final int hash;
    private final String key;

    public SipHashSearchRequestLookupKey(String key) {
        this.key = key;
        try {
            hash = generateHashCode(key.getBytes("UTF-8"));
        } catch(UnsupportedEncodingException e) {
            throw new InstantiationError("UTF-8 not found");
        }
    }

    private int generateHashCode(byte[] data) {
        return (int)SipHash.digest(SPEC_KEY,data);
    }

    public boolean equals(Object o) {
        if(o == null) return false;
        if(o == this) return true;
        if(o instanceof SipHashSearchRequestLookupKey) {
            return ((SipHashSearchRequestLookupKey)o).key.equals(this.key);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return hash;
    }

    public String toString() {
        return key;
    }


}
