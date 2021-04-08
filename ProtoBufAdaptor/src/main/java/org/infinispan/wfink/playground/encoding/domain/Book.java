package org.infinispan.wfink.playground.encoding.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

/**
 * Simplified immutable object with direct access for the fields.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class Book {

  @ProtoField(number = 1, required = true)
  public final UUID id;

  @ProtoField(number = 2, required = true)
  public final String title;

  @ProtoField(number = 3)
  public final String description;

  // why is defaultValue needed if required -> compilation error
  @ProtoField(number = 4, required = true, defaultValue = "-1")
  public final int publicationYear;

  @ProtoField(number = 5, collectionImplementation = ArrayList.class)
  public final List<Author> authors;

  @ProtoFactory
  public Book(UUID id, String title, String description, int publicationYear, List<Author> authors) {
    super();
    this.id = id;
    this.title = title;
    this.description = description;
    this.publicationYear = publicationYear;
    this.authors = new Vector<Author>(authors);
  }

  public Book(String title, String description, int publicationYear, List<Author> authors) {
    this(UUID.randomUUID(), title, description, publicationYear, authors);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + publicationYear;
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Book other = (Book) obj;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (publicationYear != other.publicationYear)
      return false;
    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Book [id=" + id + " title=" + title + ", description=" + description + ", publicationYear=" + publicationYear + ", author(s)=" + authors + "]";
  }
}
