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

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.greencheek.related.api.indexing.RelatedItem;
import org.greencheek.related.indexing.RelatedItemStorageLocationMapper;
import org.greencheek.related.indexing.util.UTCCurrentDateFormatter;
import org.greencheek.related.util.config.Configuration;

import java.util.concurrent.ConcurrentMap;


/**
 * Creates an indexname that has [prefix]-yyyy-MM-dd
 */
public class DayBasedStorageLocationMapper implements RelatedItemStorageLocationMapper {

    private boolean dateCachingEnabled;
    private final String indexPrefixName;
    private final UTCCurrentDateFormatter currentDayFormatter;
    private final int indexNameSize;
    private final ConcurrentMap<String,String> dayCache;

    public DayBasedStorageLocationMapper(Configuration configuration,
                                         UTCCurrentDateFormatter dateFormatter) {
        String s = configuration.getStorageIndexNamePrefix();
        this.indexPrefixName = s.endsWith("-") ? s : s + "-";
        this.currentDayFormatter = dateFormatter;
        this.dateCachingEnabled = configuration.isIndexNameDateCachingEnabled();
        indexNameSize = indexPrefixName.length() + 10;

        if(this.dateCachingEnabled) {
            dayCache = new ConcurrentLinkedHashMap.Builder<String,String>().maximumWeightedCapacity(configuration.getNumberOfIndexNamesToCache()).build();
        } else {
            dayCache = null;
        }


    }

    @Override
    public String getLocationName(RelatedItem product) {
        String dateStr = product.getDate();
        String date;
        if(dateStr==null) {
            date = currentDayFormatter.getCurrentDay();
        } else {
            date = currentDayFormatter.parseToDate(dateStr);
        }

        if(dateCachingEnabled) {
            String cachedIndexName = dayCache.get(date);
            if(cachedIndexName==null) {
                StringBuilder indexName = new StringBuilder(indexNameSize);
                String theIndexName = indexName.append(this.indexPrefixName).append(date).toString();
                String previous = dayCache.putIfAbsent(date,theIndexName);
                if(previous!=null) {
                    return previous;
                } else {
                    return theIndexName;
                }
            } else {
                return cachedIndexName;
            }
        } else {
            StringBuilder indexName = new StringBuilder(indexNameSize);
            return indexName.append(this.indexPrefixName).append(date).toString();
        }
    }
}
