package org.greencheek.related.indexing.locationmappers;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.greencheek.related.api.indexing.RelatedItem;
import org.greencheek.related.indexing.RelatedItemStorageLocationMapper;
import org.greencheek.related.indexing.util.UTCCurrentDateFormatter;
import org.greencheek.related.util.config.Configuration;

import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 22/06/2013
 * Time: 12:37
 * To change this template use File | Settings | File Templates.
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
            date = currentDayFormatter.parseToDate(product.getDate());
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
