package org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter;

import org.greencheek.relatedproduct.domain.searching.FrequentlyRelatedSearchResult;
import org.greencheek.relatedproduct.domain.searching.FrequentlyRelatedSearchResults;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
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
    private FrequentlyRelatedSearchResults manyResults;
    private FrequentlyRelatedSearchResults emptyResults;
    private FrequentlyRelatedSearchResults nullResults;
    private FrequentlyRelatedSearchResults oneResult;

    @Before
    public void setUp() {
        manyResults = buildManyFrequentlyRelatedSearchResults(4);
        emptyResults = buildManyFrequentlyRelatedSearchResults(0);
        nullResults = null;
        oneResult = buildManyFrequentlyRelatedSearchResults(1);


        manyResultsConverter = getConverter(manyResults);

        emptyResultsConverter = getConverter(emptyResults);

        nullResultsConverter = getConverter(nullResults);

        oneResultConverter = getConverter(oneResult);

        expectedContentType = getExpectedContentType();

    }


    private FrequentlyRelatedSearchResults buildManyFrequentlyRelatedSearchResults(int sizeOfResults) {

        List< FrequentlyRelatedSearchResult > resultList = new ArrayList<FrequentlyRelatedSearchResult>(sizeOfResults);
        for(int i = 0;i<sizeOfResults;i++) {
            FrequentlyRelatedSearchResult result = new FrequentlyRelatedSearchResult(UUID.randomUUID().toString(),i+10);
            resultList.add(result);
        }
        return new FrequentlyRelatedSearchResults(resultList);
    }

    public abstract SearchResultsConverter getConverter(FrequentlyRelatedSearchResults results);
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
        testForEmptyResults(emptyResultsConverter);
    }

    @Test
    public void testConversionOfNullResults() {
        testForEmptyResults(nullResultsConverter);
    }

    private void testForEmptyResults(SearchResultsConverter converter) {
        String s = converter.convertToString();
        System.out.println(s);

        assertTrue("results should contain '" + configuration.getKeyForFrequencyResultOverallResultsSize()+"'",s.contains("\""+configuration.getKeyForFrequencyResultOverallResultsSize()+"\""));
        assertTrue(s.contains("" + 0));
        assertTrue(s.contains("\"" + configuration.getKeyForFrequencyResults() +"\""));
        assertTrue(s.matches(".*\"results\"[^:]*:[^\\[]*\\[[^\\]]*[\\]].*"));


    }

    private void testConversionOfResults(SearchResultsConverter converter, FrequentlyRelatedSearchResults results) {
        String s = converter.convertToString();

        assertTrue(s.contains("\"" + configuration.getKeyForFrequencyResults() +"\""));
        System.out.println(s);

        for(FrequentlyRelatedSearchResult res : results.getResults()) {
            assertTrue(s.contains("\""+res.getRelatedProductId()+"\""));
            assertTrue(s.contains("\""+res.getFrequency()+"\""));
        }

        System.out.println(s);
        assertTrue(s.contains("" + results.getResults().size()));

        assertTrue("results should contain '" + configuration.getKeyForFrequencyResultOccurrence() +"'",s.contains("\""+configuration.getKeyForFrequencyResultOccurrence()+"\""));
        assertTrue("results should contain '" + configuration.getKeyForFrequencyResultOverallResultsSize()+"'",s.contains("\""+configuration.getKeyForFrequencyResultOverallResultsSize()+"\""));
        assertTrue("results should contain '" + configuration.getKeyForFrequencyResultId()+"'",s.contains("\""+configuration.getKeyForFrequencyResultId()+"\""));

    }
}
