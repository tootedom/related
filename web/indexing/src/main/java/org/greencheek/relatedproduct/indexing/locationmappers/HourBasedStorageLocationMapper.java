package org.greencheek.relatedproduct.indexing.locationmappers;

import org.greencheek.relatedproduct.searching.domain.RelatedProduct;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.util.UTCCurrentDateAndHourFormatter;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 22/06/2013
 * Time: 12:37
 * To change this template use File | Settings | File Templates.
 */
public class HourBasedStorageLocationMapper implements RelatedProductStorageLocationMapper {

    private final String indexPrefixName;
    private final UTCCurrentDateAndHourFormatter currentDayFormatter;
    private final int indexNameSize;

    public HourBasedStorageLocationMapper(Configuration configuration,
                                          UTCCurrentDateAndHourFormatter dateFormatter) {
        String s = configuration.getStorageIndexNamePrefix();
        this.indexPrefixName = s.endsWith("-") ? s : s + "-";
        currentDayFormatter = dateFormatter;

        indexNameSize = indexPrefixName.length() + 10;

    }

    @Override
    public String getLocationName(RelatedProduct product) {
        StringBuilder indexName = new StringBuilder(indexNameSize);
        return indexName.append(this.indexPrefixName).append(currentDayFormatter.parseToDateAndHour(product.getDate())).toString();
    }
}
