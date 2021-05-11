package org.infinispan.wfink.playground.encoding.mm.hotrod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Map;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.MarshallerUtil;
import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.wfink.playground.encoding.mm.domain.SimpleEntry;
import org.infinispan.wfink.playground.encoding.mm.domain.SimpleEntryMarshaller;

/**
 * A client using the deprecated method with MessageMarshalling and Encoding for Infinispan 12+. It will register a generated proto schema and use marshaller implementation to store data to the cache.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class SimpleEntryClient {
  private RemoteCacheManager remoteCacheManager;
  private RemoteCache<String, SimpleEntry> cache;

  public SimpleEntryClient(String host, String port, String cacheName, boolean registerSchema) throws IOException {
    ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
    remoteBuilder.addServer().host(host).port(Integer.parseInt(port)); // .marshaller(new ProtoStreamMarshaller()); // The Protobuf based marshaller is no longer required for query capabilities as it is the default for ISPN 11 and RHDG 8

    remoteCacheManager = new RemoteCacheManager(remoteBuilder.build());
    cache = remoteCacheManager.getCache(cacheName);

    // manually registration of Marshaller and schema
    generateSchemasAndRegisterMarshallers(registerSchema);

    if (cache == null) {
      throw new RuntimeException("Cache '" + cacheName + "' not found. Please make sure the server is properly configured");
    }
  }

  /**
   * Register the Protobuf schemas and marshallers with the client and then register the schemas with the server too.
   *
   * @throws IOException
   */
  private void generateSchemasAndRegisterMarshallers(boolean pushSchema) throws IOException {
    // Register entity marshallers on the client side ProtoStreamMarshaller
    // instance associated with the remote cache manager.
    SerializationContext ctx = MarshallerUtil.getSerializationContext(remoteCacheManager);

    // generate the proto schema and register it for the serialization context
    final String msgSchemaFile;
    try {
      ctx.registerProtoFiles(FileDescriptorSource.fromResources("test.proto"));
    } catch (Exception e) {
      throw new RuntimeException("Failed to build protobuf definition from 'Message class'", e);
    }

    // register the marshaller for the serialization context
    ctx.registerMarshaller(new SimpleEntryMarshaller());

    if (pushSchema) { // send the schema to the server
      // Cache to register the schemas for the server/cluster
      final RemoteCache<String, String> protoMetadataCache = remoteCacheManager.getCache("___protobuf_metadata");
      String schemaFile = loadFileFromClassPathAsString("test.proto");
      protoMetadataCache.put("test.proto", schemaFile);
      System.out.println("====\n" + schemaFile + "\n===");

      // check for definition error for the registered Protobuf schemas
      String errors = protoMetadataCache.get(".errors");
      if (errors != null) {
        throw new IllegalStateException("Some Protobuf schema files contain errors: " + errors + "\nSchema :\n" + schemaFile);
      }
    }
  }

  public String loadFileFromClassPathAsString(String file) throws IOException {
    StringBuilder sb = new StringBuilder();

    try (BufferedReader r = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(file)));) {
      for (String line = r.readLine(); line != null; line = r.readLine()) {
        sb.append(line);
        sb.append("\n");
      }
    } catch (IOException e) {
      throw e;
    }
    return sb.toString();
  }

  private void insertSimpleEntries() {
    System.out.println("Inserting SimpleEntries into cache...");

    for (int i = 1; i < 5; i++) {
      LinkedList<String> text = new LinkedList<>();
      for (int j = 1; j < 4; j++) {
        text.add("Entry #" + i + " text line #" + j);
      }
      cache.put(String.valueOf(i), new SimpleEntry("Entry #" + i, text, i, i));
    }
    System.out.println("  -> " + cache.size() + " current size");
  }

  private void getSimpleEntries() {
    System.out.println("read all Messages...");
    for (Map.Entry<String, SimpleEntry> m : cache.entrySet()) {
      System.out.println(">> " + m);

    }
    System.out.println("Cache size is " + cache.size());
  }

  private void stop() {
    remoteCacheManager.stop();
  }

  public static void main(String[] args) throws IOException {
    String host = "localhost";
    String port = "11222";
    String cacheName = "SimpleEntryCache";

    if (args.length > 0) {
      port = args[0];
    }
    if (args.length > 1) {
      port = args[1];
    }
    SimpleEntryClient client = new SimpleEntryClient(host, port, cacheName, true);

    client.getSimpleEntries(); // get all from cache
    client.insertSimpleEntries(); // insert some

    client.stop();
    System.out.println("\nDone !");
  }
}