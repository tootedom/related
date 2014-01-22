package org.greencheek.related.api.searching;

import com.lmax.disruptor.EventFactory;

import java.util.Map;

/**
 *
 */
public interface RelatedItemSearchFactory extends EventFactory<RelatedItemSearch> {

    /**
     * Creates an empty RelatedItemSearch object, for use with the disruptor, when it creates
     * up front the domain objects for the ring buffer.
     * @return
     */
    public RelatedItemSearch createSearchObject();


    /**
     * Given a RelatedItemSearch object, it populates that object with the passed type information,
     * and the key,value pair properties that are passed.
     *
     * These properties represent the user's search criteria
     * for performing a lookup for frequently related products.
     *
     * @param objectToPopulate The RelatedItemSearch object that is to be populated from the given properties
     * @param type             The type of search that is being performed
     * @param properties       The key/value pairs for the search criteria (product id to find, extra filter parameters)
     */
    public void populateSearchObject(RelatedItemSearch objectToPopulate,
                                     RelatedItemSearchType type,
                                     Map<String, String> properties);

}
