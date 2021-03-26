package org.infinispan.wfink.playground.encoding.hotrod;

import java.util.Map;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.wfink.playground.encoding.domain.SimplePOJO;

/**
 * A simple client which uses Simple Objects (String) without any marshaller defined.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class NoEncodingClient {
  private RemoteCacheManager remoteCacheManager;
  private RemoteCache<Object, Object> remoteCache;

  public NoEncodingClient(String host, String port, String cacheName) {
    ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
    remoteBuilder.addServer().host(host).port(Integer.parseInt(port));
    remoteCacheManager = new RemoteCacheManager(remoteBuilder.build());
    remoteCache = remoteCacheManager.getCache(cacheName);

    if (remoteCache == null) {
      throw new RuntimeException("Cache '" + cacheName + "' not found. Please make sure the server is properly configured");
    }
  }

  private void insertEntries() {
    System.out.println("Inserting simple entries into cache...");
    remoteCache.put(1, "Entry #1");
    remoteCache.put(2, "Entry #2");
    remoteCache.put(3, "Entry #3");
    remoteCache.put(4, "Entry #4");
    remoteCache.put(5, "Entry #5");
    remoteCache.put(6, "Entry #6");
    remoteCache.put(7, "Entry #7");
    remoteCache.put(8, "Entry #8");
    remoteCache.put(9, "Entry #9");
    remoteCache.put(10, "Entry #10");

    remoteCache.put("x", "Entry #x");

    // by default the protostream Marshaller is used at client side and would not accept java classes
    try {
      remoteCache.put(100, new SimplePOJO("one", "two"));
    } catch (IllegalArgumentException e) {
      System.out.println(" ### expected Exception for unknown custom classes  --> " + e.getMessage());
    }

    System.out.println("  -> " + remoteCache.size() + " inserted");
  }

  private void getEntries() {
    for (int i = 1; i <= 10; i++) {
      Object m = remoteCache.get(i);
      if (m != null) {
        System.out.println(">> " + m);
      }
    }
    System.out.println("Cache size is " + remoteCache.size());
  }

  private void getAllEntries() {
    System.out.println("Read all entries from cache...");
    for (Map.Entry<Object, Object> e : remoteCache.entrySet()) {
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
    String cacheName = "NoEncodingCache";

    if (args.length > 0) {
      port = args[0];
    }
    if (args.length > 1) {
      port = args[1];
    }
    NoEncodingClient client = new NoEncodingClient(host, port, cacheName);

    client.getAllEntries(); // read existing all entries
    client.insertEntries(); // insert entries 1...10 "x"
    client.getEntries(); // get entries 1...10

    client.stop();
    System.out.println("\nDone !");
  }
}
