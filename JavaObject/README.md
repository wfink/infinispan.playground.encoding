# infinispan.playground.encoding
Infinispan Marshalling and Encoding examples
===============================

Author: Wolf-Dieter Fink
Technologies: Infinispan, Hot Rod


What is it?
-----------

This example will show how the HotRod client and REST can read Java objects from an encoded cache

Use of Java object is not recommeded because of incompatibility with other languages.


Preparation
-------------
Follow the README from teh root project folder to prepare a server instance.
Add the necessary cache by using the template and CLI

        create cache --file=../template/JavaObjectCache.xml JavaObjectCache


Run the example
-------------------------

   Type this command or an IDE to start the simple example class JavaObjectClient

        mvn exec:java

    The JavaObjectCache will use the application/x-java-object encoding.
    Objects are stored as byte[] in the server, the cache can only be accesses if the client use the JavaSerializationMarshaller!

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

