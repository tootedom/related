package org.greencheek.relatedproduct.indexing.locationmappers;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import org.greencheek.relatedproduct.domain.RelatedProduct;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.util.UTCCurrentDateFormatter;
import org.greencheek.relatedproduct.util.config.Configuration;

import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 22/06/2013
 * Time: 12:37
 * To change this template use File | Settings | File Templates.
 */
public class DayBasedStorageLocationMapper implements RelatedProductStorageLocationMapper {

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
    public String getLocationName(RelatedProduct product) {
        StringBuilder indexName = new StringBuilder(indexNameSize);
        String date = currentDayFormatter.parseToDate(product.getDate());
        if(dateCachingEnabled) {
            String cachedIndexName = dayCache.get(date);
            if(cachedIndexName==null) {
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
            return indexName.append(this.indexPrefixName).append(date).toString();
        }
    }
}
