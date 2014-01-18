package org.greencheek.relatedproduct.api.searching;

import org.greencheek.relatedproduct.api.searching.lookup.RelatedProductSearchLookupKeyGenerator;
import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.relatedproduct.util.config.Configuration;
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
public abstract class RelatedProductSearchLookupKeyGeneratorTest {

    public abstract RelatedProductSearchLookupKeyGenerator getGenerator();
    public abstract Configuration getConfiguration();

    private RelatedProductSearchLookupKeyGenerator generator;
    private Configuration configuration;

    @Before
    public void setUp() {
        generator = getGenerator();
        configuration = getConfiguration();
    }


    public RelatedProductSearch getSearchObject() {
        RelatedProductSearch search = new RelatedProductSearch(configuration);
        search.getAdditionalSearchCriteria().addProperty("type","socks");
        search.getAdditionalSearchCriteria().addProperty("domain","com");
        search.getAdditionalSearchCriteria().addProperty("category","clothing");
        search.setMaxResults(2);
        search.setRelatedContentId("12345");
        search.setRelatedProductSearchType(RelatedProductSearchType.FREQUENTLY_RELATED_WITH);
        search.setValidMessage(true);

        return search;

    }

    @Test
    public void testGenerateSearchLookupKey() {
        RelatedProductSearch search = getSearchObject();

        SearchRequestLookupKey key = generator.createSearchRequestLookupKeyFor(search);
        assertNotNull(key);

        assertNotNull(key.toString());

        assertNull(search.getLookupKey());
    }

    @Test
    public void testPopulateSearchLookupKey() {
        RelatedProductSearch search = getSearchObject();

        generator.setSearchRequestLookupKeyOn(search);
        assertNotNull(search.getLookupKey());

        assertNotNull(search.getLookupKey().toString());
    }

    @Test
    public void testGeneratedKeyIsSameAsPopulatedKey() {
        RelatedProductSearch search = getSearchObject();

        SearchRequestLookupKey key = generator.createSearchRequestLookupKeyFor(search);
        generator.setSearchRequestLookupKeyOn(search);
        assertTrue(search.getLookupKey().equals(key));
    }
}
