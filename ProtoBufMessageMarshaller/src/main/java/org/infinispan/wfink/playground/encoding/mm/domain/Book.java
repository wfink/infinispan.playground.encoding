package org.infinispan.wfink.playground.encoding.mm.domain;

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

  @ProtoField(number = 1)
  public String id;

  @ProtoField(number = 2)
  public String title;

  @ProtoField(number = 3)
  public String description;

  // why is defaultValue needed if 'final' or set required -> compilation error
  @ProtoField(number = 4, defaultValue = "-1")
  public int publicationYear;

  @ProtoField(number = 5, collectionImplementation = ArrayList.class)
  public List<Author> authors;

  @ProtoFactory
  public Book(String id, String title, String description, int publicationYear, List<Author> authors) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.publicationYear = publicationYear;
    this.authors = new Vector<Author>(authors);
  }

  public Book(UUID id, String title, String description, int publicationYear, List<Author> authors) {
    this(id.toString(), title, description, publicationYear, authors);
  }

  public Book(String title, String description, int publicationYear, List<Author> authors) {
    this(UUID.randomUUID().toString(), title, description, publicationYear, authors);
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
    return "Book [id=" + id + " title=" + title + ", description=" + description + (publicationYear > -1 ? ", publicationYear=" + publicationYear : "") + ", author(s)=" + authors + "]";
  }
}
