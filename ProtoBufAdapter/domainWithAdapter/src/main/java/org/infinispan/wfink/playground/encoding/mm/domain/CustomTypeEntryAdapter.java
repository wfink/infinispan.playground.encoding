package org.infinispan.wfink.playground.encoding.mm.domain;

import java.math.BigInteger;

import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

/**
 * A custom implementation used as Adapter to marshall the CustomTypeEntry in a data compatible way to the legacy object with MessageMarshaller.<br/>
 * Note the @Proto annotations of CustomTypeEntry are ignored as the one from this class will be used.<br/>
 * The generated ProtoBuf schema will and the data stored with the legacy implementation will be compatible.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
@ProtoAdapter(CustomTypeEntry.class)
public class CustomTypeEntryAdapter {

  @ProtoFactory
  public CustomTypeEntry create(String description, String bigInt) {
    return new CustomTypeEntry(description, new BigInteger(bigInt));
  }

  @ProtoField(number = 1, required = true)
  public String getDescription(CustomTypeEntry t) {
    return t.description;
  }

  @ProtoField(number = 2, required = true)
  public String getBigInt(CustomTypeEntry t) {
    return t.bigInt.toString();
  }
}
