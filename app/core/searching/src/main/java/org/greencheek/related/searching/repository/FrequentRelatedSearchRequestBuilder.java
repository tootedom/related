package org.greencheek.related.searching.repository;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.greencheek.related.api.RelatedItemAdditionalProperties;
import org.greencheek.related.api.searching.RelatedItemSearch;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by dominictootell on 20/02/2014.
 */
public class FrequentRelatedSearchRequestBuilder {

    private static final Logger log = LoggerFactory.getLogger(FrequentRelatedSearchRequestBuilder.class);


    private final String relatedWithAttribute;
    private final String facetResultName;
    private final String itemIdentifierAttribute;
    private final String executionHint;
    private final boolean hasExecutionHint;
    private final TimeValue searchTimeoutValue;

    public FrequentRelatedSearchRequestBuilder(Configuration configuration) {
        this.facetResultName = configuration.getStorageFrequentlyRelatedItemsFacetResultsFacetName();
        long searchTimeout = configuration.getFrequentlyRelatedItemsSearchTimeoutInMillis();
        this.searchTimeoutValue = TimeValue.timeValueMillis(searchTimeout);

        String executionHint = configuration.getStorageFacetExecutionHint();

        if(executionHint == null) {
            hasExecutionHint = false;
        } else {
            executionHint = executionHint.trim();
            if(executionHint.length()==0) {
                hasExecutionHint = false;
            } else {
                hasExecutionHint=true;
            }
        }

        this.executionHint = executionHint;

        this.relatedWithAttribute = configuration.getKeyForIndexRequestRelatedWithAttr();
        this.itemIdentifierAttribute = configuration.getKeyForIndexRequestIdAttr();

    }


    public SearchSourceBuilder createFrequentlyRelatedContentSearch(RelatedItemSearch search) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        String id = search.getRelatedItemId();

        BoolFilterBuilder b = FilterBuilders.boolFilter();
        b.must(FilterBuilders.termFilter(relatedWithAttribute, id));


        RelatedItemAdditionalProperties searchProps = search.getAdditionalSearchCriteria();
        int numberOfProps = searchProps.getNumberOfProperties();

        for(int i = 0;i<numberOfProps;i++) {
            b.must(FilterBuilders.termFilter(searchProps.getPropertyName(i), searchProps.getPropertyValue(i)));
        }
        ConstantScoreQueryBuilder cs = QueryBuilders.constantScoreQuery(b);


        TermsFacetBuilder facetBuilder = FacetBuilders.termsFacet(facetResultName).field(itemIdentifierAttribute).size(search.getMaxResults());
        if(hasExecutionHint) {
            facetBuilder.executionHint(executionHint);
        }


        sourceBuilder.size(0);
        sourceBuilder.query(cs);
        sourceBuilder.timeout(searchTimeoutValue);
        sourceBuilder.facet(facetBuilder);
        log.debug("Frequently Related Query Built: {}",sourceBuilder);
        return sourceBuilder;

    }
}
