package org.infinispan.wfink.playground.encoding.hotrod;

import java.util.List;
import java.util.Random;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.MarshallerUtil;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.infinispan.wfink.playground.encoding.query.DateQueryEntity;

/**
 * A simple client using the recommended Marshalling and Encoding for Infinispan 11+, as Protobuf will be the best option to use the full power of Infinispan features.
 *
 * The ProtoStreamMarshaller will support primitive/scalar types without explicit definition. Because of this the simple String key can be used.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class ProtobufDateTestClient {
  private static final String PROTOSCHEMA_NAME = "datequery.proto";
  private static final String[] textTemplate = { "Alpha", "Bravo", "Charly", "Delta", "Echo" };
  private static final long startTime = System.currentTimeMillis();
  private RemoteCacheManager remoteCacheManager;
  private RemoteCache<String, DateQueryEntity> remoteCache;

  static {

  }

  public ProtobufDateTestClient(String host, String port, String cacheName) {
    ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
    remoteBuilder.addServer().host(host).port(Integer.parseInt(port)); // .marshaller(new ProtoStreamMarshaller()); // The Protobuf based marshaller is no longer required for query capabilities as it is the default for ISPN 11 and RHDG 8

    remoteCacheManager = new RemoteCacheManager(remoteBuilder.build());
    remoteCache = remoteCacheManager.getCache(cacheName);

    if (remoteCache == null) {
      throw new RuntimeException("Cache '" + cacheName + "' not found. Please make sure the server is properly configured");
    }

    registerSchemas(true);
  }

  /**
   * Register the Protobuf schemas with the client and then register the schemas with the server too if requested. Note if the server does not have the schema some operations (like queries) are failing Also other type of clients can not read the content!
   *
   * @param updateServer true if the server should be updated with the schema
   */
  private void registerSchemas(boolean updateServer) {
    // Register entity marshaller on the client side ProtoStreamMarshaller
    // instance associated with the remote cache manager.
    SerializationContext ctx = MarshallerUtil.getSerializationContext(remoteCacheManager);

    // generate the message protobuf schema file and marshaller based on the annotations on Message class
    // and register it with the SerializationContext of the client
    String msgSchemaFile = null;
    try {
      ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
      msgSchemaFile = protoSchemaBuilder.fileName(PROTOSCHEMA_NAME).packageName("playground").addClass(DateQueryEntity.class).build(ctx);
    } catch (Exception e) {
      throw new RuntimeException("Failed to build protobuf definition from 'Message class'", e);
    }

    // the following part is not necessary if the server already have the schema
    if (updateServer) {
      // Cache to register the schemas with the server too
      final RemoteCache<String, String> protoMetadataCache = remoteCacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
      // add the schema to the server side
      protoMetadataCache.put(PROTOSCHEMA_NAME, msgSchemaFile);

      // check for definition error for the registered protobuf schemas
      String errors = protoMetadataCache.get(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
      if (errors != null) {
        throw new IllegalStateException("Some Protobuf schema files contain errors: " + errors + "\nSchema :\n" + msgSchemaFile);
      }
    }
  }

  /**
   * Complete the query and show the results.
   *
   * @param qf
   * @param query where clause for the Ickle query
   */
  private void runIckleQuery(QueryFactory qf, String query) {
    try {
      long start = System.currentTimeMillis();
      Query<DateQueryEntity> q = qf.create("from playground.DateQueryEntity e where " + query);
//      System.out.printf("   create query duration %d\n", System.currentTimeMillis() - start);
      List<DateQueryEntity> results = q.execute().list();

      System.out.printf("Query %s  : found %d matches in %d ms\n", query, results.size(), System.currentTimeMillis() - start);
//      for (DateQueryEntity m : results) {
//        System.out.println(">> " + m);
//      }
    } catch (Exception e) {
      System.err.println("ICKLE QUERY FAILURE : " + e.getMessage());
    }
  }

  private void insert(int numberOfEntries) {
//    System.out.println("Inserting entries into cache...");
    Random r = new Random();
    for (int i = 0; i < numberOfEntries; i++) {
      DateQueryEntity e = new DateQueryEntity(textTemplate[r.nextInt(textTemplate.length)], r.nextInt(10), System.currentTimeMillis());
      remoteCache.put("" + e.getTimeUTC(), e);
      try {
        Thread.sleep(1);
      } catch (InterruptedException e1) {
      }
    }
    System.out.println("  -> " + remoteCache.size() + " after " + numberOfEntries + "added");
  }

  private void find() {
    QueryFactory qf = Search.getQueryFactory(remoteCache);

    long now = System.currentTimeMillis();
    long diff = now - startTime;
    long x1 = startTime + (diff / 2);
    long x2 = x1 + 60000;

    runIckleQuery(qf, "e.text = '" + textTemplate[0] + "' AND e.number = 5 AND e.timeUTC >= " + x1 + " AND e.timeUTC <= " + x2);
  }

  private void stop() {
    remoteCacheManager.stop();
  }

  public static void main(String[] args) {
    String host = "localhost";
    String port = "11222";
    String cacheName = "DateQueryCache";

    if (args.length > 0) {
      port = args[0];
    }
    if (args.length > 1) {
      port = args[1];
    }
    ProtobufDateTestClient client = new ProtobufDateTestClient(host, port, cacheName);

    boolean run = true;
    while (run) {
      client.insert(20);
      client.find();
    }

    client.stop();
    System.out.println("\nDone !");
  }
}