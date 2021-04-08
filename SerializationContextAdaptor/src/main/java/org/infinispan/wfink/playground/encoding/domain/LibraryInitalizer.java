package org.infinispan.wfink.playground.encoding.domain;

import org.infinispan.protostream.SerializationContextInitializer;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = { Author.class, Book.class }, schemaFileName = "library.proto", schemaFilePath = "proto", schemaPackageName = "library")
public interface LibraryInitalizer extends SerializationContextInitializer {

}
