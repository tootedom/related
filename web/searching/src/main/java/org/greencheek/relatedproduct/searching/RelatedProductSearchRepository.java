package org.greencheek.relatedproduct.searching;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 00:45
 * To change this template use File | Settings | File Templates.
 */

// ES
public interface RelatedProductSearchRepository {
    public void findRelatedProducts(Configuration config, RelatedProductSearch[] searches, RelatedProductSearchRequestResponseProcessor handler);
}
