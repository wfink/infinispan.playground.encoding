package org.infinispan.wfink.playground.encoding;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

/**
 * This is the ProtoSchemaBuilder created to use Java Maps directly as this support is missing from the protostream API by default. The implementation will be tracked by <a href="https://issues.redhat.com/browse/ISPN-14438">ISPN-14438</a> </br>
 * includeClasses can be used to list all classes separate instead of a complete package with basePackage.
 *
 * includeClasses = { HashtableAdapter.class, HashtableAdapter.HashTableEntry.class }
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
@AutoProtoSchemaBuilder(schemaFilePath = "proto/", schemaFileName = "java-api.proto", basePackages = { "org.infinispan.wfink.playground.encoding.adapter" })
public interface JavaAPIProtoSchemaInitializer extends GeneratedSchema {
}
