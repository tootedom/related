package org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter;

import org.greencheek.relatedproduct.api.searching.FrequentlyRelatedSearchResult;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

/**
 * Created by dominictootell on 05/01/2014.
 */
public class ExplicitSearchResultsConverterFactoryTest {
    @Test
    public void testGetConverter() throws Exception {
        SearchResultsConverter converter = mock(SearchResultsConverter.class);
        SearchResultsConverterFactory factory = new FrequentlyRelatedSearchResultsArrayConverterFactory(converter);

        SearchResultsConverter c = factory.getConverter(FrequentlyRelatedSearchResult[].class);

        assertNotNull(c);

        assertSame(c,converter);

        SearchResultsConverter con = factory.getConverter(String.class);

        assertNull(con);

    }


}
