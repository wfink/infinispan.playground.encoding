package org.infinispan.wfink.playground.encoding.hotrod;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map.Entry;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.infinispan.wfink.playground.encoding.JavaAPIProtoSchemaInitializer;

/**
 * A simple client using the HashMapAdapter to store a HashMap directly to a cache. Infinispan 12+ is mandatory.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class HashtableAdapterClient {
  private RemoteCacheManager remoteCacheManager;
  private RemoteCache<String, Hashtable<String, String>> hashtableCache;

  public HashtableAdapterClient(String host, String port, String cacheName) {
    ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
    remoteBuilder.addServer().host(host).port(Integer.parseInt(port));

    JavaAPIProtoSchemaInitializer initializer = new org.infinispan.wfink.playground.encoding.JavaAPIProtoSchemaInitializerImpl();
    remoteBuilder.addContextInitializer(initializer);

    remoteCacheManager = new RemoteCacheManager(remoteBuilder.build()); // registerSchema need a cacheManager
//      registerSchemas(initializer);  // not needed as long as the example domain classes are copied to the ISPN_HOME/server/lib directory

    hashtableCache = remoteCacheManager.getCache(cacheName);

    if (hashtableCache == null) {
      throw new RuntimeException("Cache '" + cacheName + "' not found. Please make sure the server is properly configured");
    }

  }

  /**
   * Register generated Protobuf schema with Infinispan Server.
   *
   * @param remoteCacheManager Initialized cache manager
   * @param initializer        The AutoProtoSchemaInitializer annotated class
   */
  private static void registerSchemas(RemoteCacheManager remoteCacheManager, GeneratedSchema initializer) {
    // Store schemas in the '___protobuf_metadata' cache to register them. Using ProtobufMetadataManagerConstants might require the query dependency.
    final RemoteCache<String, String> protoMetadataCache = remoteCacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
    // Add the generated schema to the cache.
    protoMetadataCache.put(initializer.getProtoFileName(), initializer.getProtoFile());
    // Ensure the registered Protobuf schemas do not contain errors.
    String errors = protoMetadataCache.get(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
    if (errors != null) {
      throw new IllegalStateException("Some Protobuf schema files contain errors: " + errors + "\nSchema :\n" + initializer.getProtoFileName());
    }
  }

  private void putHashMap() {
    // Put a sample data to the RemoteCache.
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss.S_Z");

    String key = String.format("KEY-%s", sdf.format(new Date()));
    Hashtable<String, String> map = new Hashtable<>();
    for (int i = 0; i < 3; i++) {
      map.put(String.format("KEY-%02d", i), String.format("VALUE-%02d %s", i, key));
    }
    hashtableCache.put(key, map);

    System.out.printf("added a Map for key %s : %s\n", key, hashtableCache.get(key));
  }

  private void getAll() {
    System.out.printf("read all %d entries...\n", hashtableCache.size());
    for (Entry<String, Hashtable<String, String>> m : hashtableCache.entrySet()) {
      System.out.println(">> " + m);
    }
  }

  private void stop() {
    remoteCacheManager.stop();
  }

  public static void main(String[] args) {
    String host = "localhost";
    String port = "11222";
    String cacheName = "HashtableCache";

    if (args.length > 0) {
      port = args[0];
    }
    if (args.length > 1) {
      port = args[1];
    }
    HashtableAdapterClient client = new HashtableAdapterClient(host, port, cacheName);

    client.getAll();
    client.putHashMap();

    client.stop();
    System.out.println("\nDone !");
  }
}
