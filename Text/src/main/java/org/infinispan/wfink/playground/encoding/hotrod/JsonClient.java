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
 * A simple client which uses JSON as String with and without setting the encoding. The JSON encoded cache will transcode the value accordingly.
 * <p>
 * The DataFormat might use a different Marshaller with .keyMarshaller(...) .valueMarshaller(...).
 * </p>
 * <p>
 * Precedence is,
 * <ul>
 * <li>Marshaller defined in the RemoteCacheManager</li>
 * <li>cache specific Marshaller</li>
 * <li>Marshaller associated with the mediatype passed to DataFormatBuilder</li>
 * <li>Marshaller passed to the DataFormatBuilder</li>
 * </ul>
 * </p>
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class JsonClient {
  private RemoteCacheManager remoteCacheManager;
  private RemoteCache<String, String> remoteCache;

  public JsonClient(String host, String port, String cacheName) {
    ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
    remoteBuilder.addServer().host(host).port(Integer.parseInt(port));
//    remoteBuilder.marshaller(new ProtoStreamMarshaller()); // this is basically the default since ISPN 12
    remoteBuilder.marshaller(new UTF8StringMarshaller());

    remoteCacheManager = new RemoteCacheManager(remoteBuilder.build());

    remoteCache = remoteCacheManager.getCache(cacheName);

    if (remoteCache == null) {
      throw new RuntimeException("Cache '" + cacheName + "' not found. Please make sure the server is properly configured");
    }
  }

  private void insertJSON() {
    System.out.println("Inserting JSON entries into cache...");

    // It is recommended to have the reference for the cache stored and not created at every access. Here it is just to show the different behaviour without!
//    DataFormat json = DataFormat.builder().valueType(MediaType.APPLICATION_JSON).build();
    DataFormat json = DataFormat.builder().valueType(MediaType.APPLICATION_JSON).keyType(MediaType.TEXT_PLAIN).keyMarshaller(new UTF8StringMarshaller()).valueMarshaller(new UTF8StringMarshaller()).build();
    RemoteCache<String, String> r = remoteCache.withDataFormat(json);
    r.put("json1", "{\n" + "  \"local-cache\" : {\n" + "    \"encoding\" : {\n" + "      \"key\" : {\n" + "        \"media-type\" : \"text/plain\"\n" + "      },\n" + "      \"value\" : {\n" + "        \"media-type\" : \"application/json\"\n" + "      }\n" + "    }\n" + "  }\n" + "}\n" + "");
    r.put("json2", "{ \"local-cache\" : { \"encoding\" : { \"key\" : {  \"media-type\" : \"text/plain\"  },  \"value\" : {  \"media-type\" : \"application/json\"  }  }  }  }");

    System.out.println(" size -> " + remoteCache.size());
  }

  private void insertText() {
    System.out.println("Inserting text entries into cache...");
    remoteCache.put("text1",
        "{\n" + "  \"local-cache\" : {\n" + "    \"encoding\" : {\n" + "      \"key\" : {\n" + "        \"media-type\" : \"text/plain\"\n" + "      },\n" + "      \"value\" : {\n" + "        \"media-type\" : \"application/json\"\n" + "      }\n" + "    }\n" + "  }\n" + "}\n" + "");
    remoteCache.put("text2", "{ \"local-cache\" : { \"encoding\" : { \"key\" : {  \"media-type\" : \"text/plain\"  },  \"value\" : {  \"media-type\" : \"application/json\"  }  }  }  }");

    System.out.println(" size -> " + remoteCache.size());
  }

  private void getTextEntries() {
    List<String> l = Arrays.asList("text1", "text2");
    System.out.println("Read entries as text " + l + "  > ");
    for (String key : l) {
      String m = remoteCache.get(key);
      if (m != null) {
        System.out.println(key + " >> " + m);
      }
    }
  }

  private void getJsonEntries() {
    List<String> l = Arrays.asList("json1", "json2", "text1", "text2");
    DataFormat json = DataFormat.builder().valueType(MediaType.APPLICATION_JSON).keyType(MediaType.TEXT_PLAIN).keyMarshaller(new UTF8StringMarshaller()).valueMarshaller(new UTF8StringMarshaller()).build();
    RemoteCache<String, String> r = remoteCache.withDataFormat(json);
    System.out.println("Read entries as json " + l + "  > ");
    for (String key : l) {
      String m = r.get(key);
      if (m != null) {
        System.out.println(key + " >> " + m);
      }
    }
  }

  private void stop() {
    remoteCacheManager.stop();
  }

  public static void main(String[] args) {
    String host = "localhost";
    String port = "11222";
    String cacheName = "JsonCache";

    if (args.length > 0) {
      port = args[0];
    }
    if (args.length > 1) {
      port = args[1];
    }
    JsonClient client = new JsonClient(host, port, cacheName);

    client.insertText();
    client.insertJSON();
    client.getTextEntries();
    client.getJsonEntries();

    client.stop();
    System.out.println("\nDone !");
  }
}
