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

package org.greencheek.related.indexing.locationmappers;

import org.greencheek.related.api.indexing.RelatedItem;
import org.greencheek.related.indexing.RelatedItemStorageLocationMapper;
import org.greencheek.related.indexing.util.UTCCurrentDateAndHourAndMinuteFormatter;
import org.greencheek.related.util.config.Configuration;

/**
 *  Creates an index name based on the current hour : [indexnameprefix]-yyyy-MM-dd_HH:mm
 */
public class MinuteBasedStorageLocationMapper implements RelatedItemStorageLocationMapper {

    private final String indexPrefixName;
    private final UTCCurrentDateAndHourAndMinuteFormatter currentDayFormatter;
    private final int indexNameSize;

    public MinuteBasedStorageLocationMapper(Configuration configuration,
                                            UTCCurrentDateAndHourAndMinuteFormatter dateFormatter) {
        String s = configuration.getStorageIndexNamePrefix();
        this.indexPrefixName = s.endsWith("-") ? s : s + "-";
        currentDayFormatter = dateFormatter;

        indexNameSize = indexPrefixName.length() + 16;

    }

    @Override
    public String getLocationName(RelatedItem product) {
        String dateStr = product.getDate();
        String date;
        if(dateStr==null) {
            date = currentDayFormatter.getCurrentDayAndHourAndMinute();
        } else {
            date = currentDayFormatter.parseToDateAndHourAndMinute(dateStr);
        }

        StringBuilder indexName = new StringBuilder(indexNameSize);
        return indexName.append(this.indexPrefixName).append(date).toString();
    }
}
