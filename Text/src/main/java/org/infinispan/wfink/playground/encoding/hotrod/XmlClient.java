package org.infinispan.wfink.playground.encoding.hotrod;

import java.util.Arrays;
import java.util.List;

import org.infinispan.client.hotrod.DataFormat;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.commons.marshall.UTF8StringMarshaller;

/**
 * A simple client which uses Simple Objects (String). With the encoding text/plain most of the Marshallers are able to read it. Note without encoding the cache content is different and the simple Strings used as keys are different byte[] for each Marshaller, so the entries are created per
 * Marshaller as not found.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class XmlClient {
  private RemoteCacheManager remoteCacheManager;
  private RemoteCache<String, String> remoteCache;

  public XmlClient(String host, String port, String cacheName) {
    ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
    remoteBuilder.addServer().host(host).port(Integer.parseInt(port));
    remoteBuilder.marshaller(new UTF8StringMarshaller());
//    remoteBuilder.marshaller(new StringMarshaller(Charset.defaultCharset()));

    remoteCacheManager = new RemoteCacheManager(remoteBuilder.build());

    remoteCache = remoteCacheManager.getCache(cacheName);

    if (remoteCache == null) {
      throw new RuntimeException("Cache '" + cacheName + "' not found. Please make sure the server is properly configured");
    }
  }

  private void insertXML() {
    DataFormat xml = DataFormat.builder().valueType(MediaType.APPLICATION_XML).valueMarshaller(new UTF8StringMarshaller()).keyType(MediaType.TEXT_PLAIN).build();
//    DataFormat xml = DataFormat.builder().valueType(MediaType.APPLICATION_XML).keyType(MediaType.TEXT_PLAIN).keyMarshaller(new UTF8StringMarshaller()).valueMarshaller(new UTF8StringMarshaller()).build();
    remoteCache.withDataFormat(xml).put("xml", "<person><firstname>Wolf</firstname><lastname>Fink</lastname></person>");
    remoteCache.withDataFormat(xml).put("xmlWithCR", "<person>\n  <firstname>Wolf</firstname>\n  <lastname>Fink</lastname>\n</person>");
    remoteCache.withDataFormat(xml).put("xmlInvalid", "<person><firstname>Wolf</firstname><lastname>InvalidXML</person>");
    remoteCache.withDataFormat(xml).put("xmlText", "A simple String with client side XML encoding");
  }

  private void insertText() {
    System.out.println("Inserting text entries into cache...");
    remoteCache.put("text", "A simple String\n for XML encoding");
    remoteCache.put("invalidXmlAsText", "<invalid>Not a valid XML content");
    remoteCache.put("xmlAsText", "<name>Wolf</name>");

    System.out.println("  size -> " + remoteCache.size());
  }

  private void getEntries() {
    List<String> l = Arrays.asList("xml", "xmlWithCR", "xmlInvalid", "xmlText", "text", "xmlAsText", "invalidXmlAsText");
    for (String key : l) {
      String m = remoteCache.get(key);
      if (m != null) {
        System.out.println(key + " >> " + m);
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
    String cacheName = "XmlCache";

    if (args.length > 0) {
      port = args[0];
    }
    if (args.length > 1) {
      port = args[1];
    }
    XmlClient client = new XmlClient(host, port, cacheName);

    client.insertXML();
    client.insertText();
    client.getEntries();

    client.stop();
    System.out.println("\nDone !");
  }
}
