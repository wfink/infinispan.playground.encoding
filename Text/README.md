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
   Text messages can be any String, note JSON and XML is also a text message, but not all text messages are XML or JSON.

   The example can be changed to use alternative Marshaller implementations. As the cache is encoded the client is able to read and write with different Marshallers
   or REST and the entry will be converted automatically.

   If the encoding is removed, or the NoEncodingCache is used for the same, the entries can not be found if added with a different Marshaller or REST.
   As result the entries are added multiple times per Marshaller.

    check with REST

     curl http://127.0.0.1:11222/rest/v2/caches/PlainTextCache/1


2. Start this for the JSON example

        mvn exec:java -Dexec.mainClass="org.infinispan.wfink.playground.encoding.hotrod.JsonClient"

   It will store and read Strings with JSON code or text to a json encoded cache to demonstrate how entries are stored.
   The console will show it beautyfied no matter whether it has been added encoded or not,
   but will fail if the client has added invalid JSON formatted text with the correct encoding.
   Entries encoded as text (default) are beautified if valid JSON and left as is if invalid from the Console.
   This will not affect the clients at all, here entries are returned as is.
   Node that the unencoded entries will be transformed to JSON which add a type.

    check with REST

     curl -H "Accept: application/json" http://127.0.0.1:11222/rest/v2/caches/JsonCache/json
     curl http://127.0.0.1:11222/rest/v2/caches/JsonCache/json
     curl -H "Accept: application/json" http://127.0.0.1:11222/rest/v2/caches/JsonCache/jsonNoCR
     curl -H "Accept: application/json" http://127.0.0.1:11222/rest/v2/caches/JsonCache/jsonInvalid

3. Start this for the XML example

        mvn exec:java -Dexec.mainClass="org.infinispan.wfink.playground.encoding.hotrod.XmlClient"

   It will store and read Strings with XML code to a xml encoded cache to demonstrate how entries are stored.
   The console will show it as is, there is no validation or even beautfying.
   Note that the entry wihtout encoding at the client side is put as text/plain and will be transformed to XML <string>.....</string>, even if the cache value is a number!
   REST will show the entry as is, no matter whether `text/plain` or `application/xml` is used.

    check with REST

     curl http://127.0.0.1:11222/rest/v2/caches/XmlCache/xml
     curl http://127.0.0.1:11222/rest/v2/caches/XmlCache/xmlWithCR
     curl http://127.0.0.1:11222/rest/v2/caches/XmlCache/xmlInvalid
     curl http://127.0.0.1:11222/rest/v2/caches/XmlCache/xmlText
