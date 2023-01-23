G# infinispan.playground.encoding
Infinispan Marshalling and Encoding examples
===============================

Author: Wolf-Dieter Fink
Technologies: Infinispan, Hot Rod, Protobuf


What is it?
-----------

This example will show how the HotRod client and REST can access Protobuf objects from an encoded cache.

It has been introduced with Infinispan 12 and will replace the MessageMarshaller interface and API because of the performance improvements.

This approach is recommended as it will have probably the best performance as the ProtoStream marshalling is highly optimized.
Also it allows different type of clients to access the remote cache, like HotRod (Java C++ C#) node.js and REST.
The content can be converted for each client, the server will handle this.

It uses a UUID class which is not supported out of the box from protostream and implemented by ProtoAdapter.
Also it will show how Lists or Sets can be used in custom objects and ensure the correct implementation is used, see Book.authors.


Preparation
-------------
Follow the README from the root project folder to prepare a server instance.
Add the necessary cache by using the template and CLI

        create cache --file=../template/ProtobufLibraryCache.xml LibraryCache

if necessary
        create cache --file=../template/ProtobufSimpleEntryCache.xml SimpleEntryCache
        create cache --file=../template/ProtobufCustomTypeEntryCache.xml CustomTypeCache


Run the example
-------------------------

   Type this command or an IDE to start the simple example class LibraryClient

        mvn exec:java -Dexec.mainClass="org.infinispan.wfink.playground.encoding.hotrod.LibraryClient"

     The client will use the generated classes to marshall the UUID class and the @AutoProtoSchemaBuilder annotated LibraryInitializer to have the information and to register the 
     message objects.



   access Data with REST client

      curl [-X GET] http://127.0.0.1:11222/rest/v2/caches/LibraryCache/4

        should return the protobuf Message as json object
        As there is no registered proto schema for the server this will fail
        Until:
         - The proto definition is added to the server with REST or the Management console, this is the recommended approach.
           The definition is located in the root directory and can be added
           i.e. `curl -X PUT --data-binary @library.proto http://127.0.0.1:11222/rest/v2/schemas/library`
         - The domain/target/Encoding-ProtoAdapterDomain.jar is dropped to the ISPN_HOME/server/lib directory
           This will register automatically the schema, without using the proto-schema-cache so all server instances needs the classes
         - The client register the schema when starting (first), uncomment the registerSchema(...) invocation.
           It will add the proto schema to the (persisted) proto-schema-cache clusterwide
           This is not recommened because the schema can be accidentally overriden with any legacy client at runtime.

      curl -H "Accept: application/x-protostream" --output -  http://127.0.0.1:11222/rest/v2/caches/LibraryCache/1

        returns the protobuf Message as binary protobuf, this could mess up the terminal!

      curl -H "Accept: text/plain" http://127.0.0.1:11222/rest/v2/caches/LibraryCache/1

        returns a failure "No marshaller registered..." as this convertion is not supported



Migrated example from the legacy ProtoBufMessageMarshaller implementation
------------------------------------------------------------------

SimpleEntry shows how to migrate a legacy implementation in a compatible way by using @ProtoField annotations and use ProtoBuf message types to ensure a compatible ProtoBuf schema.
As well as the generated Marshaller to use the same implementation for Interfaces or Abstract classes by set a hint which implementation should be used instead of the defaults.

Run the example with

        mvn exec:java -Dexec.mainClass="org.infinispan.wfink.playground.encoding.mm.hotrod.SimpleEntryClient"


CustomTypeEntry shows how to migrate the legacy implementation in two different ways.
1) By using the new approach with embedded messages where only unhandled classes are using a ProtoAdapter. Here the required ProtoAdapter implementation is  for BigInteger is provided by ProtoStream. This is done for Java classes which are used very often to minimize the effort if the user must implement it.

Register the proto schema with REST after the server is started with

        curl -X PUT --data-binary @domain/customtype.proto http://127.0.0.1:11222/rest/v2/schemas/customtype

Run the example with

        mvn exec:java -Dexec.mainClass="org.infinispan.wfink.playground.encoding.mm.hotrod.CustomTypeEntryClient"


2) By using a custom ProtoAdapter implementation for the complete class to have full control about the marshalling process, even if the Marshaller itself is still generated.
But the implementation how fields are marshalled is complete customizable.

Register the proto schema with REST after the server is started with

        curl -X PUT --data-binary @domainWithAdapter/customadapter.proto http://127.0.0.1:11222/rest/v2/schemas/customadapter

HINT: As both examples will use the same CustomTypeEntry, it is mandatory to delete the other customtype definition and clear the cache or restart the server because the serialization is not compatible!

        curl -X DELETE http://127.0.0.1:11222/rest/v2/schemas/customtype


Run the example with

        mvn exec:java -Dexec.mainClass="org.infinispan.wfink.playground.encoding.mm.hotrod.CustomTypeEntryClient"
