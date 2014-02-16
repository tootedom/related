---
is_for_config : true
page_no : 6
date : 2014-02-10
category : configuration
title : Tomcat
categories : 
- configuration
---

The Searching and Indexing Web Applications have been tested on tomcat version `7.0.42` with the NIO Protocol.
The tomcat was running on a 2 cpu (Intel(R) Xeon(R) CPU E5-2407 0 @ 2.20GHz), (4 cores each) server.  

The NIO Configuration was as follows:

    <Connector port="8080" protocol="org.apache.coyote.http11.Http11NioProtocol"
               connectionTimeout="60000" processorCache="20000" 
               maxKeepAliveRequests="10000" acceptorThreadCount="1" 
               socket.rxBufSize="4096" socket.txBufSize="4096" 
               socket.tcpNoDelay="true" acceptCount="1024"
               redirectPort="8443" maxThreads="8" asyncTimeout="120000"/>