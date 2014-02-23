---
is_for_config : true
page_no : 4
date : 2014-02-12
category : configuration
title : Webapp JVM
desc : JVM settings for the indexing and searching Web Applications, based on the defaults out of the box configuration.
categories : 
- configuration
---

The default configuration for indexing and searching applications (as shown below) 
are based on a 1GB heap (`-Xmx1024m -Xms1024m`) configuration.  It is for this default configuration
that the below JVM options and Heap configuration is specified.

The specific recommended (tested against) JVM options for searching and indexing
are listed below (jdk7 - the following options *WILL NOT* work on jdk6).
The JVM options slightly differ between searching and indexing.  The common options
are listed and then the differences listed:

### Common options for both Web Applications

    -XX:CMSInitiatingOccupancyFraction=85
    -XX:MaxTenuringThreshold=15
    -XX:CMSWaitDuration=70000    
    -XX:MaxPermSize=128m
    -XX:ParGCCardsPerStrideChunk=4096
    -XX:+UseParNewGC
    -XX:+UseConcMarkSweepGC
    -XX:+UseCMSInitiatingOccupancyOnly    
    -XX:+UnlockDiagnosticVMOptions
    -XX:+AggressiveOpts
    -XX:+UseCondCardMark

----
Below shows the heap configuration for indexing and search.  The difference between
the two is that of the eden space.

### Searching Heap ###

    -Xmx1024m
    -Xmn700m
    -Xms1024m
    -Xss256k

### Indexing Heap ###

    -Xmx1024m
    -Xmn256m
    -Xms1024m
    -Xss256k
