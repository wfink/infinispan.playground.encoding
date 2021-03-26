package org.infinispan.wfink.playground.encoding.hotrod;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.marshall.JavaSerializationMarshaller;
import org.infinispan.wfink.playground.encoding.domain.SimplePOJO;

/**
 * A client which uses the Java Serialization to store POJO Objects in a cache. WARNING the use of Java Marshalling is not recommended as the performance is worse, it is the slowest marshalling!
 *
 * The Java classes must be added to the server classpath as well as the serialization white-list must be configured on both sides!
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class JavaObjectClient {
  private RemoteCacheManager remoteCacheManager;
  private RemoteCache<String, SimplePOJO> remoteCache;

  public JavaObjectClient(String host, String port, String cacheName) {
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
    remoteCache.put("1", new SimplePOJO("Entry", "1"));
    remoteCache.put("2", new SimplePOJO("Entry", "2"));
    remoteCache.put("3", new SimplePOJO("Entry", "3"));
    remoteCache.put("4", new SimplePOJO("Entry", "4"));
    remoteCache.put("5", new SimplePOJO("Entry", "5"));
    remoteCache.put("6", new SimplePOJO("Entry", "6"));
    remoteCache.put("7", new SimplePOJO("Entry", "7"));
    remoteCache.put("8", new SimplePOJO("Entry", "8"));
    remoteCache.put("9", new SimplePOJO("Entry", "9"));
    remoteCache.put("10", new SimplePOJO("Entry", "10"));

    System.out.println("  -> " + remoteCache.size() + " inserted");
  }

  private void getEntries() {
    for (int i = 1; i < 10; i++) {
      SimplePOJO m = remoteCache.get(String.valueOf(i));
      if (m != null) {
        System.out.println(">> " + m);
      }
    }
    System.out.println("Cache size is " + remoteCache.size());
  }

  private void stop() {
    remoteCacheManager.stop();
  }

  public static void main(String[] args) {
    String host = "localhost";
    String port = "11222";
    String cacheName = "JavaObjectCache";

    if (args.length > 0) {
      port = args[0];
    }
    if (args.length > 1) {
      port = args[1];
    }
    JavaObjectClient client = new JavaObjectClient(host, port, cacheName);

    client.createEntries();
    client.getEntries();

    client.stop();
    System.out.println("\nDone !");
  }
}
