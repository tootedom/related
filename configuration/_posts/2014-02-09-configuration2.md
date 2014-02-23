---
is_for_config : true
page_no : 2
date : 2014-02-14
category : configuration
title : ES Type Mapping
desc : Gives a base template for the mapping to used for the related item documents when stored in the Elasticsearch cluster
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
                  "date": { "type": "date", "index": "not_analyzed", "store" : "no" },
                  "channel" : {"type" : "string" , "index" : "not_analyzed", "store" : "no" },
                  "site" : {"type" : "string" , "index" : "not_analyzed", "store" : "no" },
                  "type" : {"type" : "string" , "index" : "not_analyzed", "store" : "no" }
               }
            }
        }
    }'

----

## Sharding/Replicas ##

As previously mentioned within the searching section, by default a new index is created in elastcisearch each day ([Searching Indexes]({{site.baseurl}}/searching/searching4/).
When a search is performed, the search is made across all those indexes.  In order to present the frequently related items elasticsearch facetting is used.  It is recommended
that only 1 shard is used, in order for acurate results to be returned for the faceting.  As a new index is being created daily, (a shard is effectively a new index), the lack of
sharding will not impact the system greatly.  For most information regarding using more than 1 shard please see the following bug report: [ES-1305](https://github.com/elasticsearch/elasticsearch/issues/1305).
If you wish to use more the on shard then use either the property `related-item.searching.default.number.of.results`; setting it to a larger number, or at search time specify the query parameter: `maxresults` to a large value.

However, be warned setting either `maxresults` or `related-item.searching.default.number.of.results`, means the search result will be larger.

As a result, it is advisable to only have `1 shard`.  Instead you should have a least 1 or more `replicas`.  Whilst having 1 or more `replicas` will slow down indexing slightly (writes have to go to more than 1 index).  However, having 1 or more replicas makes sense from two aspects:

* A backup of the index.  If one node fails; the replica living on another node can take over. 
* Searches are more performant.
