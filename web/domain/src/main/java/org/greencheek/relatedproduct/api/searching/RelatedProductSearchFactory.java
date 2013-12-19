package org.greencheek.relatedproduct.api.searching;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.util.config.Configuration;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 13/10/2013
 * Time: 17:42
 * To change this template use File | Settings | File Templates.
 */
public interface RelatedProductSearchFactory extends EventFactory<RelatedProductSearch> {

    /**
     * Creates an empty RelatedProductSearch object, for use with the disruptor, when it creates
     * up front the domain objects for the ring buffer.
     * @return
     */
    public RelatedProductSearch createSearchObject();


    /**
     * Given a RelatedProductSearch object, it populates that object with the passed type information,
     * the key,value pair properties that are passed.  These properties represent the user's search criteria
     * for performing a lookup for frequently related products.
     *
     * @param objectToPopulate The RelatedProductSearch object that is to be populated from the given properties
     * @param type             The type of search that is being performed
     * @param properties       The key/value pairs for the search criteria (product id to find, extra filter parameters)
     */
    public void populateSearchObject(RelatedProductSearch objectToPopulate,
                                     RelatedProductSearchType type,
                                     Map<String, String> properties);

}
