package org.greencheek.related.searching.repository.http;

import org.greencheek.related.util.config.Configuration;

/**
 * Created by dominictootell on 07/03/2014.
 */
public class HttpUtil {

    public static String createMGetHttpEnpointUrlPath(Configuration configuration) {
        String indexName = configuration.getRelatedItemsDocumentIndexName();
        String typeName = configuration.getRelatedItemsDocumentTypeName();
        String endpoint = configuration.getElasticSearchMultiGetEndpoint();
        StringBuilder b = new StringBuilder(indexName.length()+typeName.length()+endpoint.length()+3);

        b.append('/').append(indexName).append('/').append(typeName);
        if(endpoint.charAt(0)=='/') {
            b.append(endpoint);
        } else {
            b.append('/').append(endpoint);
        }
        return b.toString();
    }
}
