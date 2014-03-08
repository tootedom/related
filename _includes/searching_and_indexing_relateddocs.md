
## Default Searching and Indexing ##

Currently when searching and indexing, the search result will just returns the id's of the related items by default, and the frequency by which it was related with the searched for item it.   It *DOES NOT* return the matching document's information.  

The reason behind not returning the matching documents source, is that there could be literally hundreds of matching documents.  An example would be the dvd `The Raid`.  This could be bought many many times, and with a number of other items; and often with the same related item many times.  

For the below related item POST(s).  The dvd `The Raid`, is associated with both `Enter the dragon` and `kick boxer`.  It is also associated with `kick boxer` twice, and therefore `kick boxer` is the most frequently related item.  

In the backend (elasticsearch), when a relation is stored/indexed there are two actual physical documents that represent `kick boxer` within the `relateitems-*` indexes.  If we are to return all the matching documents for the `most frequently related`, we have to return the source of both `kick boxer` documents from the `relateditems-*` indexes.  Expanding this further you could quite easily have am item related to another item 100's of times; which would mean 100's of matching documents.  It is for this reason the content of that matches are not returned by default.

    curl -H"Content-Type:text/json" -XPOST -v http://localhost:8080/indexing/index -d '
    {
       "channel":"uk",
       "site":"amazon",
       "items":[
          {
             "id":"1",
             "title":"The Raid",
             "type":"dvd"
          },
          {
             "id":"2",
             "title":"Enter the dragon",
             "type":"dvd"           
          }
       ]
    }'

    curl -H"Content-Type:text/json" -XPOST -v http://localhost:8080/indexing/index -d '
    {
       "channel":"uk",
       "site":"amazon",
       "items":[
          {
             "id":"1",
             "title":"The Raid",
             "type":"dvd"
          },
          {
             "id":"2",
             "title":"kick boxer",
             "type":"dvd"           
          }
       ]
    }'
    
    curl -H"Content-Type:text/json" -XPOST -v http://localhost:8080/indexing/index -d '
    {
       "channel":"uk",
       "site":"amazon",
       "items":[
          {
             "id":"1",
             "title":"The Raid",
             "type":"dvd"
          },
          {
             "id":"2",
             "title":"kick boxer",
             "type":"dvd"           
          }
       ]
    }'


## Returning Related Documents ##    


In release `1.1.0`, a new feature has been developed that allows returning of doucments with id's matching the most fequently related item.  
This feature uses an extra index (`relateddocs`) during the index process.  This feature is enabled with the following property being set to true:

    -Drelated-item.document.indexing.enabled=true

With this property set to true, the initial index of the document with the id will be inserted into the `relateddocs` index.  Any changes that may
occur to that document will `NOT` be reflected unless you install a custom plugin (more inforation about this is a moment).

When the indexing application is sent the http request, for example:

    curl -H"Content-Type:text/json" -XPOST -v http://localhost:8080/indexing/index -d '
    {
       "channel":"uk",
       "site":"amazon",
       "items":[
          {
             "id":"B00E391KA8",
             "department":"Computers & Accessories",
             "title":"Samsung 840 EVO 120GB"
          },
          {
             "id":"B004RFDHKO",
             "department":"Computers & Accessories",
             "title":"Dynamode SSD-RAIL"
          }          
       ]
    }'

The indexing application writes to two indexes.  It writes by default to the relate items date based index `relateditems-YYYY-MM-DD`:


    {
        "date": "2013-12-24T17:44:41.943Z",
        "related-with": [ "B00E391KA8" ],
        "site": "amazon",
        "channel": "uk",
        "id":"B004RFDHKO",
        "department":"Computers & Accessories",
        "title":"Dynamode SSD-RAIL"
    }

    {
        "date": "2013-12-24T17:44:41.943Z",
        "related-with": [ "B004RFDHKO" ],
        "site": "amazon",
        "channel": "uk",
        "id":"B00E391KA8",
        "department":"Computers & Accessories".
        "title":"Samsung 840 EVO 120GB"
    }

But it also writes to a secondary index `relateddocs`:

    {
        "site": "amazon",
        "channel": "uk",
        "_id":"B004RFDHKO",
        "department":"Computers & Accessories",
        "title":"Dynamode SSD-RAIL"
    }

    {             
        "site": "amazon",
        "channel": "uk",
        "_id":"B00E391KA8",
        "department":"Computers & Accessories".
        "title":"Samsung 840 EVO 120GB"
    }


