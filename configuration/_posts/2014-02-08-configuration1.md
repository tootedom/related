---
is_for_config : true
page_no : 1
date : 2014-02-15
category : configuration
title : ES Connections
desc : The various ways of connecting the indexing and searching webapps to an Elasticsearch cluster 
categories : 
- configuration
---


The related item data is stored in that of elasticsearch.  The ability to find the frequently related items, for a given item, is provided by that of elasticsearch and its faceting.  

The indexing and searching web applications use the elasticsearch java library.  

The means by which the indexing and searching applications talk to elastic is by using the elasticsearch binary transport protocol.  Meaning, the version of the client embedded within the web applications (indexing and searching), *MUST* match that of the elasticsearch server.  Also, the version of JAVA on the client and the server `MUST` be the same.

    Current embedded elasticsearch version is: **1.0.0**

By default both applications use the Transport protocol to connect to elasticsearch with sniffing enabled:

    client.transport.sniff : true

(Sniffing means that you can specify only a couple of hosts, and the client will glen information on the rest of the cluster through those nodes).


The reason behind no support for HTTP endpoint (in versions 1.0.0 and 1.0.1) is just to focus on using the most performant
client option, which is that of the transport client.   HTTP support is *now* available in version 1.0.2+.

The HTTP support requires either extra configuration at your side (i.e. a load balancer) to load balance over your ES nodes,
Or you can use in simple internal round robin implementation in the HTTP client support.

The default connection protocol is that of `transport`, but the it is relatively easy to use *http*; as shown below.

### Elasticsearch Connection Configuration ###

The defaults for indexing and searching have been set based on a JVM the is running 1GB with 128m of PermGen (The specific configuration for these JVM Parameters can be found below).

At minimum the only configuration required is the connection details for your elasticsearch installation:

    related-item.elastic.search.transport.hosts=10.0.1.19:9300

This can be a comma separated list of hosts:

    related-item.elastic.search.transport.hosts=10.0.1.19:9300,10.0.1.29:9300

By default the application uses the TRANSPORT client to connect to elastic search.  If you only specify one host, but you have 2 nodes in your elasticsearch cluster, the transport client is enabled by default to sniff (ask the node for information about other nodes in the cluster), and obtain a list of other nodes to connect to.

----

### Elasticsearch HTTP Connection Configuration

