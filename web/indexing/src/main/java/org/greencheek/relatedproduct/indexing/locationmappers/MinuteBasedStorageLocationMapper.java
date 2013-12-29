package org.greencheek.relatedproduct.indexing.locationmappers;

import org.greencheek.relatedproduct.api.indexing.RelatedProduct;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.util.UTCCurrentDateAndHourAndMinuteFormatter;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 22/06/2013
 * Time: 12:37
 * To change this template use File | Settings | File Templates.
 */
public class MinuteBasedStorageLocationMapper implements RelatedProductStorageLocationMapper {

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
    public String getLocationName(RelatedProduct product) {
        StringBuilder indexName = new StringBuilder(indexNameSize);
        return indexName.append(this.indexPrefixName).append(currentDayFormatter.parseToDateAndHourAndMinute(product.getDate())).toString();
    }
}
