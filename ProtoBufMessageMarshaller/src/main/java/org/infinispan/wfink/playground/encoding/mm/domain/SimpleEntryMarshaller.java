package org.infinispan.wfink.playground.encoding.mm.domain;

import java.io.IOException;
import java.util.LinkedList;

import org.infinispan.protostream.MessageMarshaller;

/**
 * Implementation of MessageMarshaller to handle the custom class SimpleEntry which includes supporterd field types but with some modifications for the ProtoBuf schema generator.<br/>
 * The migration result will be shown within the ProtoBufAdaptor project, note that this implementation is not longer needed after migration.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */

public class SimpleEntryMarshaller implements MessageMarshaller<SimpleEntry> {

  @Override
  public Class<? extends SimpleEntry> getJavaClass() {
    return SimpleEntry.class;
  }

  @Override
  public String getTypeName() {
    return "playground." + SimpleEntry.class.getSimpleName();
  }

  @Override
  public void writeTo(ProtoStreamWriter writer, SimpleEntry testEntry) throws IOException {
    writer.writeString("description", testEntry.getDescription());
    writer.writeInt("intDefault", testEntry.getIntDefault());
    writer.writeInt("fix32", testEntry.getFixed32());
    writer.writeCollection("text", testEntry.getText(), String.class);
  }

  @Override
  public SimpleEntry readFrom(MessageMarshaller.ProtoStreamReader reader) throws IOException {
    SimpleEntry x = new SimpleEntry();

    x.setDescription(reader.readString("description"));
    x.setIntDefault(reader.readInt("intDefault"));
    x.setFixed32(reader.readInt("fix32"));
    x.setText(reader.readCollection("text", new LinkedList<String>(), String.class));

    return x;
  }
}
