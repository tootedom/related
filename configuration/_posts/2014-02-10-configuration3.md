---
is_for_config : true
page_no : 3
date : 2014-02-13
category : configuration
title : ES Configuration
desc : Specifies the required settings in the elasticsearch.yml configuration for the indexing and search webapps to talk to elasticsearch
categories : 
- configuration
---

The elasticsearch server itself also requires some configuration.  By default out of the box elastic search will use multicast to locate other nodes in the cluster, and will locally store indexes inside the `data/` directory in the elasticsearch download installation location.  You will more than likely want to make changes to this configuration, for example:

    * Move to unicast if your network does not cope with multicast traffic routing well (i.e. multiple data centres, etc.)
    * Move the local storage to a raid array, with raid 1, 5 or raid 1+0 (10), away from the data/ directory.  So that you can update the elasticsearch binaries without affecting the data indexed.

The elasticsearch configuration file (`config/elasticsearch.yml`), needs to be updated to reflect the default cluster name that the indexing and searching application will be looking for the elasticsearch cluster/nodes to be operating with (the default being `relateditems`).  The name of the cluster is controlled by the following property on the Searching or Indexing web application:

    related-item.storage.cluster.name

Therefore the elasticsearch configuration (`config/elasticsearch.yml`) should have the following set:
 
    cluster.name: relateditems

There are several other properties that are not by default in the elasticsearch.yml file, that assist in its operations (searching, bulk operations, getting and indexing).  The following configuration reduces the size of the queue, and the maximum number of threads that elasticsearch can run of the given operations.  By changing the defaults we are allowing existing operations to complete, without flooding it with more requests until it is unable to cope with the load.  As a result we bound the size of the pools and queues, in order to apply back pressure to the request's origin (I.e. The search application and the indexing application)

These pool settings are as follows.  The settings a highly dependent upon the size of your elastic search cluster.  This is just a set of recommendations.

### Search pool

    threadpool.search.type: fixed
    threadpool.search.size: 20
    threadpool.search.queue_size: 100000

### Bulk pool
    threadpool.bulk.type: fixed
    threadpool.bulk.size: 25
    threadpool.bulk.queue_size: 100000

### Get Pool
    threadpool.get.type: fixed
    threadpool.get.size: 1
    threadpool.get.queue_size: 1

### Index pool
    threadpool.index.type: fixed
    threadpool.index.size: 20
    threadpool.index.queue_size: 100000