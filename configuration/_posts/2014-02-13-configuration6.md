---
is_for_config : true
page_no : 6
date : 2014-02-10
category : configuration
title : Tomcat
desc: server.xml configuration for tomcat 7 for running the indexing and searching webapplication 
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

An example of a full configuration file would be:

	<Server port="8005" shutdown="SHUTDOWN">	  
	  <!--APR library loader. Documentation at /docs/apr.html -->
	  <Listener className="org.apache.catalina.core.AprLifecycleListener" SSLEngine="on" />
	  <!--Initialize Jasper prior to webapps are loaded. Documentation at /docs/jasper-howto.html -->
	  <Listener className="org.apache.catalina.core.JasperListener" />
	  <!-- Prevent memory leaks due to use of particular java/javax APIs-->
	  <Listener className="org.apache.catalina.core.JreMemoryLeakPreventionListener" />
	  <Listener className="org.apache.catalina.mbeans.GlobalResourcesLifecycleListener" />
	  <Listener className="org.apache.catalina.core.ThreadLocalLeakPreventionListener" />

	  <!-- Global JNDI resources
	       Documentation at /docs/jndi-resources-howto.html
	  -->
	  <GlobalNamingResources>
	    <!-- Editable user database that can also be used by
	         UserDatabaseRealm to authenticate users
	    -->
	    <Resource name="UserDatabase" auth="Container"
	              type="org.apache.catalina.UserDatabase"
	              description="User database that can be updated and saved"
	              factory="org.apache.catalina.users.MemoryUserDatabaseFactory"
	              pathname="conf/tomcat-users.xml" />
	  </GlobalNamingResources>

	  <!-- A "Service" is a collection of one or more "Connectors" that share
	       a single "Container" Note:  A "Service" is not itself a "Container",
	       so you may not define subcomponents such as "Valves" at this level.
	       Documentation at /docs/config/service.html
	   -->
	  <Service name="Catalina">


	    <!-- A "Connector" represents an endpoint by which requests are received
	         and responses are returned. Documentation at :
	         Java HTTP Connector: /docs/config/http.html (blocking & non-blocking)
	         Java AJP  Connector: /docs/config/ajp.html
	         APR (HTTP/AJP) Connector: /docs/apr.html
	         Define a non-SSL HTTP/1.1 Connector on port 8080
	    -->
	    <Connector port="8080" protocol="org.apache.coyote.http11.Http11NioProtocol"
	            connectionTimeout="60000" processorCache="20000" maxKeepAliveRequests="10000" 
	            acceptorThreadCount="1" socket.rxBufSize="4096" socket.txBufSize="4096" 
	            socket.tcpNoDelay="true" acceptCount="1024"
	            redirectPort="8443" maxThreads="8" asyncTimeout="120000"/>
	            
	    

	    <!-- An Engine represents the entry point (within Catalina) that processes
	         every request.  The Engine implementation for Tomcat stand alone
	         analyzes the HTTP headers included with the request, and passes them
	         on to the appropriate Host (virtual host).
	         Documentation at /docs/config/engine.html -->

	    <Engine name="Catalina" defaultHost="localhost">

	      <!-- Use the LockOutRealm to prevent attempts to guess user passwords
	           via a brute-force attack -->
	      <Realm className="org.apache.catalina.realm.LockOutRealm">
	        <!-- This Realm uses the UserDatabase configured in the global JNDI
	             resources under the key "UserDatabase".  Any edits
	             that are performed against this UserDatabase are immediately
	             available for use by the Realm.  -->
	        <Realm className="org.apache.catalina.realm.UserDatabaseRealm"
	               resourceName="UserDatabase"/>
	      </Realm>

	      <Host name="localhost"  appBase="webapps"
	            unpackWARs="true" autoDeploy="false">
	      </Host>
	    </Engine>
	  </Service>
	</Server>               