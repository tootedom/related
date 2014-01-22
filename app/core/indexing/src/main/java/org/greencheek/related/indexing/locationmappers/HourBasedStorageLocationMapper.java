package org.greencheek.related.indexing.locationmappers;

import org.greencheek.related.api.indexing.RelatedItem;
import org.greencheek.related.indexing.RelatedItemStorageLocationMapper;
import org.greencheek.related.indexing.util.UTCCurrentDateAndHourFormatter;
import org.greencheek.related.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 22/06/2013
 * Time: 12:37
 * To change this template use File | Settings | File Templates.
 */
public class HourBasedStorageLocationMapper implements RelatedItemStorageLocationMapper {

    private final String indexPrefixName;
    private final UTCCurrentDateAndHourFormatter currentDayFormatter;
    private final int indexNameSize;

    public HourBasedStorageLocationMapper(Configuration configuration,
                                          UTCCurrentDateAndHourFormatter dateFormatter) {
        String s = configuration.getStorageIndexNamePrefix();
        this.indexPrefixName = s.endsWith("-") ? s : s + "-";
        currentDayFormatter = dateFormatter;

        indexNameSize = indexPrefixName.length() + 13;

    }

    @Override
    public String getLocationName(RelatedItem product) {
        StringBuilder indexName = new StringBuilder(indexNameSize);
        return indexName.append(this.indexPrefixName).append(currentDayFormatter.parseToDateAndHour(product.getDate())).toString();
    }
}
