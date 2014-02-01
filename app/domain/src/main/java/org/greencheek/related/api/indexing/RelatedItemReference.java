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

package org.greencheek.related.api.indexing;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 27/08/2013
 * Time: 22:06
 * To change this template use File | Settings | File Templates.
 */
public class RelatedItemReference {

    public RelatedItem relatedItem;

    public void setReference(RelatedItem product) {
        this.relatedItem = product;
    }

    public RelatedItem getReference() {
        return this.relatedItem;
    }
}
