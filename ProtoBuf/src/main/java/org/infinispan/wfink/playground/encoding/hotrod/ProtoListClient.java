package org.infinispan.wfink.playground.encoding.hotrod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

/**
 * A simple client using the recommended Protostream Marshalling and Encoding for Infinispan, as Protobuf will be the best option to use the full power of Infinispan features.<br/>
 * The ProtoStreamMarshaller will support primitive/scalar types without explicit definition. Since Infinispan 12 the support for Collections has been added.<br/>
 * Because of this the simple String key and a value of type ArrayList, LinkedList, HashSet, LinkedHashSet, TreeSet can be used without any customization.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class ProtoListClient {
  private RemoteCacheManager remoteCacheManager;
  private RemoteCache<String, List<String>> listCache;

  public ProtoListClient(String host, String port, String cacheName) {
    ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
    remoteBuilder.addServer().host(host).port(Integer.parseInt(port)); // .marshaller(new ProtoStreamMarshaller()); // The Protobuf based marshaller is no longer required for query capabilities as it is the default for ISPN 11 and RHDG 8

    remoteCacheManager = new RemoteCacheManager(remoteBuilder.build()); // registerSchema need a cacheManager

    listCache = remoteCacheManager.getCache(cacheName);

    if (listCache == null) {
      throw new RuntimeException("Cache '" + cacheName + "' not found. Please make sure the server is properly configured");
    }

  }

  private void insert() {
    System.out.println("Inserting some Lists into cache...");
    ArrayList<String> list = new ArrayList<>();
    list.add("test1");
    listCache.put("1", list);
    list.add("test2");
    listCache.put("2", list);
    list.add("test3");
    listCache.put("3", list);

    System.out.println("  -> " + listCache.size() + " inserted");
  }

  private void getAll() {
    System.out.println("read all ...");
    for (Map.Entry<String, List<String>> m : listCache.entrySet()) {
      System.out.println(">> " + m.getKey() + "  " + m.getKey().getClass() + " : " + m.getValue() + "  " + m.getValue().getClass());

    }
    System.out.println("Messages read size is " + listCache.size());
  }

  private void stop() {
    remoteCacheManager.stop();
  }

  public static void main(String[] args) {
    String host = "localhost";
    String port = "11222";
    String cacheName = "ListCache";

    if (args.length > 0) {
      port = args[0];
    }
    if (args.length > 1) {
      port = args[1];
    }
    ProtoListClient client = new ProtoListClient(host, port, cacheName);

    client.insert(); // add a couple
    client.getAll(); // get all from cache

    client.stop();
    System.out.println("\nDone !");
  }
}