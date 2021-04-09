package org.infinispan.wfink.playground.encoding.mm.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.infinispan.protostream.MessageMarshaller;

/**
 * Implementation of MessageMarshaller to handle custom classes which are not supported out-of-the-box from protostream.
 *
 * <b> Note that this implementation has been replaced by ProtoAdaptor annotation since Infinispan 12</b>
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class BookMarshaller implements MessageMarshaller<Book> {

  @Override
  public Class<? extends Book> getJavaClass() {
    return Book.class;
  }

  @Override
  public String getTypeName() {
    return "playground.Book";
  }

  @Override
  public void writeTo(ProtoStreamWriter writer, Book book) throws IOException {
    writer.writeString("id", book.id.toString());
    writer.writeString("title", book.title);
    writer.writeString("description", book.description);
    writer.writeInt("publicationYear", book.publicationYear);
    writer.writeCollection("authors", book.authors, Author.class);
  }

  @Override
  public Book readFrom(MessageMarshaller.ProtoStreamReader reader) throws IOException {
    final String id = reader.readString("id");
    final String title = reader.readString("title");
    final String description = reader.readString("description");
    final int publicationYear = reader.readInt("publicationYear");
    final List<Author> authors = reader.readCollection("authors", new ArrayList<>(), Author.class);
    return new Book(id, title, description, publicationYear, authors);
  }
}
