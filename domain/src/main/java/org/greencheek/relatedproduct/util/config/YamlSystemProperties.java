package org.greencheek.relatedproduct.util.config;

import org.elasticsearch.common.base.Charsets;
import org.elasticsearch.common.io.Streams;
import org.elasticsearch.common.settings.loader.SettingsLoader;
import org.elasticsearch.common.settings.loader.SettingsLoaderFactory;
import org.elasticsearch.common.settings.loader.YamlSettingsLoader;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.FailedToResolveConfigException;
import org.greencheek.relatedproduct.api.searching.SearchResultsOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dominictootell on 18/01/2014.
 */
public class YamlSystemProperties extends SystemPropertiesConfiguration {

    public static final String PROPNAME_SETTINGS_YAML_LOCATION = "related-product.settings.file";
    public final String SETTINGS_YAML_LOCATION = System.getProperty(PROPNAME_SETTINGS_YAML_LOCATION,"related-product.yaml");
    private static final Logger log = LoggerFactory.getLogger(YamlSystemProperties.class);


    private final Map<String,Object> configuration = new HashMap<String,Object>(100);

    public YamlSystemProperties() {
        super();
        Map<String,Object> defaults = super.toMap();
        Map<String,Object> overrides = super.getDiffs(defaults,super.toMap());

        Environment env = new Environment();
        InputStream is=null;
        try {
            URL url = env.resolveConfig(SETTINGS_YAML_LOCATION);
            String resourceName = url.toExternalForm();
            is= url.openStream();
            SettingsLoader settingsLoader = SettingsLoaderFactory.loaderFromResource(resourceName);
            Map<String,String> settings = settingsLoader.load(Streams.copyToString(new InputStreamReader(is, Charsets.UTF_8)));

        } catch (FailedToResolveConfigException e) {
           log.error("Unable to load YAML settings: {}. Defaults and System Properties will be in place", SETTINGS_YAML_LOCATION);
        } catch (IOException e) {
            log.error("Unable to load YAML settings: {}. Defaults and System Properties will be in place", SETTINGS_YAML_LOCATION);
        }

    }


    @Override
    public WaitStrategyFactory getWaitStrategyFactory() {
        return null;
    }

    @Override
    public int getNumberOfIndexingRequestProcessors() {
        return 0;
    }

    @Override
    public int getMaxNumberOfRelatedProductProperties() {
        return 0;
    }

    @Override
    public int getMaxNumberOfRelatedProductsPerPurchase() {
        return 0;
    }

    @Override
    public int getRelatedProductIdLength() {
        return 0;
    }

    @Override
    public String getRelatedProductInvalidIdString() {
        return null;
    }

    @Override
    public int getMinRelatedProductPostDataSizeInBytes() {
        return 0;
    }

    @Override
    public int getMaxRelatedProductPostDataSizeInBytes() {
        return 0;
    }

    @Override
    public int getRelatedProductAdditionalPropertyKeyLength() {
        return 0;
    }

    @Override
    public int getRelatedProductAdditionalPropertyValueLength() {
        return 0;
    }

    @Override
    public boolean isSearchResponseDebugOutputEnabled() {
        return false;
    }

    @Override
    public int getIndexBatchSize() {
        return 0;
    }

    @Override
    public int getSizeOfBatchIndexingRequestQueue() {
        return 0;
    }

    @Override
    public int getSizeOfIncomingMessageQueue() {
        return 0;
    }

    @Override
    public int getMaxNumberOfSearchCriteriaForRelatedContent() {
        return 0;
    }

    @Override
    public int getSizeOfRelatedContentSearchRequestHandlerQueue() {
        return 0;
    }

    @Override
    public int getSizeOfRelatedContentSearchRequestQueue() {
        return 0;
    }

    @Override
    public int getSizeOfRelatedContentSearchRequestAndResponseQueue() {
        return 0;
    }

    @Override
    public int getSizeOfResponseProcessingQueue() {
        return 0;
    }

    @Override
    public int getNumberOfSearchingRequestProcessors() {
        return 0;
    }

    @Override
    public int getNumberOfExpectedLikeForLikeRequests() {
        return 0;
    }

    @Override
    public String getKeyForIndexRequestRelatedWithAttr() {
        return null;
    }

    @Override
    public String getKeyForFrequencyResults() {
        return null;
    }

    @Override
    public String getKeyForFrequencyResultOccurrence() {
        return null;
    }

    @Override
    public String getKeyForFrequencyResultId() {
        return null;
    }

    @Override
    public String getKeyForFrequencyResultOverallResultsSize() {
        return null;
    }

    @Override
    public String getKeyForIndexRequestDateAttr() {
        return null;
    }

    @Override
    public String getKeyForIndexRequestIdAttr() {
        return null;
    }

    @Override
    public String getKeyForIndexRequestProductArrayAttr() {
        return null;
    }

    @Override
    public String getRequestParameterForSize() {
        return null;
    }

    @Override
    public String getRequestParameterForId() {
        return null;
    }

    @Override
    public int getDefaultNumberOfResults() {
        return 0;
    }

    @Override
    public String getStorageIndexNamePrefix() {
        return null;
    }

    @Override
    public String getStorageIndexNameAlias() {
        return null;
    }

    @Override
    public String getStorageContentTypeName() {
        return null;
    }

    @Override
    public String getStorageClusterName() {
        return null;
    }

    @Override
    public String getStorageFrequentlyRelatedProductsFacetResultsFacetName() {
        return null;
    }

    @Override
    public String getElasticSearchClientDefaultNodeSettingFileName() {
        return null;
    }

    @Override
    public String getElasticSearchClientDefaultTransportSettingFileName() {
        return null;
    }

    @Override
    public String getElasticSearchClientOverrideSettingFileName() {
        return null;
    }

    @Override
    public String getStorageLocationMapper() {
        return null;
    }

    @Override
    public long getFrequentlyRelatedProductsSearchTimeoutInMillis() {
        return 0;
    }

    @Override
    public int getResponseCode(SearchResultsOutcome type) {
        return 0;
    }

    @Override
    public int getTimedOutSearchRequestStatusCode() {
        return 0;
    }

    @Override
    public int getFailedSearchRequestStatusCode() {
        return 0;
    }

    @Override
    public int getNoFoundSearchResultsStatusCode() {
        return 0;
    }

    @Override
    public int getFoundSearchResultsStatusCode() {
        return 0;
    }

    @Override
    public String getPropertyEncoding() {
        return null;
    }

    @Override
    public boolean isIndexNameDateCachingEnabled() {
        return false;
    }

    @Override
    public int getNumberOfIndexNamesToCache() {
        return 0;
    }

    @Override
    public boolean getShouldReplaceOldContentIfExists() {
        return false;
    }

    @Override
    public boolean getShouldUseSeparateIndexStorageThread() {
        return false;
    }

    @Override
    public boolean shouldDiscardIndexRequestWithTooManyRelations() {
        return false;
    }

    @Override
    public ElasticeSearchClientType getElasticSearchClientType() {
        return null;
    }

    @Override
    public String getElasticSearchTransportHosts() {
        return null;
    }

    @Override
    public int getDefaultElasticSearchPort() {
        return 0;
    }

    @Override
    public boolean useSharedSearchRepository() {
        return false;
    }

    @Override
    public boolean isSafeToOutputRequestData() {
        return false;
    }

    @Override
    public String getStorageFacetExecutionHint() {
        return null;
    }
}
