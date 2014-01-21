package org.greencheek.related.indexing;

import org.greencheek.related.api.indexing.RelatedItem;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 22:14
 * To change this template use File | Settings | File Templates.
 */
public interface RelatedItemStorageRepository {
    // Must not be asynchronous, The input list will not be a copy, but a passed reference
    public void store(RelatedItemStorageLocationMapper indexToMapper,  List<RelatedItem> relatedItems);
    public void shutdown();
}
