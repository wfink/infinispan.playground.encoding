package org.infinispan.wfink.playground.encoding.domain;

import java.util.UUID;

import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.descriptors.Type;

@ProtoAdapter(UUID.class)
public class UUIDAdapter {

  @ProtoFactory
  UUID create(String stringUUID) {
    return UUID.fromString(stringUUID);
  }

  @ProtoField(number = 1, type = Type.STRING)
  String getStringUUID(UUID uuid) {
    return uuid.toString();
  }
}