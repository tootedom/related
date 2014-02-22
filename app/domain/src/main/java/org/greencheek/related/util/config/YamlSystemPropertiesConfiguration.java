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
    public static final String DEFAULT_REFERENCE_SETTINGS_YAML_LOCATION = "reference-related-items.yaml";
    public static final String DEFAULT_SETTINGS_YAML_LOCATION = "related-items.yaml";

    private static final Logger log = LoggerFactory.getLogger(YamlSystemPropertiesConfiguration.class);


    public YamlSystemPropertiesConfiguration() {
        super(replaceProperties(replaceProperties(ConfigurationConstants.DEFAULT_SETTINGS,yamlProperties(System.getProperty(PROPNAME_SETTINGS_YAML_LOCATION, DEFAULT_SETTINGS_YAML_LOCATION))),parseSystemProperties()));
    }

    public static Map<String,Object> yamlProperties(String filePath) {
        Map<String,Object> properties = new HashMap<String,Object>(100);
        Environment env = new Environment();

        // Try reading the reference settings.
        InputStream is;
        try {
            URL url = env.resolveConfig(DEFAULT_REFERENCE_SETTINGS_YAML_LOCATION);
            String resourceName = url.toExternalForm();
            is= url.openStream();
            SettingsLoader settingsLoader = SettingsLoaderFactory.loaderFromResource(resourceName);
            Map<String,String> settings = settingsLoader.load(Streams.copyToString(new InputStreamReader(is, Charsets.UTF_8)));
            if(settings!=null) {
                properties.putAll(parseProperties(settings));
            }
        } catch (Exception e) {
            log.info("Unable to load reference YAML settings: {}. Defaults and System Properties will be in place", filePath);
        }

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
