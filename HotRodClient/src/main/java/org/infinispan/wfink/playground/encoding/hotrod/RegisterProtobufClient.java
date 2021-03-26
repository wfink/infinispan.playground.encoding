package org.infinispan.wfink.playground.encoding.hotrod;

import java.io.IOException;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.MarshallerUtil;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.infinispan.wfink.playground.encoding.domain.Message;

/**
 * A simple client to generate the protostream schema from the classes with Proto annotations and update the server.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class RegisterProtobufClient {
  private RemoteCacheManager cacheManager;

  public RegisterProtobufClient(String host, String port) {
    ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
    remoteBuilder.addServer().host(host).port(Integer.parseInt(port));

    cacheManager = new RemoteCacheManager(remoteBuilder.build());
  }

  /**
   * Register the Protobuf schemas and marshallers with the client and then register the schemas with the server too.
   */
  private void registerSchemasAndMarshallers(boolean updateServer) {
    // Register entity marshaller on the client side ProtoStreamMarshaller
    // instance associated with the remote cache manager.
    SerializationContext ctx = MarshallerUtil.getSerializationContext(cacheManager);

    // generate the message protobuf schema file based on the annotations on Message class
    String msgSchemaFile = null;
    try {
      ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
      msgSchemaFile = protoSchemaBuilder.fileName("message.proto").packageName("playground").addClass(Message.class).build(ctx);
    } catch (IOException e) {
      throw new RuntimeException("Failed to build protobuf definition from 'Message class'", e);
    }

    // the following part is not necessary if the server already have the schema
    // Cache to register the schemas with the server too
    final RemoteCache<String, String> protoMetadataCache = cacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
    // add the schema to the server side
    protoMetadataCache.put("message.proto", msgSchemaFile);

    // check for definition error for the registered protobuf schemas
    String errors = protoMetadataCache.get(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
    if (errors != null) {
      throw new IllegalStateException("Some Protobuf schema files contain errors: " + errors + "\nSchema :\n" + msgSchemaFile);
    }
  }

  private void stop() {
    cacheManager.stop();
  }

  public static void main(String[] args) {
    String host = "localhost";
    String port = "11222";

    if (args.length > 0) {
      port = args[0];
    }
    if (args.length > 1) {
      port = args[1];
    }
    RegisterProtobufClient client = new RegisterProtobufClient(host, port);

    client.registerSchemasAndMarshallers(true);

    client.stop();
    System.out.println("\nDone !");
  }
}
