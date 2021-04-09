package org.infinispan.wfink.playground.encoding.mm.domain;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

/**
 * A simple class to store an Author for a book.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class Author {
  public final String name;

  public final String firstname;

  /**
   * Default constructor is needed for Protostream
   */
  /**
   * Convenient constructor
   */
  @ProtoFactory
  public Author(String name, String firstname) {
    this.name = name;
    this.firstname = firstname;
  }

  @ProtoField(number = 1)
  public String getName() {
    return name;
  }

  @ProtoField(number = 2)
  public String getFirstname() {
    return firstname;
  }

  @Override
  public String toString() {
    return "Author [" + name + ", " + firstname + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((firstname == null) ? 0 : firstname.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    Author other = (Author) obj;
    if (firstname == null) {
      if (other.firstname != null)
        return false;
    } else if (!firstname.equals(other.firstname))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

}