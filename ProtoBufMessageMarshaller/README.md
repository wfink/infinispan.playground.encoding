# infinispan.playground.encoding
Infinispan Marshalling and Encoding examples
===============================

Author: Wolf-Dieter Fink
Technologies: Infinispan, Hot Rod, Protobuf/ProtoStream


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

Build the project with 

        mvn clean package


Run the example
-------------------------

   Type this command or an IDE to start the simple example class LibraryClient

        mvn exec:java

     The client will use the @Proto annotated classes to initialize the client schema and the provided Marshallers to handle marshalling.
     
   access Data with REST client

      curl [-X GET] http://127.0.0.1:11222/rest/v2/caches/LibraryCache/4

        will fail with an "Unknown type name : playground.Book" as long as the schema is registered, otherwise it returns the protobuf Book as json object
        The schema can be registered by using the client, change the constructor of LibraryClient in main() to push the schema to the server,
        or register the schema manually with REST

          curl -X POST --data-binary @./schema/library.proto http://127.0.0.1:11222/rest/v2/caches/___protobuf_metadata/library.proto


      curl -H "Accept: application/x-protostream" --output -  http://127.0.0.1:11222/rest/v2/caches/LibraryCache/1

        returns the protobuf Message as binary protobuf message, note this could mess up the terminal!
