---
is_for_config : true
page_no : 5
date : 2014-02-11
title : App Config
categories : 
- configuration
---

The Searching and Indexing web applications can be configured via a wide range of properties.  These can either be set using System Properties:

    -Drelated-item.max.number.related.item.properties=10

Or by using a yaml configuration file:


    related-item:  
      max.related.item.post.data.size.in.bytes: 65536
      additional.prop.key.length: 20
      additional.prop.value.length: 20
      max.number.related.item.properties: 10
      indexing:
        size.of.incoming.request.queue:  32
    

The following will list all the properties that are available for configuration, along with their use.  Some properties are specifically for searching, others specifically for indexing, and others for both.  This will be noted.


### Indexing ###

Property | Use  
 ---- | ---- 
{.nowraptext}`related-item.safe.to.output.index.request.data=false` | Writes to logs (when DEBUG) the index request data 
{.nowraptext}`related-item.max.number.related.item.properties=10`    | The max number of properties a related item can have.  More properties than this will be silently discarded.  There is no guarantee of ordering 
{.nowraptext}`related-item.max.number.related.items.per.index.request=10`  | The max number of related items in a single index POST request 
{.nowraptext}`related-item.max.related.item.post.data.size.in.bytes=10240`  | Max size in bytes of the POST data for an index request 
{.nowraptext}`related-item.min.related.item.post.data.size.in.bytes=4096`   | The minimum size, in bytes, of the POSTed json data for an index request 
{.nowraptext}`related-item.indexing.size.of.incoming.request.queue=16384` | Size of the ring buffer that accepts incoming indexing POST requests 
{.nowraptext}`related-item.indexing.size.of.batch.indexing.request.queue=-1` | The size of the ring buffer for each indexing processor that batch posts indexing requests to elasticsearch
{.nowraptext}`related-item.indexing.batch.size=625` | The max number of related item objects (a single index request will have many related item objects), that can be sent for batching indexing to elastic search.
{.nowraptext}`related-item.indexing.number.of.indexing.request.processors=2` | number of processors used to perform indexing (sending batch indexing requests) to elasticsearch
{.nowraptext}`related-item.indexing.replace.old.indexed.content=false` | replace existing content (false)
{.nowraptext}`related-item.use.separate.repository.storage.thread=false` | Use a separate thread for performing indexing
{.nowraptext}`related-item.indexing.discard.storage.requests.with.too.many.relations=false` | Silently discard related items in the indexing request it there are too many.  Indexes up to the max, discards the others   

### Searching ###

Property | Use  
 ---- | ---- 
{.nowraptext}`related-item.searching.size.of.related.content.search.request.queue=16384` | Size of the ring buffer that accepts incoming search requests
{.nowraptext}`related-item.searching.size.of.related.content.search.request.handler.queue=-1` | Size of the ring buffer for each search processor that submits search requests to elasticsearch
{.nowraptext}`related-item.searching.size.of.related.content.search.request.and.response.queue=-1` | Size of the ring buffer that is used to store incoming Request AsyncContext objects for later retrieval
{.nowraptext}`related-item.searching.max.number.of.search.criteria.for.related.content=10` | number of additional properties that will be searched on
{.nowraptext}`related-item.searching.number.of.expected.like.for.like.requests=10` | The number of search request that we expect to be similar
{.nowraptext}`related-item.searching.key.for.frequency.result.id=id` | The key used for the id field in the search result json
{.nowraptext}`related-item.searching.key.for.frequency.result.occurrence=frequency` | The key used for the frequency in the search results json
{.nowraptext}`related-item.searching.key.for.storage.response.time=storage_response_time` | Key used to represent how long the elasticsearch request took, in the json response doc 
{.nowraptext}`related-item.searching.key.for.search.processing.time=response_time` | Key used to represent how long the complete search request took.  It is the key used in the response json
{.nowraptext}`related-item.searching.key.for.frequency.result.overall.no.of.related.items=size` | Key in the search response used to represent the number of frequencies returned
{.nowraptext}`related-item.searching.key.for.frequency.results=results` | Key in the search response json under which the frequencies are found
{.nowraptext}`related-item.searching.request.parameter.for.size=maxresults` | Request parameter used to specify the max number of frequencies to return
{.nowraptext}`related-item.searching.request.parameter.for.id=id` | Parameter used to associate the id in a map of request parameters
{.nowraptext}`related-item.searching.default.number.of.results=4` | Default number of search result (frequencies) to return
{.nowraptext}`related-item.searching.size.of.response.processing.queue=-1` | Size of ring buffer for processing search results and sending json response to the awaiting AsyncContext
{.nowraptext}`related-item.searching.number.of.searching.request.processors=2` |  The number of ring buffers (processors) that will be sending search requests to elasticsearch
{.nowraptext}`related-item.storage.frequently.related.items.facet.results.facet.name=frequently-related-with` | The property used for naming the facet during the search request to elastic search 
{.nowraptext}`related-item.storage.searching.facet.search.execution.hint=map` | Used during search request to elastic search.  The setting of 'map' is the default.  Makes request much much faster 
{.nowraptext}`related-item.searching.frequently.related.search.timeout.in.millis=5000` | Timeout in millis for elasticsearch requests
{.nowraptext}`related-item.searching.timed.out.search.request.status.code=504` | The http status code when a timeout occurs
{.nowraptext}`related-item.searching.failed.search.request.status.code=502` | The http status code when a search request fails to talk to elasticsearch
{.nowraptext}`related-item.searching.not.found.search.request.status.code=404` | The http status code when no search result is found
{.nowraptext}`related-item.searching.found.search.results.handler.status.code=200` | The http status code when a match is found
{.nowraptext}`related-item.searching.missing.search.results.handler.status.code=500` | The http status code when we cannot handle the json search response
{.nowraptext}`related-item.searching.use.shared.search.repository=false` | Whether the search processors use a shared connection to elastic search
{.nowraptext}`related-item.searching.response.debug.output.enabled=false` | Output the response json being sent to the client, also to a log file.

