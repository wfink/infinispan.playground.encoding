# infinispan.playground.encoding
Infinispan Marshalling and Encoding examples
===============================

Author: Wolf-Dieter Fink
Technologies: Infinispan, Hot Rod, Protobuf


What is it?
-----------

Different examples how to use the recommended server side encoding with protobuf and the clients

With encoding the cache content can be converted if different clients are used, the server will convert some of it automatically if possible.
But the only encoding which fully supports the power of Infinispan is Protostream.
Also Protostream is highly optimized to 
Hot Rod is a binary TCP client-server protocol. The Hot Rod protocol facilitates faster client and server interactions in comparison to other text based protocols and allows clients to make decisions about load balancing, failover and data location operations.


Preparation
-------------
Follow the README from teh root project folder to prepare a server instance.
Add the necessary cache by using the template and CLI

        create cache --file=../template/JBossMarshalledCache.xml JBossMarshalledCache


Build and Run the example
-------------------------

1. Type this command or an IDE to start the simple example class JBossMarshallingClient

        mvn exec:java

    The JavaJBossMarshalledCache will use the application/x-jboss-marshalling encoding.
    Objects are stored as byte[] in the server, to access the encoded cache the GenericJBossMarshaller must be used, other like JavaSerialization will not work
    as the content can not migrated.

    To read data via REST the following command can be used:

      curl --output - http://127.0.0.1:11222/rest/v2/caches/JavaSerializedCache/s1
      curl -H "Accept: application/x-jboss-marshalling" --output - http://127.0.0.1:11222/rest/v2/caches/JavaSerializedCache/s1

   Both will return the same result --output is necessary as the output can mess up the terminal!

   Conversion to plain text is the only alternative, all others will be rejected with ISPN120007.

      curl -H "Accept: text/plain" http://127.0.0.1:11222/rest/v2/caches/JBossMarshalledCache/s1

   But this will show the class name only.
