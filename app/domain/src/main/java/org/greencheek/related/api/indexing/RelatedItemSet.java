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

import org.greencheek.related.util.config.Configuration;

/**
 * Represents a group of {@link RelatedItemInfo} objects
 */
public class RelatedItemSet {
    private int numberOfRelatedItems;
    private final RelatedItemInfo[] relatedItems;


    public RelatedItemSet(Configuration configuration) {
        int num = configuration.getMaxNumberOfRelatedItemsPerItem();
        relatedItems = new RelatedItemInfo[num];
        for(int i=0;i<num;i++) {
            relatedItems[i] = new RelatedItemInfo(configuration);
        }
    }

    public void setNumberOfRelatedItems(int numberOfRelatedItems) {
        this.numberOfRelatedItems = numberOfRelatedItems;
    }

    public RelatedItemInfo[] getListOfRelatedItemInfomation() {
        return this.relatedItems;
    }

    /**
     * performs no bounds checking.
     *
     * @param index
     * @return
     */
    public RelatedItemInfo getRelatedItemAtIndex(int index) {
       return relatedItems[index];
    }

    /**
     * checks that the requested index is within the bounds of the currently
     * actively set number of related products
     *
     * @param index
     * @return
     */
    public RelatedItemInfo getCheckedRelatedItemAtIndex(int index) {
        return ( index < numberOfRelatedItems) ? relatedItems[index] : null;

    }

    public int getMaxNumberOfRelatedItems() {
        return this.relatedItems.length;
    }

    public int getNumberOfRelatedItems() {
        return this.numberOfRelatedItems;
    }


}
