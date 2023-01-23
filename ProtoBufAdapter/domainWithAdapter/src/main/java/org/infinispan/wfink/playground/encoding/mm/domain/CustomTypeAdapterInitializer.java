package org.infinispan.wfink.playground.encoding.mm.domain;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

/**
 * The ProtoSchemaBuilder to use the {@link CustomTypeEntryAdapter} implementation as Adapter to marshall the {@link CustomTypeEntry} in a full compatible way to the legacy object with MessageMarshaller.<br/>
 * The generated ProtoBuf schema will and the data stored with the legacy implementation will be compatible.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
@AutoProtoSchemaBuilder(includeClasses = { CustomTypeEntryAdapter.class }, schemaFileName = "customadapter.proto", schemaFilePath = "proto", schemaPackageName = "playground")
public interface CustomTypeAdapterInitializer extends GeneratedSchema {
}
