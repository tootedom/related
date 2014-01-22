package org.greencheek.related.api.searching;

import org.greencheek.related.api.searching.lookup.RelatedItemSearchLookupKeyGenerator;
import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.related.util.config.Configuration;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 13/10/2013
 * Time: 15:24
 * To change this template use File | Settings | File Templates.
 */
public abstract class RelatedItemSearchLookupKeyGeneratorTest {

    public abstract RelatedItemSearchLookupKeyGenerator getGenerator();
    public abstract Configuration getConfiguration();

    private RelatedItemSearchLookupKeyGenerator generator;
    private Configuration configuration;

    @Before
    public void setUp() {
        generator = getGenerator();
        configuration = getConfiguration();
    }


    public RelatedItemSearch getSearchObject() {
        RelatedItemSearch search = new RelatedItemSearch(configuration);
        search.getAdditionalSearchCriteria().addProperty("type","socks");
        search.getAdditionalSearchCriteria().addProperty("domain","com");
        search.getAdditionalSearchCriteria().addProperty("category","clothing");
        search.setMaxResults(2);
        search.setRelatedItemId("12345");
        search.setRelatedItemSearchType(RelatedItemSearchType.FREQUENTLY_RELATED_WITH);

        return search;

    }

    @Test
    public void testGenerateSearchLookupKey() {
        RelatedItemSearch search = getSearchObject();

        SearchRequestLookupKey key = generator.createSearchRequestLookupKeyFor(search);
        assertNotNull(key);

        assertNotNull(key.toString());

        assertNull(search.getLookupKey());
    }

    @Test
    public void testPopulateSearchLookupKey() {
        RelatedItemSearch search = getSearchObject();

        generator.setSearchRequestLookupKeyOn(search);
        assertNotNull(search.getLookupKey());

        assertNotNull(search.getLookupKey().toString());
    }

    @Test
    public void testGeneratedKeyIsSameAsPopulatedKey() {
        RelatedItemSearch search = getSearchObject();

        SearchRequestLookupKey key = generator.createSearchRequestLookupKeyFor(search);
        generator.setSearchRequestLookupKeyOn(search);
        assertTrue(search.getLookupKey().equals(key));
    }
}
