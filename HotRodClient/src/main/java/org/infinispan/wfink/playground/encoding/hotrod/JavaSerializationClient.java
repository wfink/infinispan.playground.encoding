package org.infinispan.wfink.playground.encoding.hotrod;

import java.util.Map;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.marshall.JavaSerializationMarshaller;
import org.infinispan.wfink.playground.encoding.domain.SimplePOJO;

/**
 * A client which uses the Java Serialization to store Objects in a cache. WARNING the use of Java Marshalling is not recommended as the performance is worse, it is the slowest marshalling!
 *
 * In this case the Java classes are not needed at server side as the class is stored in serialized form byte[]
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class JavaSerializationClient {
  private RemoteCacheManager remoteCacheManager;
  private RemoteCache<Object, SimplePOJO> remoteCache;

  public JavaSerializationClient(String host, String port, String cacheName) {
    ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
    remoteBuilder.addServer().host(host).port(Integer.parseInt(port));
    // use Java Marshaller the serialization white list must be added to READ the data from the server
    remoteBuilder.marshaller(new JavaSerializationMarshaller()).addJavaSerialWhiteList("org.infinispan.wfink.playground.encoding.domain.SimplePOJO");

    remoteCacheManager = new RemoteCacheManager(remoteBuilder.build());
    remoteCache = remoteCacheManager.getCache(cacheName);

    if (remoteCache == null) {
      throw new RuntimeException("Cache '" + cacheName + "' not found. Please make sure the server is properly configured");
    }
  }

  private void createEntries() {
    System.out.println("Inserting simple entries into cache...");
    remoteCache.put("s1", new SimplePOJO("Entry", "s1"));
    remoteCache.put("s2", new SimplePOJO("Entry", "s2"));
    remoteCache.put("s3", new SimplePOJO("Entry", "s3"));
    remoteCache.put("s4", new SimplePOJO("Entry", "s4"));
    remoteCache.put("s5", new SimplePOJO("Entry", "s5"));
    remoteCache.put(new Integer(1), new SimplePOJO("Entry", "1"));
    remoteCache.put(2, new SimplePOJO("Entry", "2"));

    System.out.println("  -> " + remoteCache.size() + " inserted");
  }

  private void getEntry(Object key) {
    SimplePOJO m = remoteCache.get(key);
    if (m != null) {
      System.out.println(">> " + m);
    }
  }

  private void getEntries() {
    for (int i = 1; i < 10; i++) {
      getEntry("s" + i);
      getEntry(i);
    }
    System.out.println("Cache size is " + remoteCache.size());
  }

  private void getAllEntries() {
    System.out.println("Read all entries from cache...");

    for (Map.Entry<Object, Object> e : remoteCacheManager.getCache("JavaSerializedCache").entrySet()) {
      System.out.println(">> " + e + "  key=" + e.getKey().getClass() + " value=" + e.getValue().getClass());
    }
    System.out.println("  done - cache size is " + remoteCache.size());
  }

  private void stop() {
    remoteCacheManager.stop();
  }

  public static void main(String[] args) {
    String host = "localhost";
    String port = "11222";
    String cacheName = "JavaSerializedCache";

    if (args.length > 0) {
      port = args[0];
    }
    if (args.length > 1) {
      port = args[1];
    }
    JavaSerializationClient client = new JavaSerializationClient(host, port, cacheName);

    client.getAllEntries();
    client.createEntries();
    client.getEntries();

    client.stop();
    System.out.println("\nDone !");
  }
}
