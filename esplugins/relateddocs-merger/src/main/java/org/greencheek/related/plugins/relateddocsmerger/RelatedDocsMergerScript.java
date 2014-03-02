package org.greencheek.related.plugins.relateddocsmerger;

import org.elasticsearch.script.AbstractExecutableScript;
import org.elasticsearch.search.lookup.DocLookup;

import java.util.Map;


public class RelatedDocsMergerScript extends AbstractExecutableScript {

    private final Map<String,Object> params;
    private Map<String, Object> ctx;
    private Map<String,Object> source;
    final String comparatorValue;
    final String comparatorKey;

    RelatedDocsMergerScript(Map<String, Object> params,
                            String comparatorKey,
                            String comparatorValue) {
        this.params = params;
        this.comparatorKey = comparatorKey;
        this.comparatorValue = comparatorValue;
    }

    @Override
    public void setNextVar(String name, Object value) {
        if(name.equals("ctx") && value instanceof Map) {
            ctx = (Map<String, Object> )value;
            source = (Map<String,Object>)ctx.get("_source");
        }
    }

    @Override
    public Object run() {
        if(ctx!=null && source!=null && comparatorValue!=null) {
            String md5Prop = (String)source.get(comparatorKey);
            if(md5Prop==null) {
                for(String key: params.keySet()) {
                    source.put(key,params.get(key));
                }
            }
            else {
                if(md5Prop.equals(comparatorValue)) {
                    ctx.put("op","none");
                } else {
                    for(String key: params.keySet()) {
                        source.put(key,params.get(key));
                    }
                }
            }
        }
        return null;
    }

}