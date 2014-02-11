---
is_for_config : true
page_no : 2
date : 2014-02-14
title : ES Type Mapping
categories : 
- configuration
---

When the indexing and searching applications talk to elasticsearch they search for documents within the index `relateditems-YYYY-MM-DD` for the document type `related`.  The defined properties require for the `related` type are that of: 

* id 
* related-with
* date

When indexing documents in elasticsearch, if a type (i.e. "related") does not have an associated mapping then a dynamic mapping of a document's json properties are created by that of elasticsearch (the default behaviour of elasticsearch)    Which may or may not be what is required.  As a result, you should define a mapping for the type.  The mapping for the related type should at minimum be the following:

    curl -XPUT http://localhost:9200/_template/relateditems -d '{
        "template" : "relateditems*",
        "settings" : {
            "number_of_shards" : 1,
            "number_of_replicas" : 1,
            "index.refresh_interval" : "5s",
            "index.store.compress.stored" : false,
            "index.query.default_field" : "id",
            "index.routing.allocation.total_shards_per_node" : 1,
            "indices.memory.index_buffer_size" : 30
        },
        "mappings" : {
            "related" : {
               "_all" : {"enabled" : false},
               "dynamic" : false,
               "properties" : {
                  "id": { "type": "string", "index": "not_analyzed", "store" : "yes" },
                  "related-with": { "type": "string", "index": "not_analyzed", "store" : "yes" },
                  "date": { "type": "date", "index": "not_analyzed", "store" : "no" }
               }
            }
        }
    }'

If the related documents you are indexed are going to have more properties (i.e. channel, type, etc).  You need to expand upon the mapping above to detail those properties.  A guide to mapping can be found [In the following elasticsearch documentation](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-core-types.html)

    curl -XPUT http://10.0.1.19:9200/_template/relateditems -d '{
        "template" : "relateditems*",
        "settings" : {
            "number_of_shards" : 1,
            "number_of_replicas" : 1,
            "index.refresh_interval" : "5s",
            "index.store.compress.stored" : false,
            "index.query.default_field" : "id",
            "index.routing.allocation.total_shards_per_node" : 1,
            "indices.memory.index_buffer_size" : 30
        },
        "mappings" : {
            "related" : {
               "_all" : {"enabled" : false},
               "dynamic" : false,
               "properties" : {
                  "id": { "type": "string", "index": "not_analyzed", "store" : "yes" },
                  "related-with": { "type": "string", "index": "not_analyzed", "store" : "yes" },
                  "date": { "type": "date", "index": "not_analyzed", "store" : "no" },
                  "channel" : {"type" : "string" , "index" : "not_analyzed", "store" : "no" },
                  "site" : {"type" : "string" , "index" : "not_analyzed", "store" : "no" },
                  "type" : {"type" : "string" , "index" : "not_analyzed", "store" : "no" }
               }
            }
        }
    }'
