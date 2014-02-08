---
is_main_overview: true
title: 
---
**Relate it** is a simple and easy way to relate one item to several others.  Once several items are related, you can enquire:  "For this item, what are the most frequently related items."

An example use for this functionality is on a web site to create relations between the items the people are purchasing, and presenting users browsing those items a "Frequently Purchased With" section.

For example, Imagine a person purchases the books: 'Java Performance Tuning', 'Akka Concurrency' and 'Java concurrency in Practice' at the same time.  When future user is browsing 'Java Performance Tuning', you can present that user with a set related items that this book is most "Frequently Purchased With".  

This application has 3 parts to it:

* A Indexing Web Application (Java)
* A Searching Web Application (Java)
* [Elasticsearch](http://www.elasticsearch.org/ "Elasticsearch") backend

The indexing and searching components (web applications) make use of the [Disruptor](https://github.com/LMAX-Exchange/disruptor "Disruptor") library.  

The search technology Elasticsearch provides the storage, and searching mechanism for providing the related product search.

The Indexing and Searching components do not need to directly be used.  In other words you can just post data in the relevant format into elasticsearch, and then perform searching directly against elasticsearch to obtain the most frequently related items.   However, it is the Indexing and Searching components that provide a means of batching indexing and searching requests that are being send to elasticsearch.

----

## Requirements ##


* JDK 7 (recommended jdk7u40+), Java Web Application Server with Servlet 3 (i.e. Tomcat 7+) and Elasticsearch 0.90.9 

