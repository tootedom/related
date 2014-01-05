package org.greencheek.relatedproduct.searching.disruptor.responseprocessing;

import org.greencheek.relatedproduct.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.relatedproduct.api.searching.SearchResultsOutcome;
import org.greencheek.relatedproduct.searching.domain.api.ResponseEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;
import org.greencheek.relatedproduct.searching.responseprocessing.MapBasedSearchResponseContextHandlerLookup;
import org.greencheek.relatedproduct.searching.responseprocessing.SearchResponseContextHandlerLookup;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverterFactory;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by dominictootell on 04/01/2014.
 */
public class DisruptorBasedResponseEventHandlerTest {



    private final DisruptorBasedResponseEventHandler createResponseEventHandler(SearchResponseContextHandlerLookup responseContextHandler,
                                                                                SearchResultsConverterFactory factory) {
        return new DisruptorBasedResponseEventHandler(responseContextHandler,factory);
    }


    private ResponseEvent createResponse(String[] id, long[] frequency, SearchResponseContextHolder... holders) {

        FrequentlyRelatedSearchResult[] results = new FrequentlyRelatedSearchResult[id.length];
        for(int i =id.length;i!=0;i--) {
            results[i] = new FrequentlyRelatedSearchResult(id[i],frequency[i]);
        }

        ResponseEvent responseEvent = new ResponseEvent();
        responseEvent.setContexts(holders);
        responseEvent.setResults(new SearchResultsEvent(SearchResultsOutcome.HAS_RESULTS,results));

        return responseEvent;
    }

    @Test
    public void testHandleResponseEvent() throws Exception {
        Configuration config = new SystemPropertiesConfiguration();
        SearchResultsConverterFactory resultsConverterFactory =  mock(SearchResultsConverterFactory.class);
        when(resultsConverterFactory.getConverter(FrequentlyRelatedSearchResult[].class)).thenReturn(new NumberOfSearchResultsConverter("application/json"));
        DisruptorBasedResponseEventHandler eventHandler = createResponseEventHandler(new MapBasedSearchResponseContextHandlerLookup(config),resultsConverterFactory);


    }

    private class NumberOfSearchResultsConverter implements SearchResultsConverter<FrequentlyRelatedSearchResult[]> {

        private final String mediaType;

        public NumberOfSearchResultsConverter(String mediaType) {
            this.mediaType = mediaType;
        }

        @Override
        public String contentType() {
            return mediaType;
        }

        @Override
        public String convertToString(SearchResultsEvent<FrequentlyRelatedSearchResult[]> results) {
            return "{ \"num\": \""+Integer.toString(results.getSearchResults().length) +"\" }";
        }
    }
}
