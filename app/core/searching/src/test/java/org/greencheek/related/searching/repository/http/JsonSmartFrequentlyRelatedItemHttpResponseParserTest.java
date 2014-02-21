package org.greencheek.related.searching.repository.http;

import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by dominictootell on 20/02/2014.
 */
public class JsonSmartFrequentlyRelatedItemHttpResponseParserTest {

    private static final String THREE_RESULTS_ONE_NO_RESULTS = "{\n" +
            "   \"responses\":[\n" +
            "      {\n" +
            "         \"took\":3,\n" +
            "         \"timed_out\":false,\n" +
            "         \"_shards\":{\n" +
            "            \"total\":5,\n" +
            "            \"successful\":5,\n" +
            "            \"failed\":0\n" +
            "         },\n" +
            "         \"hits\":{\n" +
            "            \"total\":497,\n" +
            "            \"max_score\":0.0,\n" +
            "            \"hits\":[\n" +
            "\n" +
            "            ]\n" +
            "         },\n" +
            "         \"facets\":{\n" +
            "            \"frequently-related-with\":{\n" +
            "               \"_type\":\"terms\",\n" +
            "               \"missing\":0,\n" +
            "               \"total\":497,\n" +
            "               \"other\":0,\n" +
            "               \"terms\":[\n" +
            "                  {\n" +
            "                     \"term\":\"4\",\n" +
            "                     \"count\":299\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"term\":\"15\",\n" +
            "                     \"count\":198\n" +
            "                  }\n" +
            "               ]\n" +
            "            }\n" +
            "         }\n" +
            "      },\n" +
            "      {\n" +
            "         \"took\":5,\n" +
            "         \"timed_out\":false,\n" +
            "         \"_shards\":{\n" +
            "            \"total\":5,\n" +
            "            \"successful\":5,\n" +
            "            \"failed\":0\n" +
            "         },\n" +
            "         \"hits\":{\n" +
            "            \"total\":2005,\n" +
            "            \"max_score\":0.0,\n" +
            "            \"hits\":[\n" +
            "\n" +
            "            ]\n" +
            "         },\n" +
            "         \"facets\":{\n" +
            "            \"frequently-related-with\":{\n" +
            "               \"_type\":\"terms\",\n" +
            "               \"missing\":0,\n" +
            "               \"total\":2005,\n" +
            "               \"other\":0,\n" +
            "               \"terms\":[\n" +
            "                  {\n" +
            "                     \"term\":\"1\",\n" +
            "                     \"count\":1002\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"term\":\"15\",\n" +
            "                     \"count\":1001\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"term\":\"3\",\n" +
            "                     \"count\":1\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"term\":\"2\",\n" +
            "                     \"count\":1\n" +
            "                  }\n" +
            "               ]\n" +
            "            }\n" +
            "         }\n" +
            "      },\n" +
            "      {\n" +
            "         \"took\":5,\n" +
            "         \"timed_out\":false,\n" +
            "         \"_shards\":{\n" +
            "            \"total\":5,\n" +
            "            \"successful\":5,\n" +
            "            \"failed\":0\n" +
            "         },\n" +
            "         \"hits\":{\n" +
            "            \"total\":0,\n" +
            "            \"max_score\":0.0,\n" +
            "            \"hits\":[\n" +
            "\n" +
            "            ]\n" +
            "         },\n" +
            "         \"facets\":{\n" +
            "            \"frequently-related-with\":{\n" +
            "               \"_type\":\"terms\",\n" +
            "               \"missing\":0,\n" +
            "               \"total\":0,\n" +
            "               \"other\":0,\n" +
            "               \"terms\":[\n" +
            "\n" +
            "               ]\n" +
            "            }\n" +
            "         }\n" +
            "      }\n" +
            "   ]\n" +
            "}";


