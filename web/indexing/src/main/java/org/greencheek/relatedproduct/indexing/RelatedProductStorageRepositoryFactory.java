package org.greencheek.relatedproduct.indexing;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 15/06/2013
 * Time: 12:32
 * To change this template use File | Settings | File Templates.
 */
public interface RelatedProductStorageRepositoryFactory {
    /**
     * Depending upon the implementation can create a new instance, or return the same instance.
     * @return
     */
    public RelatedProductStorageRepository getRepository();
}
