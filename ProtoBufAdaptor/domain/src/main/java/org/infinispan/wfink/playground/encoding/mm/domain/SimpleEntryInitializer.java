package org.infinispan.wfink.playground.encoding.mm.domain;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = { SimpleEntry.class }, schemaFileName = "simple.proto", schemaFilePath = "proto", schemaPackageName = "playground")
public interface SimpleEntryInitializer extends GeneratedSchema {

}
