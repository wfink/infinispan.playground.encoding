# infinispan.playground.encoding
Infinispan Marshalling and Encoding examples
===============================

Author: Wolf-Dieter Fink
Technologies: Infinispan, Hot Rod, Protobuf


What is it?
-----------

This example will show how the HotRod client and REST can read Java serialized objects from an encoded cache

Java serialization is not recommeded as the performance is bad.


Preparation
-------------
Follow the README from teh root project folder to prepare a server instance.
Add the necessary cache by using the template and CLI

        create cache --file=../template/JavaSerializedCache.xml JavaSerializedCache


Run the example
-------------------------

   Type this command or an IDE to start the simple example class JavaSerializationClient

        mvn exec:java

    The JavaSerializedCache will use the application/x-java-serialized-object encoding.
    Objects are stored as byte[] in the server, the cache can only be accesses if the client use the JavaSerializationMarshaller!

      curl -H "Key-Content-Type: application/x-java-object;type=java.lang.String" --output - http://127.0.0.1:11222/rest/v2/caches/JavaSerializedCache/s1
       -> returns the class as byte[]
          the --output - is needed to force it as this could mess up the terminal!

    Conversion to other output like json or xml is not possible.
     TODO could it be customized?

    TODO why the Integer key is not readable?
