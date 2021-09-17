# infinispan.playground.encoding
Infinispan Marshalling and Encoding examples
===============================

Author: Wolf-Dieter Fink
Technologies: Infinispan, Hot Rod, Protobuf, JBossMarshalling


What is it?
-----------

Different examples how to use the recommended server side encoding with protobuf and clients

With encoding the cache content can be converted if different clients are used, the server will convert some of it automatically if possible.
The only encoding which fully supports the power of Infinispan is Protostream.
Also Protostream is highly optimized to provide the best performance

Hot Rod is a binary TCP client-server protocol. The Hot Rod protocol facilitates faster client and server interactions in comparison to other text based protocols and allows clients to make decisions about load balancing, failover and data location operations.


Prepare a server instance
-------------
Simple start a Infinispan 11+ or RHDG 8+ server and follow the examples below.
To keep the client simple you need to remove the 'security-realm' from the endpoints element, as well as the authorization.

Type this command to build the projects

        mvn clean package


You might create all the caches directly using CLI

        ./bin/cli.sh -c localhost:11222
          create cache --file=template/ProtobufMessageCache.xml ProtobufMessageCache
          create cache --file=template/ProtobufLibraryCache.xml LibraryCache
          create cache --file=template/NoEncodingCache.xml NoEncodingCache
          create cache --file=template/JavaSerializedCache.xml JavaSerializedCache
          create cache --file=template/JavaObjectCache.xml JavaObjectCache
          create cache --file=template/JBossMarshalledCache.xml JBossMarshalledCache
          create cache --file=template/ProtobufSimpleEntryCache.xml ProtobufSimpleEntryCache
          create cache --file=template/ProtobufCustomTypeEntryCache.xml CustomTypeCache


Examples
--------

 Follow the individual README for each example

 Note: for most of the caches the console http://localhost:11222 can be used to show cache content by navigating to the CacheContainer->{CacheName}



Protobuf marshalling

  ProtoBuf is a simple project which uses the API to extract the schema and register it.
  A second example will add some objects to the cache and iterate or use Ickle query to find some entries.
  As well as using REST.

  ProtoBufMessage TODO  show the old deprecated way to marshall custom objects.

  ProtoBufAdaptor is a project to show how the new Marshalling with ProtoAdaptor annotation introduced with Infinispan 12 works to be able to marshall custom classes
  which are not supported by Protostream out of the box.
  This Adaptor annotation and the processing will generate the Marshalling code and with AutoProtoSchemaBuilder the registration is done.
  The MessageMarshalling interface is now deprecated and might be removed in a future version.


NoEncoding

  The cache does not use encoding.

JBossMarshalling

  Cache use JBossMarshalling and the related encoding.
  
  Note: this might not compile because the use of JBossMarshaller is deprecated and might be removed in future versions.

Java Serialization

  Cache uses simple Java serialization and the related encoding.
  This is not recommended as the serialization will have a worse performance compared with others.

Java Object

  Cache uses encoding for java-objects and show how it works.
  It will be not compatible with other type of (none java) clients and other Marshallers







QUESTIONS 
- auslesen von protobuf objekten als json oder xml
- konvertieren von objekten ?
- anlegen von proto def mit REST
- Java Objekte schreiben und lesen per REST?
