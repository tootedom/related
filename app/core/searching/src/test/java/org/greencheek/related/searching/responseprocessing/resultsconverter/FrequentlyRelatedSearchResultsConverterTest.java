package org.greencheek.related.searching.responseprocessing.resultsconverter;

import org.greencheek.related.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.related.api.searching.SearchResultsOutcome;
import org.greencheek.related.api.searching.lookup.SipHashSearchRequestLookupKey;
import org.greencheek.related.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.related.searching.domain.api.SearchResultsEvent;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 23/06/2013
 * Time: 15:11
 * To change this template use File | Settings | File Templates.
 */
public abstract class FrequentlyRelatedSearchResultsConverterTest {

    private SearchResultsConverter manyResultsConverter;
    private SearchResultsConverter emptyResultsConverter;
    private SearchResultsConverter oneResultConverter;
    private SearchResultsConverter nullResultsConverter;

    private String expectedContentType;
    public static final Configuration configuration = new SystemPropertiesConfiguration();
    private SearchResultsEvent manyResults;
    private SearchResultsEvent emptyResults;
    private SearchResultsEvent nullResults;
    private SearchResultsEvent oneResult;

    @Before
    public void setUp() {
        manyResults = buildManyFrequentlyRelatedSearchResults(4);
        emptyResults = buildManyFrequentlyRelatedSearchResults(0);
        nullResults = null;
        oneResult = buildManyFrequentlyRelatedSearchResults(1);


        manyResultsConverter = getConverter();

        emptyResultsConverter = getConverter();

        nullResultsConverter = getConverter();

        oneResultConverter = getConverter();

        expectedContentType = getExpectedContentType();

    }


    private SearchResultsEvent<FrequentlyRelatedSearchResult[]> buildManyFrequentlyRelatedSearchResults(int sizeOfResults) {

        FrequentlyRelatedSearchResult[] resultList = new FrequentlyRelatedSearchResult[sizeOfResults];
        for(int i = 0;i<sizeOfResults;i++) {
            FrequentlyRelatedSearchResult result = new FrequentlyRelatedSearchResult(UUID.randomUUID().toString(),i+10);
            resultList[i] = result;
        }
        return new SearchResultsEvent(SearchResultsOutcome.HAS_RESULTS,resultList);
    }

    public abstract SearchResultsConverter getConverter();
    public abstract String getExpectedContentType();


    @Test
    public void testContentType() {
        assertTrue(expectedContentType != null);
        assertEquals(expectedContentType, manyResultsConverter.contentType());
    }

    @Test
    public void testConversionOfMultipleResults() {
       testConversionOfResults(manyResultsConverter, manyResults);
    }

    @Test
    public void testConversionOfOneResults() {
        testConversionOfResults(oneResultConverter, oneResult);
    }

    @Test
    public void testConversionOfEmptyResults() {
        testForEmptyResults(emptyResultsConverter, emptyResults);
    }

    @Test
    public void testConversionOfNullResults() {
        testForEmptyResults(nullResultsConverter, nullResults);
    }

    private void testForEmptyResults(SearchResultsConverter converter, SearchResultsEvent results) {
        String s = converter.convertToString(new SearchResultEventWithSearchRequestKey(results,new SipHashSearchRequestLookupKey("1"),0,System.nanoTime()));
        System.out.println(s);

        assertTrue("results should contain '" + configuration.getKeyForFrequencyResultOverallResultsSize()+"'",s.contains("\""+configuration.getKeyForFrequencyResultOverallResultsSize()+"\""));
        assertTrue(s.contains("" + 0));
        assertTrue(s.contains("\"" + configuration.getKeyForFrequencyResults() +"\""));
        assertTrue(s.matches(".*\"results\"[^:]*:[^\\[]*\\[[^\\]]*[\\]].*"));


    }

    private void testConversionOfResults(SearchResultsConverter converter, SearchResultsEvent<FrequentlyRelatedSearchResult[]> results) {
        String s = converter.convertToString(new SearchResultEventWithSearchRequestKey(results,new SipHashSearchRequestLookupKey("1"),0,System.nanoTime()));

        assertTrue(s.contains("\"" + configuration.getKeyForFrequencyResults() +"\""));
        System.out.println(s);

        for(FrequentlyRelatedSearchResult res : results.getSearchResults()) {
            assertTrue(s.contains("\""+res.getRelatedItemId()+"\""));
            assertTrue(s.contains("\""+res.getFrequency()+"\""));
        }

        System.out.println(s);
        assertTrue(s.contains("" + results.getSearchResults().length));

        assertTrue("results should contain '" + configuration.getKeyForFrequencyResultOccurrence() +"'",s.contains("\""+configuration.getKeyForFrequencyResultOccurrence()+"\""));
        assertTrue("results should contain '" + configuration.getKeyForFrequencyResultOverallResultsSize()+"'",s.contains("\""+configuration.getKeyForFrequencyResultOverallResultsSize()+"\""));
        assertTrue("results should contain '" + configuration.getKeyForFrequencyResultId()+"'",s.contains("\""+configuration.getKeyForFrequencyResultId()+"\""));

    }
}
