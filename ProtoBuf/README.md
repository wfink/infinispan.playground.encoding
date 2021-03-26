# infinispan.playground.encoding
Infinispan Marshalling and Encoding examples
===============================

Author: Wolf-Dieter Fink
Technologies: Infinispan, Hot Rod, Protobuf


What is it?
-----------

This example will show how the HotRod client and REST can access Protobuf objects from an encoded cache. It will use also simple Ickle queries for demonstration.

This approach is recommended as it will have probably the best performance as the ProtoStream marshalling is highly optimized.
Also it allows different type of clients to access the remote cache, like HotRod (Java C++ C#) node.js and REST.
The content can be converted for each client, the server will handle this.


Preparation
-------------
Follow the README from teh root project folder to prepare a server instance.
Add the necessary cache by using the template and CLI

        create cache --file=../template/ProtobufMessageCache.xml ProtobufMessageCache


Run the example
-------------------------

   Type this command or an IDE to start the simple example class RegisterProtobufClient

        mvn exec:java -D"exec.mainClass"="org.infinispan.wfink.playground.encoding.hotrod.RegisterProtobufClient"

     The client will use the ProtoSchemaBuilder API to extract the information from the Message class @ProtoDoc and other @Proto annotations to register the 
     message object as playground.message.

     Other option is to use the prepared message.proto file and add it with a REST command

        curl -X POST --data-binary @../template/message.proto http://127.0.0.1:11222/rest/v2/caches/___protobuf_metadata/message.proto

     Both will ensure that the server is aware of the object, otherwise Ickle queries are not possible and will fail with a client error

        WARN: ISPN004005: Error received from the server: java.lang.IllegalArgumentException: Unknown type name : playground.Message
        ICKLE QUERY FAILURE : java.lang.IllegalArgumentException: Unknown type name : playground.Message

     REST request will fail as well with "Unknown type name : playground.Message" as the server is not able to convert the cached object to a readable json format.



   Use java command or an IDE to start the simple example class ProtobufMessageClient

        mvn exec:java -D"exec.mainClass"="org.infinispan.wfink.playground.encoding.hotrod.ProtobufMessageClient"

     This example use a simple String key with the Message.id to store different messages.
     The client will add some Messages and use Ickle queries to demonstrate the use.
        Note! if the protop schema is not registered the queries and following access with REST will not work as the server does not have the proto schema!

   access Data with REST client

      curl [-X GET] http://127.0.0.1:11222/rest/v2/caches/ProtobufMessageCache/4

        returns the protobuf Message as json object

      curl -H "Accept: application/x-protostream" --output -  http://127.0.0.1:11222/rest/v2/caches/ProtobufMessageCache/1

        returns the protobuf Message as binary protobuf, this could mess up the terminal!

      curl -H "Accept: text/plain" http://127.0.0.1:11222/rest/v2/caches/ProtobufMessageCache/1

        returns a failure "No marshaller registered..." as this convertion is not supported

      curl -X POST --data-binary @./template/message100.json -H "Content-Type: application/json" http://127.0.0.1:11222/rest/v2/caches/ProtobufMessageCache/100

        add the Message entry 100 to the cache from the file message100.json


   Pitfall with key types

     If the cache definition is <Object, Message> it is possible to add entries with put(1,...) or put("1",...)
     in that case the result of a REST request above will not find an entry for /ProtobufMessageCache/1 as REST will use the "1" as String,
     the header can be used to tell REST the key is an Integer

       curl -H "Key-Content-Type: application/x-java-object;type=java.lang.Integer" http://127.0.0.1:11222/rest/v2/caches/ProtobufMessageCache/1

     Adding the message100 like this

       curl -X POST --data-binary @./template/message100.json -H "Key-Content-Type: application/x-java-object;type=java.lang.Integer" -H "Content-Type: application/json" http://127.0.0.1:11222/rest/v2/caches/ProtobufMessageCache/100

     will have the same result, but consider executing ProtobufMessageClient will show the message iterating the cache without ClassCastException, but will not find it with get("100")

