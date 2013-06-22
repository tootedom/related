package org.greencheek.relatedproduct.bootstrap;

import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 18/06/2013
 * Time: 19:29
 * To change this template use File | Settings | File Templates.
 */
public class SystemPropertiesConfigurationBootStrap implements ConfigurationBootStrap {

    private static final Configuration configuration;

    static {
        configuration = new SystemPropertiesConfiguration();
    }


    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
}
