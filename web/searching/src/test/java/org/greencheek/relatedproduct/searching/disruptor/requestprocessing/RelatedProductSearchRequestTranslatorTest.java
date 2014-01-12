package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import org.greencheek.relatedproduct.api.searching.KeyFactoryBasedRelatedProductSearchLookupKeyGenerator;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.api.searching.lookup.RelatedProductSearchFactoryWithSearchLookupKeyFactory;
import org.greencheek.relatedproduct.api.searching.lookup.SipHashSearchRequestLookupKeyFactory;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.searching.requestprocessing.AsyncServletSearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.Test;

import javax.servlet.AsyncContext;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class RelatedProductSearchRequestTranslatorTest {


    @Test
    public void testTranslateTo() throws Exception {
        Configuration config = new SystemPropertiesConfiguration();

        RelatedProductSearchRequest request = new RelatedProductSearchRequest(config);
        Map<String,String> properties = new HashMap<String,String>();
        properties.put(config.getRequestParameterForId(),"id1");
        properties.put("channel","com");

        SearchResponseContext[] contexts = new SearchResponseContext[] {new AsyncServletSearchResponseContext(mock(AsyncContext.class))};

        RelatedProductSearchRequestTranslator translator = new RelatedProductSearchRequestTranslator(
                new RelatedProductSearchFactoryWithSearchLookupKeyFactory(config,new KeyFactoryBasedRelatedProductSearchLookupKeyGenerator(config,new SipHashSearchRequestLookupKeyFactory())));



        translator.translateTo(request, 1,RelatedProductSearchType.FREQUENTLY_RELATED_WITH,properties,contexts);

        assertSame(request.getRequestContext().getContexts(), contexts);

        assertEquals(request.getSearchRequest().getRelatedContentId(),"id1");

        assertEquals(1,request.getSearchRequest().getAdditionalSearchCriteria().getNumberOfProperties());

        assertEquals("channel",request.getSearchRequest().getAdditionalSearchCriteria().getPropertyName(0));
        assertEquals("com",request.getSearchRequest().getAdditionalSearchCriteria().getPropertyValue(0));
    }
}
