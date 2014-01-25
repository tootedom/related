package org.greencheek.related.util.config;

import junit.framework.TestCase;
import org.greencheek.related.util.arrayindexing.Util;
import org.junit.Test;

/**
 * Created by dominictootell on 25/01/2014.
 */
public class SystemPropertiesConfigurationTest extends TestCase {

    @Test
    public void testSearchHandlerSize() {
        System.setProperty(ConfigurationConstants.PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE, "32768");
        SystemPropertiesConfiguration config = new SystemPropertiesConfiguration();

        System.out.println(Util.ceilingNextPowerOfTwo(config.getSizeOfRelatedItemSearchRequestHandlerQueue()));
    }
}
