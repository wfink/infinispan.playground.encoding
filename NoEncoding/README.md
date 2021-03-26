# infinispan.playground.encoding
Infinispan Marshalling and Encoding examples
============================================

Author: Wolf-Dieter Fink
Technologies: Infinispan, Hot Rod


What is it?
-----------

Different examples how to use the recommended server side encoding with protobuf and the clients

With encoding the cache content can be converted if different clients are used, the server will convert some of it automatically if possible.
But the only encoding which fully supports the power of Infinispan is Protostream.
Also Protostream is highly optimized to 
Hot Rod is a binary TCP client-server protocol. The Hot Rod protocol facilitates faster client and server interactions in comparison to other text based protocols and allows clients to make decisions about load balancing, failover and data location operations.


Preparation
-------------
Follow the README from the root project folder to prepare a server instance.
Add the necessary cache by using the template and CLI

        create cache --file=../template/NoEncodingCache.xml NoEncodingCache

 ! note NoEncodingCache will show a WARN message in the server


Build and Run the example
-------------------------

1. Type this command or an IDE to start the simple example class NoEncodingClient

        mvn exec:java

   It will use Object/Object to insert Integer/String entries with no Marshalling configured. This will use the default ProtoStreamMarshaller.

    check with REST

     curl http://127.0.0.1:11222/rest/v2/caches/NoEncodingCache/1
     curl http://127.0.0.1:11222/rest/v2/caches/NoEncodingCache/x
     curl -H "Key-Content-Type: application/x-java-object;type=java.lang.Integer" http://127.0.0.1:11222/rest/v2/caches/NoEncodingCache/1

   none of it will return an entry as the key is stored completely different
   TODO -> Is it possible to read the entries with byte[] format?

   restart server to clear the cache and add content with REST
     curl -d "REST Entry#1" -H "Content-Type: application/octet-stream" http://127.0.0.1:11222/rest/v2/caches/NoEncodingCache/1
     curl -d "REST Entry#2" -H "Key-Content-Type: application/x-java-object;type=java.lang.Integer" -H "Content-Type: application/octet-stream" http://127.0.0.1:11222/rest/v2/caches/NoEncodingCache/2

   this could not be read with

     curl http://127.0.0.1:11222/rest/v2/caches/NoEncodingCache/1
     curl -H "Key-Content-Type: application/x-java-object;type=java.lang.Integer" http://127.0.0.1:11222/rest/v2/caches/NoEncodingCache/2

   Note the cache without encoding is difficult to handle and the content must be octet-stream.