    private static final String TWO_RESULTS_ONE_TIMED_OUT ="{\n" +
            "   \"responses\":[\n" +
            "      {\n" +
            "         \"took\":18,\n" +
            "         \"timed_out\":true,\n" +
            "         \"_shards\":{\n" +
            "            \"total\":5,\n" +
            "            \"successful\":5,\n" +
            "            \"failed\":0\n" +
            "         },\n" +
            "         \"hits\":{\n" +
            "            \"total\":886,\n" +
            "            \"max_score\":0.0,\n" +
            "            \"hits\":[\n" +
            "\n" +
            "            ]\n" +
            "         },\n" +
            "         \"facets\":{\n" +
            "            \"frequently-related-with\":{\n" +
            "               \"_type\":\"terms\",\n" +
            "               \"missing\":0,\n" +
            "               \"total\":886,\n" +
            "               \"other\":0,\n" +
            "               \"terms\":[\n" +
            "                  {\n" +
            "                     \"term\":\"4\",\n" +
            "                     \"count\":721\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"term\":\"15\",\n" +
            "                     \"count\":163\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"term\":\"3\",\n" +
            "                     \"count\":1\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"term\":\"2\",\n" +
            "                     \"count\":1\n" +
            "                  }\n" +
            "               ]\n" +
            "            }\n" +
            "         }\n" +
            "      },\n" +
            "      {\n" +
            "         \"took\":23,\n" +
            "         \"timed_out\":false,\n" +
            "         \"_shards\":{\n" +
            "            \"total\":5,\n" +
            "            \"successful\":5,\n" +
            "            \"failed\":0\n" +
            "         },\n" +
            "         \"hits\":{\n" +
            "            \"total\":2005,\n" +
            "            \"max_score\":0.0,\n" +
            "            \"hits\":[\n" +
            "\n" +
            "            ]\n" +
            "         },\n" +
            "         \"facets\":{\n" +
            "            \"frequently-related-with\":{\n" +
            "               \"_type\":\"terms\",\n" +
            "               \"missing\":0,\n" +
            "               \"total\":2005,\n" +
            "               \"other\":0,\n" +
            "               \"terms\":[\n" +
            "                  {\n" +
            "                     \"term\":\"1\",\n" +
            "                     \"count\":1002\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"term\":\"15\",\n" +
            "                     \"count\":1001\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"term\":\"3\",\n" +
            "                     \"count\":1\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"term\":\"2\",\n" +
            "                     \"count\":1\n" +
            "                  }\n" +
            "               ]\n" +
            "            }\n" +
            "         }\n" +
            "      }\n" +
            "   ]\n" +
            "}";


    private static final String TWO_RESULTS_ONE_WITH_ERROR = "{\n" +
            "   \"responses\":[\n" +
            "      {\n" +
            "         \"took\":14,\n" +
            "         \"timed_out\":false,\n" +
            "         \"_shards\":{\n" +
            "            \"total\":5,\n" +
            "            \"successful\":5,\n" +
            "            \"failed\":0\n" +
            "         },\n" +
            "         \"hits\":{\n" +
            "            \"total\":3,\n" +
            "            \"max_score\":0.0,\n" +
            "            \"hits\":[\n" +
            "\n" +
            "            ]\n" +
            "         },\n" +
            "         \"facets\":{\n" +
            "            \"frequently-related-with\":{\n" +
            "               \"_type\":\"terms\",\n" +
            "               \"missing\":0,\n" +
            "               \"total\":3,\n" +
            "               \"other\":0,\n" +
            "               \"terms\":[\n" +
            "                  {\n" +
            "                     \"term\":\"4\",\n" +
            "                     \"count\":1\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"term\":\"3\",\n" +
            "                     \"count\":1\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"term\":\"2\",\n" +
            "                     \"count\":1\n" +
            "                  }\n" +
            "               ]\n" +
            "            }\n" +
            "         }\n" +
            "      },\n" +
            "      {\n" +
            "         \"error\":\"IndexMissingException[[bob] missing]\"\n" +
            "      }\n" +
            "   ]\n" +
            "}";


