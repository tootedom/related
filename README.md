Indexing info

'''
curl -XPUT http://macmini:9200/_template/relatedproducts -d '{
    "template" : "relatedproducts*",
    "settings" : {
        "number_of_shards" : 1,
        "number_of_replicas" : 1,
        "index.cache.field.type" : "soft",
        "index.refresh_interval" : "5s",
        "index.store.compress.stored" : true,
        "index.query.default_field" : "id",
        "index.routing.allocation.total_shards_per_node" : 1
    },
    "mappings" : {
        "relatedproduct" : {
           "_all" : {"enabled" : false},
           "dynamic" : false,
           "properties" : {
              "id": { "type": "string", "index": "not_analyzed" },
              "related-with": { "type": "string", "index": "not_analyzed" },
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
cluster.name: relatedproducts

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
   "products":[
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

'''
curl -H"Content-Type:text/json" -XPOST -v http://localhost:8080/indexing/index -d '{ "channel" : "uk", "site" : "amazon", "date" : "2013-05-22T20:31:35", "products" : [ { "id" : "111","type":"coat"}, { "id" : "123","type":"socks"}, { "id" : "23334","type":"button"} ]  }'
'''

Sample indexing configuration params:

'''
-Drelated-product.wait.strategy=busy -Drelated-product.size.of.incoming.request.queue=131072 -Drelated-product.number.of.indexing.request.processors=8 -Drelated-product.index.batch.size=125 -Drelated-product.elastic.search.transport.hosts=10.0.1.19:9300
'''


---

Sample search request
'''
http://10.0.1.29:8080/searching/frequentlyrelatedto/8855?channel=uk

curl http://localhost:8080/searching/frequentlyrelatedto/123?channel=uk

'''


in elastic this would be:

'''
curl -XPOST http://macmini:9200/relatedproducts*/relatedproduct/_search -d '
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