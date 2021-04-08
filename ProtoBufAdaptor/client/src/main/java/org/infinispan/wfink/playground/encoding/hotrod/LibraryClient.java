package org.infinispan.wfink.playground.encoding.hotrod;

import java.util.Map;
import java.util.Vector;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.MarshallerUtil;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.SerializationContextInitializer;
import org.infinispan.wfink.playground.encoding.domain.Author;
import org.infinispan.wfink.playground.encoding.domain.Book;

/**
 * A simple client using the recommended Marshalling and Encoding for Infinispan 12+, as Protobuf will be the best option to use the full power of Infinispan features.
 *
 * The ProtoStreamMarshaller will support primitive/scalar types without explicit definition. Because of this the simple String key can be used.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class LibraryClient {
  private RemoteCacheManager remoteCacheManager;
  private RemoteCache<String, Book> libraryCache;

  public LibraryClient(String host, String port, String cacheName) {
    ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
    remoteBuilder.addServer().host(host).port(Integer.parseInt(port)); // .marshaller(new ProtoStreamMarshaller()); // The Protobuf based marshaller is no longer required for query capabilities as it is the default for ISPN 11 and RHDG 8

    // why the initializer is needed, I would expect the annotation works automatically
    // add the Initializer as String will work
    // remoteBuilder.addContextInitializer("org.infinispan.wfink.playground.encoding.domain.LibraryInitalizerImpl");
    // add the Initializer directly as class instance to get compiler errors if missed
    org.infinispan.wfink.playground.encoding.domain.LibraryInitalizer initializer = new org.infinispan.wfink.playground.encoding.domain.LibraryInitalizerImpl();
    remoteBuilder.addContextInitializer(initializer);

    remoteCacheManager = new RemoteCacheManager(remoteBuilder.build()); // registerSchema need a cacheManager
//    registerSchemas(initializer);

    libraryCache = remoteCacheManager.getCache(cacheName);

    if (libraryCache == null) {
      throw new RuntimeException("Cache '" + cacheName + "' not found. Please make sure the server is properly configured");
    }

  }

  /**
   * Register the Protobuf schemas with the client and then register the schemas with the server too if requested. Note if the server does not have the schema some operations (like queries) are failing Also other type of clients can not read the content!
   *
   * @param updateServer true if the server should be updated with the schema
   */
  private void registerSchemas(SerializationContextInitializer initializer) {
    // Register entity marshaller on the client side ProtoStreamMarshaller
    // instance associated with the remote cache manager.
    SerializationContext ctx = MarshallerUtil.getSerializationContext(remoteCacheManager);

    // Cache to register the schemas with the server too
    final RemoteCache<String, String> protoMetadataCache = remoteCacheManager.getCache("___protobuf_metadata"); // ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME is only available if query dependency is set
    // add the schema to the server side
    protoMetadataCache.put(initializer.getProtoFileName(), initializer.getProtoFile());

    // check for definition error for the registered protobuf schemas
    String errors = protoMetadataCache.get(".errors"); // ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX is only available if query dependency is set
    if (errors != null) {
      throw new IllegalStateException("Some Protobuf schema files contain errors: " + errors + "\nSchema :\n" + initializer.getProtoFileName());
    }
  }

  /**
   * Complete the query and show the results.
   *
   * @param qf
   * @param query where clause for the Ickle query
   */
//  private void runIckleQuery4Message(QueryFactory qf, String query) {
//    try {
//      Query<Message> q = qf.create("from playground.Message m where " + query);
//      List<Message> results = q.execute().list();
//      System.out.printf("Query %s  : found %d matches\n", query, results.size());
//      for (Message m : results) {
//        System.out.println(">> " + m);
//      }
//    } catch (Exception e) {
//      System.err.println("ICKLE QUERY FAILURE : " + e.getMessage());
//    }
//  }

  private void insertBooks() {
    System.out.println("Inserting Messages into cache...");
    Vector<Author> authors = new Vector<>();
    authors.add(new Author("Rowling", "J.K."));

    libraryCache.put("1", new Book("Harry Potter and the Philosopher's Stone", "First adventure of Harry Potter", 1997, authors));
    libraryCache.put("2", new Book("Harry Potter and the Chamber of Secrets", "Second adventure of Harry Potter", 1998, authors));
    libraryCache.put("3", new Book("Harry Potter and the Prisoner of Azkaban", "Third adventure of Harry Potter", 1999, authors));
    libraryCache.put("4", new Book("Harry Potter and the Goblet of Fire", "Fourth adventure of Harry Potter", 2000, authors));
    libraryCache.put("5", new Book("Harry Potter and the Order of the Phoenix", "Fifth adventure of Harry Potter", 2003, authors));
    libraryCache.put("6", new Book("Harry Potter and the Half-Blood Prince", "Sixth adventure of Harry Potter", 2005, authors));
    libraryCache.put("7", new Book("Harry Potter and the Deathly Hollows", "Seventh adventure of Harry Potter", 2007, authors));

    System.out.println("  -> " + libraryCache.size() + " inserted");
  }

  private void getAllBooks() {
    System.out.println("read all Messages...");
    for (Map.Entry<String, Book> m : libraryCache.entrySet()) {
      System.out.println(">> " + m);

    }
    System.out.println("Messages read size is " + libraryCache.size());
  }

//  private void findMessages() {
//    System.out.println("find all Messages with author");
//    QueryFactory qf = Search.getQueryFactory(libraryCache);
//
//    runIckleQuery4Message(qf, "m.author = 'Wolf'");
//    System.out.println("  ------");
//  }

  private void stop() {
    remoteCacheManager.stop();
  }

  public static void main(String[] args) {
    String host = "localhost";
    String port = "11222";
    String cacheName = "LibraryCache";

    if (args.length > 0) {
      port = args[0];
    }
    if (args.length > 1) {
      port = args[1];
    }
    LibraryClient client = new LibraryClient(host, port, cacheName);

    client.getAllBooks(); // get all messages from cache
    client.insertBooks(); // add a couple of messsages

    client.stop();
    System.out.println("\nDone !");
  }
}