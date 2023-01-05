package org.infinispan.wfink.playground.encoding.mm.domain;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import org.infinispan.protostream.types.java.math.BigIntegerAdapter;

/**
 * The ProtoSchemaBuilder to use the annotated {@link CustomTypeEntry} implementation to generate the ProtoBuf schema and Marshaller by using the {@link BigIntegerAdapter} in a <b>not</b> compatible way to the legacy object with MessageMarshaller.<br/>
 * The generated ProtoBuf schema will use an inner message for the BigInteger field and the schema and the data stored with the legacy implementation will be <b>not</b> compatible.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
@AutoProtoSchemaBuilder(includeClasses = { CustomTypeEntry.class, BigIntegerAdapter.class }, schemaFileName = "customtype.proto", schemaFilePath = "proto", schemaPackageName = "playground")
public interface CustomTypeInitializer extends GeneratedSchema {
}
