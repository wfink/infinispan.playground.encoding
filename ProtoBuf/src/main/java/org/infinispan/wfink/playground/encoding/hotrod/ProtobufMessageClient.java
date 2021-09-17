package org.infinispan.wfink.playground.encoding.hotrod;

import java.util.List;
import java.util.Map;

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
import org.infinispan.wfink.playground.encoding.domain.Message;

/**
 * A simple client using the recommended Marshalling and Encoding for Infinispan 11+, as Protobuf will be the best option to use the full power of Infinispan features.
 *
 * The ProtoStreamMarshaller will support primitive/scalar types without explicit definition. Because of this the simple String key can be used.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class ProtobufMessageClient {
  private RemoteCacheManager remoteCacheManager;
  private RemoteCache<String, Message> messageCache;

  public ProtobufMessageClient(String host, String port, String cacheName) {
    ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
    remoteBuilder.addServer().host(host).port(Integer.parseInt(port)); // .marshaller(new ProtoStreamMarshaller()); // The Protobuf based marshaller is no longer required for query capabilities as it is the default for ISPN 11 and RHDG 8

    remoteCacheManager = new RemoteCacheManager(remoteBuilder.build());
    messageCache = remoteCacheManager.getCache(cacheName);

    if (messageCache == null) {
      throw new RuntimeException("Cache '" + cacheName + "' not found. Please make sure the server is properly configured");
    }

    registerSchemas(false);
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
      msgSchemaFile = protoSchemaBuilder.fileName("message.proto").packageName("playground").addClass(Message.class).build(ctx);
    } catch (Exception e) {
      throw new RuntimeException("Failed to build protobuf definition from 'Message class'", e);
    }

    // the following part is not necessary if the server already have the schema
    if (updateServer) {
      // Cache to register the schemas with the server too
      final RemoteCache<String, String> protoMetadataCache = remoteCacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
      // add the schema to the server side
      protoMetadataCache.put("message.proto", msgSchemaFile);

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
  private void runIckleQuery4Message(QueryFactory qf, String query) {
    try {
      Query<Message> q = qf.create("from playground.Message m where " + query);
      List<Message> results = q.execute().list();
      System.out.printf("Query %s  : found %d matches\n", query, results.size());
      for (Message m : results) {
        System.out.println(">> " + m);
      }
    } catch (Exception e) {
      System.err.println("ICKLE QUERY FAILURE : " + e.getMessage());
    }
  }

  private void insertMessages() {
    System.out.println("Inserting Messages into cache...");
    messageCache.put("1", new Message(1, "First message for Ickle query", "Wolf", "Gustavo"));
    messageCache.put("2", new Message(2, "Second message for Ickle query", "Wolf", "Adrian"));
    messageCache.put("3", new Message(3, "A notification", "Wolf", "Tristan"));
    messageCache.put("4", new Message(4, "Another message", "Wolf", "Pedro"));
    messageCache.put("5", new Message(5, "Another message for Ickle-Query", "Wolf", "Adrian"));
    messageCache.put("6", new Message(6, "And another message for .Ickle.Query. with dots", "Wolf", "Adrian"));
    messageCache.put("7", new Message(7, "And another message to check query Ickle will be found", "Wolf", "Adrian"));
    messageCache.put("8", new Message(8, "And another message to check query MyIckleBla will be found", "Wolf", "Adrian"));
    messageCache.put("9", new Message(9, "Yet another message", "Wolf", "Adrian"));

//    for (int i = 10; i < 1001; i++) {
//      messageCache.put(String.valueOf(i), new Message(i, "Text " + i, "unknown", "nobody"));
//    }

    System.out.println("  -> " + messageCache.size() + " inserted");
  }

  private void getMessages() {
    System.out.println("read Messages by key 1...9");
    for (int i = 1; i < 10; i++) {
      Object m = messageCache.get(String.valueOf(i));
      if (m != null) {
        System.out.println(">> " + m);
      }
    }
    System.out.println("Cache size is " + messageCache.size());
  }

  private void getAllMessages() {
    System.out.println("read all Messages...");
    for (Map.Entry<String, Message> m : messageCache.entrySet()) {
      System.out.println(">> " + m);

    }
    System.out.println("Messages read size is " + messageCache.size());
  }

  private void findMessages() {
    System.out.println("find all Messages with author");
    QueryFactory qf = Search.getQueryFactory(messageCache);

    runIckleQuery4Message(qf, "m.author = 'Wolf'");
    System.out.println("  ------");
  }

  private void stop() {
    remoteCacheManager.stop();
  }

  public static void main(String[] args) {
    String host = "localhost";
    String port = "11222";
    String cacheName = "ProtobufMessageCache";

    if (args.length > 0) {
      port = args[0];
    }
    if (args.length > 1) {
      port = args[1];
    }
    ProtobufMessageClient client = new ProtobufMessageClient(host, port, cacheName);

    client.getAllMessages(); // get all messages from cache
    client.insertMessages(); // add a couple of messsages
    client.getMessages(); // read messages 1...9 by key
    // as correctly registered the client can use Ickle query
    client.findMessages();

    client.stop();
    System.out.println("\nDone !");
  }
}