    private static final String SINGLE_RESULT = "{\n" +
            "   \"responses\":[\n" +
            "      {\n" +
            "         \"took\":91,\n" +
            "         \"timed_out\":false,\n" +
            "         \"_shards\":{\n" +
            "            \"total\":5,\n" +
            "            \"successful\":5,\n" +
            "            \"failed\":0\n" +
            "         },\n" +
            "         \"hits\":{\n" +
            "            \"total\":1791,\n" +
            "            \"max_score\":0.0,\n" +
            "            \"hits\":[\n" +
            "\n" +
            "            ]\n" +
            "         },\n" +
            "         \"facets\":{\n" +
            "            \"frequently-related-with\":{\n" +
            "               \"_type\":\"terms\",\n" +
            "               \"missing\":0,\n" +
            "               \"total\":1791,\n" +
            "               \"other\":0,\n" +
            "               \"terms\":[\n" +
            "                  {\n" +
            "                     \"term\":\"4\",\n" +
            "                     \"count\":1002\n" +
            "                  }"+
            "               ]\n" +
            "            }\n" +
            "         }\n" +
            "      }\n" +
            "   ]\n" +
            "}";

    private static final String SINGLE_ERROR_RESULTS = "{\"responses\":[{\"error\":\"IndexMissingException[[testddddd] missing]\"}]}";

    private FrequentlyRelatedItemHttpResponseParser parser;

    @Before
    public void setUp() {
        parser = new JsonSmartFrequentlyRelatedItemHttpResponseParser(new SystemPropertiesConfiguration());
    }

    @Test
    public void testParseSingleError() throws Exception {
        FrequentlyRelatedItemSearchResponse[] responses = parser.parse(SINGLE_ERROR_RESULTS);

        assertEquals("Should only have one result",1,responses.length);
        assertTrue("Result should be an error", responses[0].hasErrored());
        assertEquals("error message not read from response","IndexMissingException[[testddddd] missing]",responses[0].getErrorMessage());
    }

    @Test
    public void testParseSingleResult() {
        FrequentlyRelatedItemSearchResponse[] responses = parser.parse(SINGLE_RESULT);

        assertEquals("Should only have one result",1,responses.length);
        assertFalse("Result should not be an error", responses[0].hasErrored());
        assertEquals("Result should have 1 terms",1,responses[0].getNumberOfFacets());

        assertEquals("Result should have 1 terms of '4'","4",responses[0].getFacetResult(0).name);
        assertEquals("Result should have 1 terms with count of '1002'",1002,responses[0].getFacetResult(0).count);
    }

    @Test
    public void testParseTwoResultsOneErrorOneOk() {
        FrequentlyRelatedItemSearchResponse[] responses = parser.parse(TWO_RESULTS_ONE_WITH_ERROR);

        assertEquals("Should only have two results",2,responses.length);
        assertEquals("Result should have 3 terms",3,responses[0].getNumberOfFacets());

        assertEquals("Result should have 3 terms with term 1 of '4'","4",responses[0].getFacetResult(0).name);
        assertEquals("Result should have 3 terms with term 1 of '4'","3",responses[0].getFacetResult(1).name);
        assertEquals("Result should have 3 terms with term 1 of '4'","2",responses[0].getFacetResult(2).name);
        assertEquals("Result should have 1st term with count of '1'",1,responses[0].getFacetResult(0).count);
        assertEquals("Result should have 2nd term with count of '1'",1,responses[0].getFacetResult(1).count);
        assertEquals("Result should have 3rd term with count of '1'",1,responses[0].getFacetResult(2).count);


        assertTrue("Result should not be an error", responses[1].hasErrored());
        assertEquals("Result should have 0 terms",0,responses[1].getNumberOfFacets());
        assertEquals("error message not read from response","IndexMissingException[[bob] missing]",responses[1].getErrorMessage());


    }

