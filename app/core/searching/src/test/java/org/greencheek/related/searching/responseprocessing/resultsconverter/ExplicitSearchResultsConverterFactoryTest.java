/*
 *
 *  * Licensed to Relateit under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Relateit licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.greencheek.related.searching.responseprocessing.resultsconverter;

import org.greencheek.related.api.searching.FrequentlyRelatedSearchResult;
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
