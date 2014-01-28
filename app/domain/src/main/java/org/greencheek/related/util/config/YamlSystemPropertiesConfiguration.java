package org.greencheek.related.util.config;

import org.elasticsearch.common.base.Charsets;
import org.elasticsearch.common.io.Streams;
import org.elasticsearch.common.settings.loader.SettingsLoader;
import org.elasticsearch.common.settings.loader.SettingsLoaderFactory;
import org.elasticsearch.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dominictootell on 18/01/2014.
 */
public class YamlSystemPropertiesConfiguration extends SystemPropertiesConfiguration {

    public static final String PROPNAME_SETTINGS_YAML_LOCATION = "related-items.settings.file";
    public static final String DEFAULT_SETTINGS_YAML_LOCATION = "related-items.yaml";

    private static final Logger log = LoggerFactory.getLogger(YamlSystemPropertiesConfiguration.class);


    public YamlSystemPropertiesConfiguration() {
        super(replaceProperties(replaceProperties(ConfigurationConstants.DEFAULT_SETTINGS,yamlProperties(System.getProperty(PROPNAME_SETTINGS_YAML_LOCATION, DEFAULT_SETTINGS_YAML_LOCATION))),parseSystemProperties()));
    }

    public static Map<String,Object> yamlProperties(String filePath) {
        Map<String,Object> properties = new HashMap<String,Object>(100);
        Environment env = new Environment();
        InputStream is=null;
        try {
            URL url = env.resolveConfig(filePath);
            String resourceName = url.toExternalForm();
            is= url.openStream();
            SettingsLoader settingsLoader = SettingsLoaderFactory.loaderFromResource(resourceName);
            Map<String,String> settings = settingsLoader.load(Streams.copyToString(new InputStreamReader(is, Charsets.UTF_8)));
            if(settings!=null) {
                properties.putAll(parseProperties(settings));
            }
        } catch (Exception e) {
            log.warn("Unable to load YAML settings: {}. Defaults and System Properties will be in place", filePath);
        }

        return properties;

    }
}
