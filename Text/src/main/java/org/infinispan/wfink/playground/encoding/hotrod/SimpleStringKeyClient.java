package org.infinispan.wfink.playground.encoding.hotrod;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;

/**
 * A simple client which uses Simple Objects (String). With the encoding text/plain most of the Marshallers are able to read it. Note without encoding the cache content is different and the simple Strings used as keys are different byte[] for each Marshaller, so the entries are created per
 * Marshaller as not found.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class SimpleStringKeyClient {
  private RemoteCacheManager remoteCacheManager;
  private RemoteCache<String, String> remoteCache;

  public SimpleStringKeyClient(String host, String port, String cacheName) {
    ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
    remoteBuilder.addServer().host(host).port(Integer.parseInt(port));
    remoteBuilder.marshaller(new ProtoStreamMarshaller()); // this is basically the default since ISPN 12
//  remoteBuilder.marshaller(new JavaSerializationMarshaller()); // this will not work
    // the remaining Marshallers are working and can be used for the same cache configured with encoding text/plain
//  remoteBuilder.marshaller(new UTF8StringMarshaller());
//  remoteBuilder.marshaller(new StringMarshaller(Charset.defaultCharset()));
//  remoteBuilder.marshaller(new GenericJBossMarshaller());
    remoteCacheManager = new RemoteCacheManager(remoteBuilder.build());

    remoteCache = remoteCacheManager.getCache(cacheName);

    if (remoteCache == null) {
      throw new RuntimeException("Cache '" + cacheName + "' not found. Please make sure the server is properly configured");
    }
  }

  private void insertEntries() {
    System.out.println("Inserting simple entries into cache...");
    remoteCache.put("1", "Entry #1");
    remoteCache.put("2", "Entry #2");
    remoteCache.put("3", "Entry #3");
    remoteCache.put("4", "Entry #4");
    remoteCache.put("5", "Entry #5");
    remoteCache.put("6", "Entry #6");
    remoteCache.put("7", "Entry #7");
    remoteCache.put("8", "Entry #8");
    remoteCache.put("9", "Entry #9");
    remoteCache.put("10", "Entry #10");
    remoteCache.put("xml1", "<person><firstname>Wolf</firstname><lastname>Fink</lastname></person>");

    System.out.println("  -> " + remoteCache.size() + " inserted");
  }

  private void getEntries() {
    for (int i = 1; i < 11; i++) {
      String m = remoteCache.get(String.valueOf(i));
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
    String cacheName = "PlainTextCache";

    if (args.length > 0) {
      port = args[0];
    }
    if (args.length > 1) {
      port = args[1];
    }
    SimpleStringKeyClient client = new SimpleStringKeyClient(host, port, cacheName);

    client.insertEntries();
    client.getEntries();

    client.stop();
    System.out.println("\nDone !");
  }
}