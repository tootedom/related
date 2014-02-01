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

import org.greencheek.related.api.RelatedItemAdditionalProperties;
import org.greencheek.related.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 19:33
 */
public class BasicRelatedItemIndexingMessageConverter implements RelatedItemIndexingMessageConverter {

    private final Configuration configuration;

    public BasicRelatedItemIndexingMessageConverter(Configuration configuration) {
        this.configuration = configuration;
    }

    public RelatedItem[] convertFrom(RelatedItemIndexingMessage message) {

        RelatedItemSet items = message.getRelatedItems();
        int numberOfRelatedItems = items.getNumberOfRelatedItems();

        RelatedItem[] relatedItems = new RelatedItem[numberOfRelatedItems];

        RelatedItemInfo[][] idLists = relatedIds(items.getListOfRelatedItemInfomation(), items.getNumberOfRelatedItems());

        int length = numberOfRelatedItems-1;
        for(int i =0;i<numberOfRelatedItems;i++) {
            RelatedItemInfo id = idLists[i][length];
            relatedItems[i] = createRelatedItem(message, id, idLists[i], message.getIndexingMessageProperties());
        }


        return relatedItems;

    }

    private RelatedItem createRelatedItem(RelatedItemIndexingMessage message, RelatedItemInfo info,
                                          RelatedItemInfo[] ids,
                                          RelatedItemAdditionalProperties indexProperties) {
        RelatedItemAdditionalProperties properties = new RelatedItemAdditionalProperties(info.getAdditionalProperties(),indexProperties);

        int relatedIdLength = ids.length-1;
        char[][] relatedIds = new char[relatedIdLength][];
        for(int i=0;i<relatedIdLength;i++) {
            relatedIds[i] = ids[i].getId().duplicate();
        }

        return new RelatedItem(info.getId().duplicate(),message.getUTCFormattedDate(),relatedIds,properties);
    }


    /**
     * Given a list of ids {"1","2","3","5"}
     *
     * The method returns list of ids, where each id, is returned
     * with a link to the other ids in the list that it is related to.
     *
     * for example:
     *
     * 1 -> 2, 3, 5
     * 2 -> 3, 5, 1
     * 3 -> 5, 1, 2
     * 5 -> 1, 2, 3
     *
     * The item that is related to the the other items, is the last element in the returned
     * array (X marks the spot below)
     *
     * i.e.
     *              X
     * [
     *   [ 2, 3, 5, 1 ],
     *   [ 3, 5, 1, 2 ],
     *   [ 5, 1, 2, 3 ],
     *   [ 1, 2, 3, 5 ]
     * ]
     *
     *
     *
     */
    public static RelatedItemInfo[][] relatedIds(RelatedItemInfo[] ids, int length) {
        int len = length;
        RelatedItemInfo[][] idSets = new RelatedItemInfo[len][len];
        for(int j = 0;j<len;j++) {
            idSets[0][j] = ids[j];
        }

        for(int i=1;i<len;i++) {
            int k=0;
            for(int j=i;j<len;j++) {
                idSets[i][k++] = ids[j];
            }
            for(int j=0;j<i;j++) {
                idSets[i][k++] = ids[j];
            }
        }

        return idSets;
    }

}
