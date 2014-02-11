---
is_for_config : true
page_no : 1
date : 2014-02-15
title : Elasticsearch
categories : 
- configuration
---


The related item data is stored in that of elasticsearch.  The ability to find the frequently related items, for a given item, is provided by that of elasticsearch and its faceting.  

The indexing and searching web applications use the elasticsearch java library.  

The means by which the indexing and searching applications talk to elastic is by using the elasticsearch binary transport protocol.  Meaning, the version of the client embedded within the web applications (indexing and searching), *MUST* match that of the elasticsearch server.  Also, the version of JAVA on the client and the server *MUST* be the same.

    * Current embedded elasticsearch version is: **0.90.9**

By default both applications use the Transport protocol to connect to elasticsearch with sniffing enabled:

    client.transport.sniff : true

(Sniffing means that you can specify only a couple of hosts, and the client will glen information on the rest of the cluster through those nodes).

The reason behind no support for HTTP endpoint is just to focus on using the most performant client option, which is that of the transport client.  HTTP support is on the list of things to enable, but it would require either extra configuration at your side (i.e. a load balancer), or for the application to provide a simple round robin implementation to round robin request over a list of nodes.  So at the moment only the `node`, or `transport` option are available.  With the default being that of `transport`.

### Elasticsearch Connection Configuration ###

The defaults for indexing and searching have been set based on a JVM the is running 1GB with 128m of PermGen (The specific configuration for these JVM Parameters can be found below).

At minimum the only configuration required is the connection details for your elasticsearch installation:

    * related-item.elastic.search.transport.hosts=10.0.1.19:9300

This can be a comma separated list of hosts:

    * related-item.elastic.search.transport.hosts=10.0.1.19:9300,10.0.1.29:9300

By default the application uses the TRANSPORT client to connect to elastic search.  If you only specify one host, but you have 2 nodes in your elasticsearch cluster, the transport client is enabled by default to sniff (ask the node for information about other nodes in the cluster), and obtain a list of other nodes to connect to.