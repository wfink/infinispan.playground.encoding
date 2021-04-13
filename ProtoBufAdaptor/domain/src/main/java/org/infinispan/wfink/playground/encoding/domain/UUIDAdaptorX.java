package org.infinispan.wfink.playground.encoding.domain;

import java.util.UUID;

import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.descriptors.Type;

@ProtoAdapter(UUID.class)
public class UUIDAdaptorX {

  @ProtoFactory
  UUID create(Long mostSigBitsFixed, Long leastSigBitsFixed) {
    return new UUID(mostSigBitsFixed, leastSigBitsFixed);
  }

  @ProtoField(number = 1, type = Type.FIXED64, defaultValue = "0")
  Long getMostSigBitsFixed(UUID uuid) {
    return uuid.getMostSignificantBits();
  }

  @ProtoField(number = 2, type = Type.FIXED64, defaultValue = "0")
  Long getLeastSigBitsFixed(UUID uuid) {
    return uuid.getLeastSignificantBits();
  }
}