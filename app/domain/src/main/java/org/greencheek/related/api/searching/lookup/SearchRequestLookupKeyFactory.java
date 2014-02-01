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

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 12/10/2013
 * Time: 11:57
 *
 * Allow for future different implmentations of the SearchRequestLookupKey
 * by creating a Factory that creates the direct implementations.  So we can
 * swap in and out the implementations rather than having a direct dependency on
 * a particular implementation
 */
public interface SearchRequestLookupKeyFactory {

    /**
     * Give a normal string object, converts to a SearchRequestLookupKey
     *
     * @param key The string to wrap.
     * @return the SearchRequestLookupKey
     */
    SearchRequestLookupKey createSearchRequestLookupKey(String key);


}
