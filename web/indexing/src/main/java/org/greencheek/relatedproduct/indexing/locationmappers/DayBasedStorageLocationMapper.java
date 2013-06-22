package org.greencheek.relatedproduct.indexing.locationmappers;

import org.greencheek.relatedproduct.domain.RelatedProduct;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageLocationMapper;
import org.greencheek.relatedproduct.util.UTCCurrentDateFormatter;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 22/06/2013
 * Time: 12:37
 * To change this template use File | Settings | File Templates.
 */
public class DayBasedStorageLocationMapper implements RelatedProductStorageLocationMapper {

    private final String indexPrefixName;
    private final UTCCurrentDateFormatter currentDayFormatter;
    private final int indexNameSize;

    public DayBasedStorageLocationMapper(Configuration configuration,
                                         UTCCurrentDateFormatter dateFormatter) {
        String s = configuration.getStorageIndexNamePrefix();
        this.indexPrefixName = s.endsWith("-") ? s : s + "-";
        currentDayFormatter = dateFormatter;

        indexNameSize = indexPrefixName.length() + 10;

    }

    @Override
    public String getLocationName(RelatedProduct product) {
        StringBuilder indexName = new StringBuilder(indexNameSize);
        return indexName.append(this.indexPrefixName).append(currentDayFormatter.parseToDate(product.getDate())).toString();
    }
}
