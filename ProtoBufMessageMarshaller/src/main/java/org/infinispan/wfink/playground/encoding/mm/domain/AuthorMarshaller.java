package org.infinispan.wfink.playground.encoding.mm.domain;

import java.io.IOException;

import org.infinispan.protostream.MessageMarshaller;

/**
 * Implementation of MessageMarshaller to handle custom classes which are not supported out-of-the-box from protostream.
 *
 * <b> Note that this implementation has been replaced by ProtoAdaptor annotation since Infinispan 12</b>
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class AuthorMarshaller implements MessageMarshaller<Author> {

  @Override
  public Class<? extends Author> getJavaClass() {
    return Author.class;
  }

  @Override
  public String getTypeName() {
    return "playground.Author";
  }

  @Override
  public void writeTo(ProtoStreamWriter writer, Author author) throws IOException {
    writer.writeString("name", author.name);
    writer.writeString("firstname", author.firstname);
  }

  @Override
  public Author readFrom(MessageMarshaller.ProtoStreamReader reader) throws IOException {
    final String name = reader.readString("name");
    final String firstname = reader.readString("firstname");
    return new Author(name, firstname);
  }
}
