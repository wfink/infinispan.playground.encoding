# infinispan.playground.encoding
Infinispan Marshalling and Encoding examples
============================================

Author: Wolf-Dieter Fink
Technologies: Infinispan, Hot Rod


What is it?
-----------

Example how to use simple text based Marshalling and encoding with different client settings.

Preparation
-------------
Follow the README from the root project folder to prepare a server instance.
Add the necessary cache by using the template and CLI

        create cache --file=../template/PlainTextCache.xml PlainTextCache


Build and Run the example
-------------------------

1. Type this command or an IDE to start the simple example class org.infinispan.wfink.playground.encoding.hotrod.SimpleStringKeyClient

        mvn exec:java -Dexec.mainClass="org.infinispan.wfink.playground.encoding.hotrod.SimpleStringClient"

   It will use String/String to insert entries with text/plain encoding configured.
   By default the ProtoStreamMarshaller is used, the code can be changed to use other Marshallers to demonstrate the compatibility.
   It will be possible as the server will translate the entry for different encoders.
   If the encoding is removed, or the NoEncodingCache is used for the same, the entries can not be found if added with a different Marshaller or REST.
   As result the entries are added multiple times per Marshaller.

    check with REST

     curl http://127.0.0.1:11222/rest/v2/caches/PlainTextCache/1


2. Start this for the JSON example

        mvn exec:java -Dexec.mainClass="org.infinispan.wfink.playground.encoding.hotrod.JsonClient"

   It will store and read Strings with JSON code to a json encoded cache to demonstrate how entries are stored.
   Node that the unencoded entries will be transformed to JSON which add a type.

    check with REST

     curl http://127.0.0.1:11222/rest/v2/caches/JsonCache/json1
     curl http://127.0.0.1:11222/rest/v2/caches/JsonCache/json2
     curl http://127.0.0.1:11222/rest/v2/caches/JsonCache/text1
     curl http://127.0.0.1:11222/rest/v2/caches/JsonCache/text2

3. Start this for the XML example

        mvn exec:java -Dexec.mainClass="org.infinispan.wfink.playground.encoding.hotrod.XmlClient"

   It will store and read Strings with XML code to a xml encoded cache to demonstrate how entries are stored.
   Node that the unencoded entries will be transformed to XML which add a string-type.

    check with REST

     curl http://127.0.0.1:11222/rest/v2/caches/XmlCache/xml1
     curl http://127.0.0.1:11222/rest/v2/caches/XmlCache/xml2
     curl http://127.0.0.1:11222/rest/v2/caches/XmlCache/text1
     curl http://127.0.0.1:11222/rest/v2/caches/XmlCache/text2
