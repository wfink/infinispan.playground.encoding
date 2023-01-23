package org.infinispan.wfink.playground.encoding.mm.hotrod;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.wfink.playground.encoding.mm.domain.CustomTypeEntry;

/**
 * <p>
 * An example to demostrate a compatible way to use legacy caches with Message Marshalling, or have the full programatic control of the serialization and marshalling. The ProtoStreamMarshaller will support primitive/scalar types without explicit definition. Because of this only custom classes or
 * unsupported classes like BigInteger need an adapter.
 * </p>
 * <p>
 * Note that this client is mostly the same as CustomTypeEntryClient except the construction for the Initializer class to demonstrate the different options of implementation.
 * </p>
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class CustomTypeEntryAdapterClient {
  private RemoteCacheManager remoteCacheManager;
  private RemoteCache<String, CustomTypeEntry> cache;

  public CustomTypeEntryAdapterClient(String host, String port, String cacheName) {
    ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
    remoteBuilder.addServer().host(host).port(Integer.parseInt(port)); // .marshaller(new ProtoStreamMarshaller()); // The Protobuf based marshaller is no longer required for query capabilities as it is the default for ISPN 11 and RHDG 8

    // There is an enhancement request to not use generated code -> https://issues.redhat.com/browse/ISPN-12963
    // add the Initializer as String will work
    remoteBuilder.addContextInitializer("org.infinispan.wfink.playground.encoding.mm.domain.CustomTypeAdapterInitializerImpl");
    // add the Initializer directly as class instance to get compiler errors if missed

    remoteCacheManager = new RemoteCacheManager(remoteBuilder.build()); // registerSchema need a cacheManager

    cache = remoteCacheManager.getCache(cacheName);

    if (cache == null) {
      throw new RuntimeException("Cache '" + cacheName + "' not found. Please make sure the server is properly configured");
    }

  }

  /**
   * Register the Protobuf schemas with the client and then register the schemas with the server too if requested. Note if the server does not have the schema some operations (like queries) are failing Also other type of clients can not read the content! The code should be hidden by having a method
   * to register the schema and/or push it to the server -> https://issues.redhat.com/browse/ISPN-12964
   *
   * @param updateServer true if the server should be updated with the schema
   */
  private void registerSchemas(GeneratedSchema initializer) {
    // Cache to register the schemas with the server too
    final RemoteCache<String, String> protoMetadataCache = remoteCacheManager.getCache("___protobuf_metadata"); // ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME is only available if query dependency is set
    // add the schema to the server side
    protoMetadataCache.put(initializer.getProtoFileName(), initializer.getProtoFile());

    // check for definition error for the registered protobuf schemas
    String errors = protoMetadataCache.get(".errors"); // ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX is only available if query dependency is set
    if (errors != null) {
      throw new IllegalStateException("Some Protobuf schema files contain errors: " + errors + "\nSchema :\n" + initializer.getProtoFileName());
    }
  }

  private void insertCustomEntries() {
    System.out.println("Inserting TestEntries into cache...");

    cache.put("100", new CustomTypeEntry("Entry100", new BigInteger("13763753091226345046315979581580902400000000")));
    cache.put("200", new CustomTypeEntry("Entry200", new BigInteger("523022617466601111760007224100074291200000000")));
    cache.put("300", new CustomTypeEntry("Entry300", new BigInteger("20397882081197443358640281739902897356800000000")));
    cache.put("400", new CustomTypeEntry("Entry400", new BigInteger("815915283247897734345611269596115894272000000000")));

    System.out.println("  -> " + cache.size() + " current size");
  }

  private void getCustomEntries() {
    System.out.println("read all Custom Entries...");
    for (Map.Entry<String, CustomTypeEntry> m : cache.entrySet()) {
      System.out.println(">> " + m);

    }
    System.out.println("CustomTypeCache size is " + cache.size());
  }

  private void queryStatic() {
    QueryFactory queryFactory = Search.getQueryFactory(cache);
    Query<CustomTypeEntry> query = queryFactory.create("FROM playground.CustomTypeEntry e WHERE e.description = 'Entry100'");
    System.out.println("Execute query description");
    List<CustomTypeEntry> list = query.execute().list();
    System.out.println("Result : " + list);

    // will not work for embedded entities
    query = queryFactory.create("FROM playground.CustomTypeEntry e WHERE e.bigInt = 2039788208119744335864028173990289735680000000");
    System.out.println("Execute query bigInt");
    try {
      list = query.execute().list();
      System.out.println("Result : " + list);
    } catch (Exception e) {
      System.out.println("Failed unexpected, message : " + e.getMessage());
    }
  }

  private void queryWithParameter() {
    QueryFactory queryFactory = Search.getQueryFactory(cache);
    Query<CustomTypeEntry> query = queryFactory.create("FROM playground.CustomTypeEntry e WHERE e.description = :desc");
    query.setParameter("desc", "Entry100");
    System.out.println("Execute parameter query description");
    List<CustomTypeEntry> list = query.execute().list();
    System.out.println("Result : " + list);

    // currently failing with "No Marshaller for BitInteger" even if registered
    // this will be implemented with ISPN-14453
    query = queryFactory.create("FROM playground.CustomTypeEntry e WHERE e.bigInt = :bigInt");
    query.setParameter("bigInt", new BigInteger("20397882081197443358640281739902897356800000000"));
    System.out.println("Execute parameter query bigInt");
    try {
      list = query.execute().list();
      System.out.println("Result : " + list);
    } catch (Exception e) {
      System.out.println("Failed as expected because of missing support for BigInteger ISPN-14453, message : " + e.getMessage());
    }
  }

  private void stop() {
    remoteCacheManager.stop();
  }

  public static void main(String[] args) throws IOException {
    String host = "localhost";
    String port = "11222";
    String cacheName = "CustomTypeCache";

    if (args.length > 0) {
      port = args[0];
    }
    if (args.length > 1) {
      port = args[1];
    }
    CustomTypeEntryAdapterClient client = new CustomTypeEntryAdapterClient(host, port, cacheName);

    client.getCustomEntries(); // get all from cache
    client.insertCustomEntries(); // insert some
    client.queryStatic();
    client.queryWithParameter();

    client.stop();
    System.out.println("\nDone !");
  }
}