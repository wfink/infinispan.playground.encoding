# infinispan.playground.encoding
Infinispan Marshalling and Encoding examples
===============================

Author: Wolf-Dieter Fink
Technologies: Infinispan, Hot Rod, JBossMashalling


What is it?
-----------

Example how to use the server side encoding with JBossMarshalling and the clients

With encoding the cache content can be converted if different clients are used, the server will convert some of it automatically if possible.
But the only encoding which fully supports the power of Infinispan is Protostream.

Hot Rod is a binary TCP client-server protocol. The Hot Rod protocol facilitates faster client and server interactions in comparison to other text based protocols and allows clients to make decisions about load balancing, failover and data location operations.


Preparation
-------------
Follow the README from the root project folder to prepare a server instance.
Add the necessary cache by using the template and CLI

        create cache --file=../template/JBossMarshalledCache.xml JBossMarshalledCache


Build and Run the example
-------------------------

1. Type this command or an IDE to start the simple example class JBossMarshallingClient

        mvn exec:java

    The JavaJBossMarshalledCache will use the application/x-jboss-marshalling encoding.
    Objects are stored as byte[] in the server, to access the encoded cache the GenericJBossMarshaller must be used, other like JavaSerialization will not work
    as the content can not migrated.

    To read data via REST the following command can be used:

      curl --output - http://127.0.0.1:11222/rest/v2/caches/JBossMarshalledCache/s1
      curl -H "Accept: application/x-jboss-marshalling" --output - http://127.0.0.1:11222/rest/v2/caches/JBossMarshalledCache/s1

   Both will return the same result, the entry is shown with special character as there is no transcoding, because of this --output is necessary as the output can mess up the terminal!

   Conversion to plain text is the only alternative, all others will be rejected with ISPN120007.

      curl -H "Accept: text/plain" http://127.0.0.1:11222/rest/v2/caches/JBossMarshalledCache/s1

   This will show an error because of blocked serialization, the allow-list needs to be set for the cache.
   Also it is possible to add `-Dinfinispan.deserialization.allowlist.regexps=org.infinispan.wfink.*` to the server start command to set this.

   After that the REST command will show the class name 'SimplePOJO' but there is a ClassNotFound logged at server side.
   This can be fixed by adding the class to the server path, copy ../Domain/target/Encoding-Domain.jar to the SERVER/server/lib directory.

   With those changes the server should be able to show the entry correctly. Note that the Console will use the REST API as well so it display the content
   only if the REST command above is able to show that as well, otherwise an error message is shown.

   If a new Java version (>11) is used there will be an InaccessibleObjectException for reflection.
   This is because of limits to the use of certain classes as part of JavaModules aka. JigSaw.

   To fix that it is necessary to add options to allow that, for this project the following need to be added to the Java options, i.e. the following line at the end of bin/server.conf

       JAVA_OPTS="$JAVA_OPTS --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED"


Hint
-----
There might be a WARN message, i.e. ISPN000602, that conversion between Java Objects and JSON are deprecated. This is because JBossMarshalling is deprecated.
But it might stay for simple use-cases where protostream can not be used.