When a search in executed, the original facetted search is performed which returns the list of related ids, and the associated frequency; representing the most frequently related item(s).  
Then, with related document indexing enabled, a secondary search would then be issued against the elasticsearch cluster.  This performs a multi get for documents with the given id.  What this means is that the 
`relateddocs` index holds documents based on the `id` field; and there is only one document that exists for a given id.

So for the search query:

    curl -v -N "http://localhost:8080/searching/frequentlyrelatedto/B00E391KA8"

The following results will be returned

    {
       "size":"1",
       "results":[
          {
             "id":"B004RFDHKO",
             "frequency":"1",
             "source":{
                "channel":"uk",
                "department":"Computers & Accessories",
                "site":"amazon",
                "title":"Dynamode SSD-RAIL",
                "sha256":"f317f2f157720b2888d2ccfb12c6488e65653c613345eab275732a444f3ed014"
             }
          }
       ],
       "storage_response_time":"2",
       "response_time":"304"
    }


 In the above results you can see that there is the addtional `source` attribute in the returned document.  If the document does not exist in the `relateddocs` index then the 
 `source` attribute will be empty:

    {
       "size":"1",
       "results":[
          {
             "id":"B004RFDHKO",
             "frequency":"1",
             "source":{                
             }
          }
       ],
       "storage_response_time":"2",
       "response_time":"304"
    }

## Updating Related Documents ##    

As discussed above, when related items are indexed, the related document is indexed in in the `relatedocs` index.  The `_id` of the document is that of the `"id"` field, i.e. B004RFDHKO
When the document is referenced in another relation, then the `relateddocs` index is again send an index request with the data.  If the item didn't exist it would be created.
It any details had been ammended, the will NOT be update.  

In order to have a document subsequently updated; a plugin has to be installed in your elasticsearch cluster.  This plugin allows a document to be updated.  For instance, in the below
The "Samsung 840 EV0 120GB" is relate with a "Torx Set".  However in the below, the Samsung related document has an extra field `description`.  In order for the Samsung document to be updated
`B00E391KA8` you need to install the update plugin.

For example:

    curl -H"Content-Type:text/json" -XPOST -v http://localhost:8080/indexing/index -d '
    {
       "channel":"uk",
       "site":"amazon",
       "items":[
          {
             "id":"B00E391KA8",
             "department":"Computers & Accessories",
             "title":"Samsung 840 EVO 120GB",
             "description":"2.5 inch Basic SATA Solid State Drive"
          },
          {
             "id":"B0000934GO",
             "department":"Computers & Accessories",
             "title":"Torx Set"
          }          
       ]
    }' 

Then issue the search request:    

    curl -v -N "http://localhost:8080/searching/frequentlyrelatedto/B0000934GO"

The results show the addition of the `description` attribute:

    {
       "size":"1",
       "results":[
          {
             "id":"B00E391KA8",
             "frequency":"1",
             "source":{
                "channel":"uk",
                "department":"Computers & Accessories",
                "site":"amazon",
                "title":"Samsung 840 EVO 120GB",
                "sha256":"2600849b237aa8b3fe8d160321bd8cc13d0041019501953fd5f3985cb5dc88d2",
                "description":"2.5 inch Basic SATA Solid Stat"
             }
          }
       ],
       "storage_response_time":"2",
       "response_time":"3"
    }


Without the plugin the document update will to the `relateddoc` will not occur.  Don't worry the origin indexing/recording of frequencies will still occur, so 
finding the frequently related items will all work.  All that will not occur, without the plugin enabled, is updates to the `relateddocs` document.   The updates uses
the elasticsearch updates api and the upsert mechanism to install documents.

### Installing the Plugin ### 

To install the plugin, you need to download the plugin from:

    https://oss.sonatype.org/content/repositories/releases/org/greencheek/related/plugins/relateddocs-merger/1.0.0/relateddocs-merger-1.0.0.jar

And install it into your elasticsearch cluster in the `ES_HOME/lib` directory, or into your plugins dir.  Then you enable the plugin in your elasticsearch cluster by adding the following to your
`elasticsearch.yaml`:

    script.native.relateddocupdater.type: org.greencheek.related.plugins.relateddocsmerger.RelatedDocsMergerFactory

The plugin source code can be found at:

    https://github.com/tootedom/related-esplugins  