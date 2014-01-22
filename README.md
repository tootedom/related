# Relateit #

Relateit is a simple and easy way to relate one item to several others.  Once several items are related to other items you can enquire:  For this item, what are the most frequently related items.  

This can be used on a web site to associate the items the people are purchasing.  If a person purchases the books 'Java Performance Tuning', 'Akka Concurrency' and 'Java concurrency in practice' at the same time.  When a second user is browsing 'Java Performance Tuning', you can present that user with related items that this book is frequently purchased with.

This application has 3 parts to it:

* A Indexing Web Application
* A Searching Web Application
* [Elasticsearch](http://www.elasticsearch.org/ "Elasticsearch") backend

The indexing and searching components make use of the [Disruptor](https://github.com/LMAX-Exchange/disruptor "Disruptor") library.  Whilst the Indexing and Searching components do not need to directly be used, they provide a means of batching indexing and searching requests.

The index web application is POSTed data contain the related items:

    curl -H"Content-Type:text/json" -XPOST -v http://localhost:8080/indexing/index -d '
    {
       "channel":"de",
       "site":"amazon",
       "items":[
          {
             "id":"1",
             "type":"map"
          },
          {
             "id":"2",
             "type":"compass"
          },
          {
             "id":"3",
             "type":"torch"
          }
       ]
    }'

The indexing application returns a 202 accepted status code.  Indicating that the request has been accepted for processing and indexing.  At which point the POSTed data is assembled into several documents.  For example for the above request, 3 JSON documents will be assembled and submitted to elastic search for storage and indexing:

    {
        "id": "1" ,
        "date": "2013-12-24T17:44:41.943Z",
        "related-with": [ "2","3"],
        "type": "map",
        "site": "amazon",
        "channel": "de"
    }

    {
        "id": "2" ,
        "date": "2013-12-24T17:44:41.943Z",
        "related-with": [ "1","3"],
        "type": "compass",
        "site": "amazon",
        "channel": "de"
    }

    {
        "id": "3" ,
        "date": "2013-12-24T17:44:41.943Z",
        "related-with": [ "1","2"],
        "type": "torch",
        "site": "amazon",
        "channel": "de"
    }

The above 

The search web application is then called to request the frequently related items for a product (a GET request):

    curl -v -N http://10.0.1.29:8080/searching/frequentlyrelatedto/1?channel=uk

Which returns json data containing the list of items (their id's) that are frequently related to that item




#Indexing info


    HOST=localhost
    curl -XPUT http://$HOST:9200/_template/relateditems -d '{
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
                  "date": { "type": "date", "index": "not_analyzed" },
                  "channel" : {"type" : "string" , "index" : "not_analyzed" },
                  "site" : {"type" : "string" , "index" : "not_analyzed" },
                  "type" : {"type" : "string" , "index" : "not_analyzed" }
               }
            }
        }
    }'




'''

Elastic config
'''
cluster.name: relateditems

# Search pool
threadpool.search.type: fixed
threadpool.search.size: 20
threadpool.search.queue_size: 1000000

# Bulk pool
threadpool.bulk.type: fixed
threadpool.bulk.size: 25
threadpool.bulk.queue_size: 1000000

threadpool.get.type: fixed
threadpool.get.size: 1
threadpool.get.queue_size: 1


# Index pool
threadpool.index.type: fixed
threadpool.index.size: 20
threadpool.index.queue_size: 1000000
'''




Sample indexing json

'''
{
   "channel":"de",
   "site":"amazon",
   "items":[
      {
         "id":"1",
         "type":"map"
      },
      {
         "id":"2",
         "type":"compass"
      },
      {
         "id":"3",
         "type":"torch"
      }
   ]
}
'''


Sample curl request for indexing:


    curl -H"Content-Type:text/json" -XPOST -v http://localhost:8080/indexing/index -d         '{ "channel" : "uk", "site" : "amazon", "date" : "2013-05-22T20:31:35", "items" : [ { "id" :     "111","type":"coat"}, { "id" : "123","type":"socks"}, { "id" : "23334","type":"button"} ]  }'


Sample indexing configuration params:

'''
-Drelated-product.wait.strategy=busy -Drelated-product.size.of.incoming.request.queue=131072 -Drelated-product.number.of.indexing.request.processors=8 -Drelated-product.index.batch.size=125 -Drelated-product.elastic.search.transport.hosts=10.0.1.19:9300
'''


---

Sample search request

    curl -v -N http://10.0.1.29:8080/searching/frequentlyrelatedto/8855?channel=uk

    curl -v -N http://localhost:8080/searching/frequentlyrelatedto/123?channel=uk



in elastic this would be:

'''
curl -XPOST http://macmini:9200/relateditemss*/relateditem/_search -d '
{
  "query" :
        {
            "bool" : {
                "must" : [
                    {"field" : {"id" : "338906"} },
                    {"field" : {"channel" : "uk"} },
                    {"field" : {"site" : "amazon"} }
                ]
            }
        },
        "facets" : {
            "frequently-related-with" : {
                "terms" : {"field" : "related-with", "size" : 5 }
            }
        },
        "size":0
}
'
'''