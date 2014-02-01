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
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public class RelatedItemIndexingMessage {

    private boolean validMessage;
    private String dateUTC;

    private final RelatedItemSet relatedItems;
    private final RelatedItemAdditionalProperties additionalProperties;


    public RelatedItemIndexingMessage(Configuration config) {
        validMessage = false;
        relatedItems = new RelatedItemSet(config);
        additionalProperties = new RelatedItemAdditionalProperties(config,config.getMaxNumberOfRelatedItemProperties());

    }

    public void setValidMessage(boolean isValid) {
        this.validMessage = isValid;
    }

    public void setUTCFormattedDate(String date) {
        this.dateUTC = date;
    }

    /**
     * This only returns set has been set via {@link #setUTCFormattedDate}
     * @return
     */
    public String getUTCFormattedDate() {
        return this.dateUTC;
    }

    public boolean isValidMessage() {
        return this.validMessage;
    }

    public int getMaxNumberOfRelatedItemsAllowed() {
        return this.relatedItems.getMaxNumberOfRelatedItems();
    }

    public RelatedItemSet getRelatedItems() {
        return this.relatedItems;
    }

    public RelatedItemAdditionalProperties getIndexingMessageProperties() {
        return this.additionalProperties;
    }

}
