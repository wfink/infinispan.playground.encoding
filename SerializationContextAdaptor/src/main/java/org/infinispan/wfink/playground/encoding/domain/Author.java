package org.infinispan.wfink.playground.encoding.domain;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class Author {
  @ProtoField(number = 1)
  final String name;

  @ProtoField(number = 2)
  final String firstname;

  @ProtoFactory
  Author(String name, String firstname) {
    this.name = name;
    this.firstname = firstname;
  }

  public String getName() {
    return name;
  }

  public String getFirstname() {
    return firstname;
  }
}