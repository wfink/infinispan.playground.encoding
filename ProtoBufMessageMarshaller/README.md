# infinispan.playground.encoding
Infinispan Marshalling and Encoding examples
===============================

Author: Wolf-Dieter Fink
Technologies: Infinispan, Hot Rod, Protobuf


What is it?
-----------

This example will show how the HotRod client and REST can access Protobuf objects from an encoded cache.
It uses a UUID class which is not supported out of the box from protostream.

The MessageMarshaller interface and API is deprecated from Infinispan 11 and replaced by @ProtoAdaptor in Infinispan 12.

It allows different type of clients to access the remote cache, like HotRod (Java C++ C#) node.js and REST.
The content can be converted for each client, the server will handle this.


Preparation
-------------
Follow the README from the root project folder to prepare a server instance.
Add the necessary cache by using the template and CLI

        create cache --file=../template/ProtobufLibraryCache.xml LibraryCache


Run the example
-------------------------

   Type this command or an IDE to start the simple example class LibraryClient

        mvn exec:java

     The client will use the generated classes to marshall the UUID class and the @AutoProtoSchema annotated LibraryInitializer to have the information and to register the 
     message objects.


    TODO  !!!!! 

   access Data with REST client

      curl [-X GET] http://127.0.0.1:11222/rest/v2/caches/LibraryCache/4

        returns the protobuf Book as json object

      curl -H "Accept: application/x-protostream" --output -  http://127.0.0.1:11222/rest/v2/caches/LibraryCache/1

        returns the protobuf Message as binary protobuf, this could mess up the terminal!
