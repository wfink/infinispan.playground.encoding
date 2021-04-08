package org.infinispan.wfink.playground.encoding.domain;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class Author {
  @ProtoField(number = 1)
  public final String name;

  @ProtoField(number = 2)
  public final String firstname;

  @ProtoFactory
  public Author(String name, String firstname) {
    this.name = name;
    this.firstname = firstname;
  }

  public String getName() {
    return name;
  }

  public String getFirstname() {
    return firstname;
  }

  @Override
  public String toString() {
    return "Author [" + ", " + firstname + "]";
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