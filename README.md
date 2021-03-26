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


Prepare a server instance
-------------
Simple start a Infinispan 11+ or RHDG 8+ server and follow the examples below.
To keep the client simple you need to remove the 'security-realm' from the endpoints element.

Build and Run the example
-------------------------
1. Start a Infninispan 11+ or RHDG 8+ server

2. Add the necessary caches by using the templates

        ./bin/cli.sh -c localhost:11222
        >  create cache --file=template/ProtobufMessageCache.xml ProtobufMessageCache
        >  create cache --file=template/JavaSerializedCache.xml JavaSerializedCache
        >  create cache --file=template/NoEncodingCache.xml NoEncodingCache
           ! note NoEncodingCache will show a WARN message in the server

3. Type this command to build and deploy the archive:

        mvn clean package

4. Use java command or an IDE to start a simple example

   RegisterProtobufSchemaClient

     This will add the proto schema used with this examples to the server.
     Note this is needed only once for the server, even if restarted, as the schemas are persistent.

5. Start a simple example with protostream objects and Ickl queries

   ProtobufMessageClient
 
     This example use a simple String key with the Message.id to store different messages.
     The client will add some Messages to the cache to demonstrate the use.
     Note! if step 4 is missing the queries and following access with REST will not work as the server does not have the proto schema!

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


6. Cache without encoding

    run NoEncodingHotRodClient  It will use Object/Object to insert Integer/String entries with no Marshalling configured. This will use the default ProtoStreamMarshaller.

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



7. Cache with Java Objects

    The JavaObjectCache will use the application/x-java-object encoding.
    To be able to use it the server must have the configuration for serialization white-list and the Java class added to server/lib (ATM only ./lib works!!)
    Copy the Domain/target/Encoding-Domain.jar to the server lib directory.
    Change the server/conf/infinispan.xml and add the following to the <cache-container>

        <serialization>
          <white-list>
            <regex>org.infinispan.wfink.playground.*</regex>
          </white-list>
        </serialization>

    The Java class added with the JavaObjectClient can be read with REST using different formats

     curl -H "Accept: text/plain" http://127.0.0.1:11222/rest/v2/caches/JavaObjectCache/1
       -> SimplePOJO [firstValue=Entry, secondValue=1]
       this will be the String representation, also the text/plain is the default and must not be added as header.

     curl -H "Accept: application/json" http://127.0.0.1:11222/rest/v2/caches/JavaObjectCache/1
       -> {"_type":"org.infinispan.wfink.playground.encoding.hotrod.domain.SimplePOJO","firstValue":"Entry","secondValue":"1"}

     curl -H "Accept: application/xml" http://127.0.0.1:11222/rest/v2/caches/JavaObjectCache/1
       -> <?xml version="1.0" ?><org.infinispan.wfink.playground.encoding.domain.SimplePOJO><firstValue>Entry</firstValue><secondValue>1</secondValue></org.infinispan.wfink.playground.encoding.domain.SimplePOJO>


8. Cache with application/x-java-serialized-object

    Run the JavaSerializationClient.
    The JavaSerializedCache will use the application/x-java-object encoding.
    Objects are stored as byte[] in the server, the cache can only be accesses if the client use the JavaSerializationMarshaller!

      curl -H "Key-Content-Type: application/x-java-object;type=java.lang.String" --output - http://127.0.0.1:11222/rest/v2/caches/JavaSerializedCache/s1
       -> returns the class as byte[]
          the --output - is needed to force it as this could mess up the terminal!

    Conversion to other output like json or xml is not possible. TODO could it be customized?

    TODO why the Integer key is not readable?


9. Cache with application/x-jboss-marshalling

    Run the JBossMarshallingClient.
    The JavaJBossMarshalledCache will use the application/x-jboss-marshalling encoding.
    Objects are stored as byte[] in the server, to access the encoded cache the GenericJBossMarshaller must be used, other like JavaSerialization will not work
    as the content can not migrated.

    To read data vie REST the following command can be used:

      curl --output - http://127.0.0.1:11222/rest/v2/caches/JavaSerializedCache/s1
      curl -H "Accept: application/x-jboss-marshalling" --output - http://127.0.0.1:11222/rest/v2/caches/JavaSerializedCache/s1

   Both will return the same result --output is necessary as the output can mess up the terminal!

   Conversion to plain text is the only alternative, all others will be rejected with ISPN120007.

      curl -H "Accept: text/plain" http://127.0.0.1:11222/rest/v2/caches/JBossMarshalledCache/s1

   But this will show the class name only.


Note: for most of the caches the console http://localhost:11222 can be used to show cache content by navigating to the CacheContainer->{CacheName}


QUESTIONS 
- auslesen von protobuf objekten als json oder xml
- konvertieren von objekten ?
- anlegen von proto def mit REST
- Java Objekte schreiben und lesen per REST?
- Marshaller für JBM und JavaSerialization
