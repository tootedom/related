package org.greencheek.relatedproduct.util.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class YamlSystemPropertiesConfigurationTest {

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
        System.clearProperty(YamlSystemPropertiesConfiguration.PROPNAME_SETTINGS_YAML_LOCATION);
        System.clearProperty(ConfigurationConstants.PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE);
    }


    @Test
    public void testTesYamlFileIsRead() {
        System.setProperty(YamlSystemPropertiesConfiguration.PROPNAME_SETTINGS_YAML_LOCATION,"test-related-product.yaml");
        Configuration config = new YamlSystemPropertiesConfiguration();

        assertEquals(1024, config.getSizeOfRelatedContentSearchRequestHandlerQueue());
        assertEquals(16,config.getNumberOfSearchingRequestProcessors());
        assertEquals(4096,config.getSizeOfBatchIndexingRequestQueue());

    }

    @Test
    public void testYamlFileThatDoesNotExistWillUseDefault() {
        System.setProperty(YamlSystemPropertiesConfiguration.PROPNAME_SETTINGS_YAML_LOCATION,"test-related-product-doesnotexist.yaml");
        Configuration config = new YamlSystemPropertiesConfiguration();

        assertEquals(ConfigurationConstants.DEFAULT_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE, config.getSizeOfRelatedContentSearchRequestHandlerQueue());
        assertEquals(ConfigurationConstants.DEFAULT_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS,config.getNumberOfSearchingRequestProcessors());
        assertEquals(ConfigurationConstants.DEFAULT_SIZE_OF_BATCH_STORAGE_INDEX_REQUEST_QUEUE,config.getSizeOfBatchIndexingRequestQueue());
    }

    @Test
    public void testSystemPropertiesOverrideYamlFile() {
        System.setProperty(YamlSystemPropertiesConfiguration.PROPNAME_SETTINGS_YAML_LOCATION,"test-related-product.yaml");
        System.setProperty(ConfigurationConstants.PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_HANDLER_QUEUE,"8192");
        Configuration config = new YamlSystemPropertiesConfiguration();

        assertEquals(8192, config.getSizeOfRelatedContentSearchRequestHandlerQueue());
        assertEquals(16,config.getNumberOfSearchingRequestProcessors());
        assertEquals(4096,config.getSizeOfBatchIndexingRequestQueue());
    }

}
