package org.infinispan.wfink.playground.encoding.mm.domain;

import java.io.IOException;
import java.math.BigInteger;

import org.infinispan.protostream.MessageMarshaller;

/**
 * Implementation of MessageMarshaller to handle the custom class CustomTypeEntry which includes field types which are not supported out-of-the-box from protostream.<br/>
 * <b> Note that this implementation has been replaced by ProtoAdaptor annotation since Infinispan 12</b><br/>
 * The migration result will be shown within the ProtoBufAdaptor project, note that this implementation is not longer needed after migration.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class CustomTypeEntryMarshaller implements MessageMarshaller<CustomTypeEntry> {

  @Override
  public Class<? extends CustomTypeEntry> getJavaClass() {
    return CustomTypeEntry.class;
  }

  @Override
  public String getTypeName() {
    return "playground." + CustomTypeEntry.class.getSimpleName();
  }

  @Override
  public void writeTo(ProtoStreamWriter writer, CustomTypeEntry testEntry) throws IOException {
    writer.writeString("description", testEntry.description);
    writer.writeString("bigInt", testEntry.bigInt.toString());
  }

  @Override
  public CustomTypeEntry readFrom(MessageMarshaller.ProtoStreamReader reader) throws IOException {
    final String desc = reader.readString("description");
    final BigInteger bInt = new BigInteger(reader.readString("bigInt"));

    return new CustomTypeEntry(desc, bInt);
  }
}