### Both ###

Property | Use
 ---- | ---- 
{.nowraptext}`related-item.related.item.id.length=36` | The max number of characters that the "id" of a related items can have
{.nowraptext}`related-item.additional.prop.key.length=30`| The max number of characters a property name can have
{.nowraptext}`related-item.additional.prop.value.length=30` | The max number of characters a property value can have
{.nowraptext}`related-item.storage.index.name.prefix=relateditems` |  The name of the index used in elasticsearch for storing related item documents (i.e. `relateditems-YYYY-MM-DD`) 
{.nowraptext}`related-item.storage.index.name.alias=` (no alias) | The name of the index alias against which to search (http://www.elasticsearch.org/blog/changing-mapping-with-zero-downtime/) 
{.nowraptext}`related-item.storage.content.type.name=related` | The index type
{.nowraptext}`related-item.storage.cluster.name=relateditems` | The name of the elasticsearch cluster 
{.nowraptext}`related-item.indexing.key.for.index.request.related.with.attr=items` | The key used in the indexed document for the storing the related ids
{.nowraptext}`related-item.indexing.key.for.index.request.date.attr=date` | The key used in the indexed document for the date attribute
{.nowraptext}`related-item.indexing.key.for.index.request.id.attr=id` | The key against which the id is stored in the indexed document 
{.nowraptext}`related-item.indexing.key.for.index.request.item.array.attr=items` | The key in the incoming user json indexing request that contains the list of items
{.nowraptext}`related-item.elastic.search.client.default.transport.settings.file.name=default-transport-elasticsearch.yml` | name of the elastic search file containing the transport client settings (defaults)
{.nowraptext}`related-item.elastic.search.client.default.node.settings.file.name=default-node-elasticsearch.yml` | name of the elasticsearch file containing the node client settings (defaults)
{.nowraptext}`related-item.elastic.search.client.override.settings.file.name=elasticsearch.yml` | name of the elasticsearch file than can be distributed to override the default node/transport settings
{.nowraptext}`related-item.storage.location.mapper=day` | day/hour/min used to convert date to a string used for creating the index name in which documents are stored
{.nowraptext}`related-item.wait.strategy=yield`  | The type of ring buffer wait strategy: yield/busy/sleep/block
{.nowraptext}`related-item.es.client.type=transport` | The type of elasticsearch client to use 
{.nowraptext}`related-item.indexing.indexname.date.caching.enabled=true` | caching of index date
{.nowraptext}`related-item.indexing.number.of.indexname.to.cache=365` | number of index names to cache 
{.nowraptext}`related-item.elastic.search.transport.hosts=127.0.0.1:9300` | The host:port,host:port contain the unicast addresses of the search nodes in elastic search to talk to
{.nowraptext}`related-item.elastic.search.default.port=9300` | The default port if not specified to talk to in elasticsearch

----

By default the Searching and Indexing web applications will look for a yaml configuration file from which to load the configuration details.  Any settings in the configuration file, override the defaults.  Any system properties set will override the settings that are contained within the yaml configuration.  

By default the yaml file **related-items.yaml** is looked for on the class path.  The location of the file can be specified by the property, **related-items.settings.file**, for example:

* -Drelated-items.settings.file=/etc/relateditems.yml

The yaml file, may look like the following:

    related-item:
           searching:
                  number.of.searching.request.processors: 16
                  size.of.related.content.search.request.handler.queue: 1024

           indexing:
                  size.of.batch.indexing.request.queue: 4096


With the above in place the following properties are overridden:

    related-item.searching.number.of.searching.request.processors
    related-item.searching.size.of.related.content.search.request.handler.queue
    related-time.indexing.size.of.batch.indexing.reqeust.queue

If a system properties was set `-Drelated-item.searching.number.of.searching.request.processors=2`, that would override the setting in the yaml file.