As of release 1.0.2+ an http client connection has been made available, [Issue 2](https://github.com/tootedom/related/issues/2).
The http client connection means that you do not have to have ES client library, as that of the ES server.  This means
you can upgrade the server with less fear of breaking the client implementation.

The Http client library used is that of the [AsyncHttpClient](https://github.com/AsyncHttpClient/async-http-client).
Even though the asynchronous features of this http client library have not been taken advantage of, the easy of use of
 the library made it a preferred choice over that of something like Apache's Http Client.

To enable the HTTP client connection you need to specify the property:

    -Drelated-item.es.client.type=http

With this enabled the indexing and searching web applications use the HTTP connection factory to talk to the ES server's
http endpoint.   In order for the app to know what to talk to you specify the following property (by default it is `http://127.0.0.1:9200`):

    -Drelated-item.elastic.search.http.hosts=http://10.0.1.19:9200


You can specify multiple hosts, by comma separating them:

    -Drelated-item.elastic.search.http.hosts=http://10.0.1.19:9200,http://10.0.1.29:9200,http://10.0.1.39:9200,http://10.0.1.49:9200

The http client will round robin requests over the given number of endpoints* (*see below for round robin details*).

By default the HTTP client will run a background scheduling task that talks to each ES endpoint, checking for any newly
added ES nodes.  If a node has been added the HTTP client is notified of the new host, and it will be made available for
round robin allocation of http requests.  The background thread runs every 15 minutes hitting the following url:

    host:port/_nodes/http

from the returned json it parses the http endpoint information building a list of available connections.  It is that
list that forms the new list of load balanced nodes.

This sniffing of available ES servers can be disabled with the following property:

    -Drelated-item.elastic.search.http.nodesniffing.enabled=false

If you wish to descrease the frequency of which the sniffing takes place use the following property:

    -Drelated-item.elastic.search.http.nodesniffing.retry.interval=30

The default unit is minutes.  This can be changed with the property:

    -Drelated-item.elastic.search.http.nodesniffing.retry.interval.unit=secs|mins|hours|days

----

#### Elasticsearch HTTP Connection Round Robin

The Http Client performs round robin load balancing of requests over the set of available ES http nodes.
So for example given 2 hosts:

    -Drelated-item.elastic.search.http.hosts=http://10.0.1.19:9200,http://10.0.1.29:9200

Each host will be sent an equal number of requests.  The round robin load balancing implementation uses an array
size to a power of 2, to loop over the array of available hosts.  This means for a list of 3 hosts.  The load is *NOT*
spread evenly over the nodes.  Given 3 hosts:

    -Drelated-item.elastic.search.http.hosts=http://10.0.1.19:9200,http://10.0.1.29:9200,http://10.0.1.39:9200

The host list internally will be expanded to a power of 2 (i.e 4 available hosts for load balancing).  The extra hosts
are made up from the existing list of actual available.   Which means that for 3 defined hosts.
1 host will get more traffic than then others.  For example the repetition may something like the follwoing list, where
`http://10.0.1.19:9200` is in the list twice.

    http://10.0.1.19:9200,http://10.0.1.29:9200,http://10.0.1.39:9200,http://10.0.1.19:9200


----

#### Http Connection configuration 

The following properties can be used to control the HTTP connections, such as the request and connection timeouts to the elasticsearch nodes.


Property | Use  
 ---- | ---- 
{.nowraptext}`related-item.elastic.search.http.request.timeout.ms=5000` | The max amount ot time the http request agaist the elastic search node can take
{.nowraptext}`related-item.elastic.search.http.connect.timeout.ms=2000` | The max amount of time the http request can take to connect to the elasticsearch node
{.nowraptext}`related-item.elastic.search.http.no.of.retries=0`  | The number of retries to try against the elasticsearch node.
{.nowraptext}`related-item.elastic.search.http.follow.redirects=false`  | If we should automatically follow redirects issued by elasticsearch 
{.nowraptext}`related-item.elastic.search.http.connection.pool.enabled=true`   | If keep alive connections should be made to the elasticsearch nodes
{.nowraptext}`related-item.elastic.search.http.compression.enabled=true` | Compressed responses from elasticsearch can be dealt with
{.nowraptext}`related-item.elastic.search.http.nodesniffing.request.timeout.ms=15000` | The amount of time allowed for making the node request to the elasticsearch server.
{.nowraptext}`related-item.elastic.search.http.nodesniffing.connect.timeout.ms=5000` | The amount of time allowed to establish a connection to the elasticsearch node to obtain the nodes it knows about
{.nowraptext}`related-item.elastic.search.http.nodesniffing.no.of.retries=0` | The number of automatic retries the GET request for node information.
{.nowraptext}`related-item.elastic.search.http.multisearch.endpoint=/_msearch` | The searching endpoint
{.nowraptext}`related-item.elastic.search.http.nodesniffing.endpoint=/_nodes/http` | The endpoint used for sniffing nodes added to the cluster
{.nowraptext}`related-item.elastic.search.http.nodesniffing.retry.interval=15` | The amount of time between runs for sniffing the cluster for any newly added elasticsearch nodes
{.nowraptext}`related-item.elastic.search.http.nodesniffing.retry.interval.unit=mins` | The duration (minutes) for which the interval applies: days,hours,mins,secs
{.nowraptext}`related-item.elastic.search.http.nodesniffing.enabled=true` | If node sniffing is enabled or not.


