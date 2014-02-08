---
is_searching_overview : true
title: Searching
---

Find the items that are mostly purchased with product **1**, filtering the results to that of "torches" (type=torch):

    curl -v -N http://10.0.1.29:8080/searching/frequentlyrelatedto/1?type=torch | python -mjson.tool

The result is:

    {
        "response_time": "11", 
        "results": [
            {
                "frequency": "1", 
                "id": "4"
            }, 
            {
                "frequency": "1", 
                "id": "3"
            }
        ], 
        "size": "2", 
        "storage_response_time": "2"
    }

This can be reduced event further to search for torches, just in channel uk, which is:

    curl -v -N "http://localhost:8080/searching/frequentlyrelatedto/1?type=torch&channel=uk" | python -mjson.tool

Which will result in just the one related item:

    {
        "response_time": 3, 
        "results": [
            {
                "frequency": "1", 
                "id": "4"
            }
        ], 
        "size": "1", 
        "storage_response_time": 1
    }

 