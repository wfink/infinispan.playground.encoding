# infinispan.playground.encoding
Infinispan Query and indexing performance test
===============================

Author: Wolf-Dieter Fink
Technologies: Infinispan, Hot Rod, Protobuf, Query


What is it?
-----------

This example will test queries with range and constantly increase the number of entries.

There are some basic fileds with random value and a timeUTC which stores the currentTimeMillies().
The query will use a time range of 60seconds in the middle of start- and current time.


Preparation
-------------
Follow the README from the root project folder to prepare a server instance.
Add the necessary cache by using this as template

      <replicatedCache name="DateQueryCache">
              <encoding media-type="application/x-protostream"/>
           <indexing>
            <indexed-entities>
              <indexed-entity>playground.DateQueryEntity</indexed-entity>
            </indexed-entities>
          </indexing>
      </replicatedCache>

Encoding is necessary, the index part can be dropped to show the behavior without an index.


Run the example
-------------------------

   Run the test example
   ---------------------------------------------

   Use java command or an IDE to start the simple example class ProtobufDateTestClient

        mvn exec:java -D"exec.mainClass"="org.infinispan.wfink.playground.encoding.hotrod.ProtobufDateTestClient"

     The client will use the ProtoSchemaBuilder API to extract the information from the DateQueryEntity class @ProtoDoc and other @Proto annotations to register the 
     message object as playground.datequeryentity.

     The test will run a loop
       adding 20 entries each with a field timeUTC of currentTimeMillies
       run a query with a time range of 60seconds in the middle of start time and current time
       show the result size and duration time for the query

   access index statistics with REST client

      curl http://127.0.0.1:11222/rest/v2/caches/DateQueryCache/search/stats

        returns the index statistics for the current run
        Find more details in this documentation
        https://infinispan.org/docs/stable/titles/rest/rest.html#rest_v2_search_stats
