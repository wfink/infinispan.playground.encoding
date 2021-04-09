package org.infinispan.wfink.playground.encoding.mm.hotrod;

import java.util.ArrayList;
import java.util.Map;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.MarshallerUtil;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.wfink.playground.encoding.mm.domain.Author;
import org.infinispan.wfink.playground.encoding.mm.domain.AuthorMarshaller;
import org.infinispan.wfink.playground.encoding.mm.domain.Book;
import org.infinispan.wfink.playground.encoding.mm.domain.BookMarshaller;

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

  /**
   *
   * @param host           server hostname
   * @param port           server port
   * @param cacheName      the name of the cache in use
   * @param registerSchema flag whether the schema should be pushed to the server
   */
  public LibraryClient(String host, String port, String cacheName, boolean registerSchema) {
    ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
    remoteBuilder.addServer().host(host).port(Integer.parseInt(port)); // .marshaller(new ProtoStreamMarshaller()); // The Protobuf based marshaller is no longer required for query capabilities as it is the default for ISPN 11 and RHDG 8

    remoteCacheManager = new RemoteCacheManager(remoteBuilder.build());
    libraryCache = remoteCacheManager.getCache(cacheName);

    // manually registration of Marshaller and schema
    generateSchemasAndRegisterMarshallers(registerSchema);

    if (libraryCache == null) {
      throw new RuntimeException("Cache '" + cacheName + "' not found. Please make sure the server is properly configured");
    }
  }

  /**
   * Register the Protobuf schemas and marshallers with the client and then register the schemas with the server too.
   */
  private void generateSchemasAndRegisterMarshallers(boolean pushSchema) {
    // Register entity marshallers on the client side ProtoStreamMarshaller
    // instance associated with the remote cache manager.
    SerializationContext ctx = MarshallerUtil.getSerializationContext(remoteCacheManager);

    // generate the proto schema and register it for the serialization context
    final String msgSchemaFile;
    try {
      ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
      msgSchemaFile = protoSchemaBuilder.fileName("library.proto").packageName("playground").addClass(Book.class).addClass(Author.class).build(ctx);
      System.out.println(msgSchemaFile);
    } catch (Exception e) {
      throw new RuntimeException("Failed to build protobuf definition from 'Message class'", e);
    }

    // register the marshaller for the serialization context
    ctx.registerMarshaller(new AuthorMarshaller());
    ctx.registerMarshaller(new BookMarshaller());

    if (pushSchema) { // send the schema to the server
      // Cache to register the schemas for the server/cluster
      final RemoteCache<String, String> protoMetadataCache = remoteCacheManager.getCache("___protobuf_metadata");
      protoMetadataCache.put("library.proto", msgSchemaFile);

      // check for definition error for the registered Protobuf schemas
      String errors = protoMetadataCache.get(".errors");
      if (errors != null) {
        throw new IllegalStateException("Some Protobuf schema files contain errors: " + errors + "\nSchema :\n" + msgSchemaFile);
      }
    }
  }

  private void insertBooks() {
    System.out.println("Inserting Messages into cache...");
    ArrayList<Author> authors = new ArrayList<>();
    authors.add(new Author("Martin", "George R.R."));

    libraryCache.put("1", new Book("A game of Thrones", "First novel of A Song of Ice and Fire", 1996, authors));
    libraryCache.put("2", new Book("A clash of Kings", "Second novel of A Song of Ice and Fire", 1999, authors));
    libraryCache.put("3", new Book("A storm of swords", "Third novel of A Song of Ice and Fire", 2000, authors));
    libraryCache.put("4", new Book("A feast for crows", "Fourth  novel of A Song of Ice and Fire", 2005, authors));
    libraryCache.put("5", new Book("A dance with Dragons", "Fifth  novel of A Song of Ice and Fire", 2011, authors));
    libraryCache.put("6", new Book("The winds of winter", "Sixth  novel of A Song of Ice and Fire", -1, authors));
    libraryCache.put("7", new Book("The dream of spring", "Seventh  novel of A Song of Ice and Fire", -1, authors));

    System.out.println("  -> " + libraryCache.size() + " current size");
  }

  private void getAllBooks() {
    System.out.println("read all Messages...");
    for (Map.Entry<String, Book> m : libraryCache.entrySet()) {
      System.out.println(">> " + m);

    }
    System.out.println("Messages read size is " + libraryCache.size());
  }

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
    LibraryClient client = new LibraryClient(host, port, cacheName, false);

    client.getAllBooks(); // get all messages from cache
    client.insertBooks(); // add a couple of messsages

    client.stop();
    System.out.println("\nDone !");
  }
}