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

package org.greencheek.related.api;

import org.greencheek.related.util.config.Configuration;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 05/07/2013
 * Time: 20:14
 * To change this template use File | Settings | File Templates.
 */

public class RelatedItemInfoIdentifier {

    private final int maxStringIdLength;
    private final char[] id;
    private int length = 0;

    public RelatedItemInfoIdentifier(Configuration configuration) {
        maxStringIdLength = configuration.getRelatedItemIdLength();
        id = new char[maxStringIdLength];
    }

    public void setId(String id) {
        length = Math.min(id.length(),maxStringIdLength);
        id.getChars(0, length,this.id,0);
    }

    public char[] getIdCharArray() {
        return id;
    }

    public char[] duplicate() {
        char destination[] = new char[length];
        System.arraycopy(this.id,0,destination,0,length);
        return destination;
    }

    public String toString() {
        return new String(id,0,length);
    }


    private void setLength(int length) {
        this.length = length;
    }

    public void copyTo(RelatedItemInfoIdentifier destination) {
        System.arraycopy(this.id,0,destination.getIdCharArray(),0,length);
        destination.setLength(this.length);
    }
}