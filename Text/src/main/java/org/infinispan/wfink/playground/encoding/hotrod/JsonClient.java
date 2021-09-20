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
 * A simple client which uses JSON as String with and without set the encoding with DataFormat. The JSON encoded cache will transcode the value accordingly.
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
    // The put will fail if the marshallers are not set here because the settings for remoteCache builder are not used.
    // DataFormat json = DataFormat.builder().valueType(MediaType.APPLICATION_JSON).build();
    DataFormat json = DataFormat.builder().valueType(MediaType.APPLICATION_JSON).keyType(MediaType.TEXT_PLAIN).keyMarshaller(new UTF8StringMarshaller()).valueMarshaller(new UTF8StringMarshaller()).build();

    /*
     * TODO: why there is a difference using UTF8 or Protostream? From a user perspective the put uses simple strings Console will fail if Protostream is used ! DataFormat json = DataFormat.builder().valueType(MediaType.APPLICATION_JSON).keyType(MediaType.TEXT_PLAIN).keyMarshaller(new
     * ProtoStreamMarshaller()).valueMarshaller(new ProtoStreamMarshaller()).build();
     */

    RemoteCache<String, String> r = remoteCache.withDataFormat(json);
    r.put("json", "{\n" + "  \"person\": {\n" + "    \"name\": {\n" + "      \"family\": \"Fink\",\n" + "      \"first\" : \"Wolf\",\n" + "      \"middle\" : \"Dieter\"\n" + "    }\n" + "  }\n" + "}\n");
    r.put("jsonNoCR", "{\"person\": {\"name\": {\"family\": \"Fink\", \"first\" : \"Wolf\", \"middle\" : \"Dieter\"}}}");

    // the next 2 entries will cause issues with the console, it will be not displayed and maybe suppress the complete output
//    r.put("jsonInvalid", " \"bla\" : bla bla ");
//    r.put("jsonSimpleText", "No valid JSON but Text");

    System.out.println(" size -> " + remoteCache.size());
  }

  private void insertText() {
    System.out.println("Inserting text entries into cache...");
    remoteCache.put("textJSON", "{\n" + "  \"person\": {\n" + "    \"name\": {\n" + "      \"family\": \"Fernandez\",\n" + "      \"first\" : \"Gustavo\"\n" + "    }\n" + "  }\n" + "}\n");
    remoteCache.put("textJSONnoCR", "{\"person\": {\"name\": {\"family\": \"Fernandez\", \"first\" : \"Gustavo\"}}}");
    remoteCache.put("textInvalidJSON", "{\n" + "  person: {\n" + "    \"name\": {\n" + "      \"family\": \"Fernandez\",\n" + "      \"first\" : \"Gustavo\"\n" + "    }\n" + "  }\n" + "}\n");
    remoteCache.put("text", "Simple String");

    System.out.println(" size -> " + remoteCache.size());
  }

  private void getTextEntries() {
    List<String> l = Arrays.asList("text", "textJSON", "textJSONnoCR", "textInvalidJSON", "json", "jsonNoCR");
    System.out.printf("Read entries without encoding (as text)\n  %s\n====================================\n", l);
    for (String key : l) {
      String m = remoteCache.get(key);
      if (m != null) {
        System.out.println(key + " >> " + m);
      }
    }
    System.out.println("");
  }

  private void getJsonEntries() {
    List<String> l = Arrays.asList("json", "jsonNoCR", "jsonInvalid", "jsonSimpleText", "text1", "text2noCR");
    DataFormat json = DataFormat.builder().valueType(MediaType.APPLICATION_JSON).valueMarshaller(new UTF8StringMarshaller()).build();
    RemoteCache<String, String> r = remoteCache.withDataFormat(json);
    System.out.printf("Read entries with JSON encoding and UTF8Marshaller\n  %s\n====================================\n", l);
    for (String key : l) {
      String m = r.get(key);
      if (m != null) {
        System.out.println(key + " >> " + m);
      }
    }
    System.out.println("");
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
