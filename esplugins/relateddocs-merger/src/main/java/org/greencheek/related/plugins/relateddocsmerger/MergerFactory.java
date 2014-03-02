package org.greencheek.related.plugins.relateddocsmerger;

import org.elasticsearch.common.Nullable;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.NativeScriptFactory;
 
import java.util.Map;
 
public class MergerFactory implements NativeScriptFactory {

    protected static final String DEFAULT_COMPARATOR_STRING_KEY = "md5";
    protected static final String COMPARATOR_PARAM = "comparator";

    @Override public ExecutableScript newScript(@Nullable Map<String, Object> params) {
        String documentComparatorValue = null;
        String documentComparatorKey = DEFAULT_COMPARATOR_STRING_KEY;
        Object comparator = params.remove(COMPARATOR_PARAM);
        if(comparator==null) {
            comparator = params.get(DEFAULT_COMPARATOR_STRING_KEY);

        } else {
            if(comparator instanceof String) {
                documentComparatorKey = (String)comparator;
            }
            comparator = params.get(documentComparatorKey);
            if(comparator == null) {
                documentComparatorKey = DEFAULT_COMPARATOR_STRING_KEY;
                comparator = params.get(DEFAULT_COMPARATOR_STRING_KEY);
            }
        }

        if(comparator instanceof String) {
            documentComparatorValue = (String)comparator;
        }

        return new RelatedDocsMergerScript(params,documentComparatorKey,documentComparatorValue);
    }
}