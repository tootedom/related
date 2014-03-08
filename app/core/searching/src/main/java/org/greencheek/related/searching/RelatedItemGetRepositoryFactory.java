package org.greencheek.related.searching;

import org.greencheek.related.util.config.Configuration;

/**
 * Created by dominictootell on 05/03/2014.
 */
public interface RelatedItemGetRepositoryFactory {
    RelatedItemGetRepository createRelatedItemGetRepository(Configuration configuration);
}