    @Test
    public void testParseTwoResultsWithOneTimedOut() {
        FrequentlyRelatedItemSearchResponse[] responses = parser.parse(TWO_RESULTS_ONE_TIMED_OUT);


        assertEquals("Should only have two results",2,responses.length);
        assertEquals("Result should have 4 terms",4,responses[0].getNumberOfFacets());

        assertEquals("Result should have 3 terms with term 1 of '4'","4",responses[0].getFacetResult(0).name);
        assertEquals("Result should have 3 terms with term 2 of '4'","15",responses[0].getFacetResult(1).name);
        assertEquals("Result should have 3 terms with term 3 of '4'","3",responses[0].getFacetResult(2).name);
        assertEquals("Result should have 3 terms with term 4 of '4'","2",responses[0].getFacetResult(3).name);


        assertEquals("Result should have 1st term with count of '1'",721,responses[0].getFacetResult(0).count);
        assertEquals("Result should have 2nd term with count of '1'",163,responses[0].getFacetResult(1).count);
        assertEquals("Result should have 3rd term with count of '1'",1,responses[0].getFacetResult(2).count);
        assertEquals("Result should have 3rd term with count of '1'",1,responses[0].getFacetResult(3).count);


        assertFalse("Result should not be an error", responses[0].hasErrored());
        assertTrue("Result should timed out", responses[0].hasTimedOut());

        assertFalse("Result should not be an error", responses[1].hasErrored());
        assertFalse("Result should timed out", responses[1].hasTimedOut());

        assertEquals("Result should have 3 terms with term 1 of '4'","1",responses[1].getFacetResult(0).name);
        assertEquals("Result should have 3 terms with term 2 of '4'","15",responses[1].getFacetResult(1).name);
        assertEquals("Result should have 3 terms with term 3 of '4'","3",responses[1].getFacetResult(2).name);
        assertEquals("Result should have 3 terms with term 4 of '4'","2",responses[1].getFacetResult(3).name);


        assertEquals("Result should have 1st term with count of '1'",1002,responses[1].getFacetResult(0).count);
        assertEquals("Result should have 2nd term with count of '1'",1001,responses[1].getFacetResult(1).count);
        assertEquals("Result should have 3rd term with count of '1'",1,responses[1].getFacetResult(2).count);
        assertEquals("Result should have 3rd term with count of '1'",1,responses[1].getFacetResult(3).count);


    }

    @Test
    public void testParseThreeResultsOneNoTerms() {
        FrequentlyRelatedItemSearchResponse[] responses = parser.parse(THREE_RESULTS_ONE_NO_RESULTS);


        assertEquals("Should only have 3 results",3,responses.length);
        assertEquals("Result should have 2 terms",2,responses[0].getNumberOfFacets());

        assertEquals("Result should have 3 terms with term 1 of '4'","4",responses[0].getFacetResult(0).name);
        assertEquals("Result should have 3 terms with term 2 of '4'","15",responses[0].getFacetResult(1).name);



        assertEquals("Result should have 1st term with count of '1'",299,responses[0].getFacetResult(0).count);
        assertEquals("Result should have 2nd term with count of '1'",198,responses[0].getFacetResult(1).count);


        assertFalse("Result should not be an error", responses[1].hasErrored());
        assertFalse("Result should not timed out", responses[1].hasTimedOut());

        assertEquals("Result should have 4 terms",4,responses[1].getNumberOfFacets());

        assertEquals("Result should have 3 terms with term 1 of '4'","1",responses[1].getFacetResult(0).name);
        assertEquals("Result should have 3 terms with term 2 of '4'","15",responses[1].getFacetResult(1).name);
        assertEquals("Result should have 3 terms with term 3 of '4'","3",responses[1].getFacetResult(2).name);
        assertEquals("Result should have 3 terms with term 4 of '4'","2",responses[1].getFacetResult(3).name);


        assertEquals("Result should have 1st term with count of '1'",1002,responses[1].getFacetResult(0).count);
        assertEquals("Result should have 2nd term with count of '1'",1001,responses[1].getFacetResult(1).count);
        assertEquals("Result should have 3rd term with count of '1'",1,responses[1].getFacetResult(2).count);
        assertEquals("Result should have 3rd term with count of '1'",1,responses[1].getFacetResult(3).count);


        assertEquals("Result should have 0 terms",0,responses[2].getNumberOfFacets());
        assertFalse("Result should not be an error", responses[2].hasErrored());
        assertFalse("Result should not timed out", responses[2].hasTimedOut());


    }


}
