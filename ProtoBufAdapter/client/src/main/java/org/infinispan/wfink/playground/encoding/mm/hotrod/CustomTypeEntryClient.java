package org.infinispan.wfink.playground.encoding.mm.hotrod;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.wfink.playground.encoding.mm.domain.CustomTypeEntry;
import org.infinispan.wfink.playground.encoding.mm.domain.CustomTypeInitializer;

/**
 * A simple client using the recommended Marshalling and Encoding for Infinispan 12+, as Protobuf will be the best option to use the full power of Infinispan features.
 *
 * The ProtoStreamMarshaller will support primitive/scalar types without explicit definition. Because of this the simple String key can be used.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class CustomTypeEntryClient {
  private RemoteCacheManager remoteCacheManager;
  private RemoteCache<String, CustomTypeEntry> cache;

  public CustomTypeEntryClient(String host, String port, String cacheName) {
    ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
    remoteBuilder.addServer().host(host).port(Integer.parseInt(port)); // .marshaller(new ProtoStreamMarshaller()); // The Protobuf based marshaller is no longer required for query capabilities as it is the default for ISPN 11 and RHDG 8

    // There is an enhancement request to not use generated code -> https://issues.redhat.com/browse/ISPN-12963
    // add the Initializer as String will work
    // remoteBuilder.addContextInitializer("org.infinispan.wfink.playground.encoding.domain.LibraryInitalizerImpl");
    // add the Initializer directly as class instance to get compiler errors if missed
    CustomTypeInitializer initializer = new org.infinispan.wfink.playground.encoding.mm.domain.CustomTypeInitializerImpl();
    remoteBuilder.addContextInitializer(initializer);

    remoteCacheManager = new RemoteCacheManager(remoteBuilder.build()); // registerSchema need a cacheManager
    System.out.println("SCHEMA\n" + initializer.getProtoFile());
//    registerSchemas(initializer);  // not needed as long as the example domain classes are copied to the ISPN_HOME/server/lib directory

    cache = remoteCacheManager.getCache(cacheName);

    if (cache == null) {
      throw new RuntimeException("Cache '" + cacheName + "' not found. Please make sure the server is properly configured");
    }

  }

  /**
   * Register the Protobuf schemas with the client and then register the schemas with the server too if requested. Note if the server does not have the schema some operations (like queries) are failing Also other type of clients can not read the content! The code should be hidden by having a method
   * to register the schema and/or push it to the server -> https://issues.redhat.com/browse/ISPN-12964
   *
   * @param updateServer true if the server should be updated with the schema
   */
  private void registerSchemas(GeneratedSchema initializer) {
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

  private void insertCustomEntries() {
    System.out.println("Inserting TestEntries into cache...");

    cache.put("100", new CustomTypeEntry("Entry100", new BigInteger("13763753091226345046315979581580902400000000")));
    cache.put("200", new CustomTypeEntry("Entry200", new BigInteger("523022617466601111760007224100074291200000000")));
    cache.put("300", new CustomTypeEntry("Entry300", new BigInteger("20397882081197443358640281739902897356800000000")));
    cache.put("400", new CustomTypeEntry("Entry400", new BigInteger("815915283247897734345611269596115894272000000000")));

    System.out.println("  -> " + cache.size() + " current size");
  }

  private void getCustomEntries() {
    System.out.println("read all Messages...");
    for (Map.Entry<String, CustomTypeEntry> m : cache.entrySet()) {
      System.out.println(">> " + m);

    }
    System.out.println("Messages read size is " + cache.size());
  }

  private void stop() {
    remoteCacheManager.stop();
  }

  public static void main(String[] args) throws IOException {
    String host = "localhost";
    String port = "11222";
    String cacheName = "CustomTypeCache";

    if (args.length > 0) {
      port = args[0];
    }
    if (args.length > 1) {
      port = args[1];
    }
    CustomTypeEntryClient client = new CustomTypeEntryClient(host, port, cacheName);

    client.getCustomEntries(); // get all from cache
    client.insertCustomEntries(); // insert some

    client.stop();
    System.out.println("\nDone !");
  }
}