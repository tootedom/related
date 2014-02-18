---
is_indexing_overview : true
title: Indexing
sitemap_include: false
---

The index web application is POSTed data containing a group of related items, i.e. for example a purchase containing several items.  

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
          },
          {
             "id":"4",
             "type":"torch",
             "channel":"uk"
          }
       ]
    }'

 