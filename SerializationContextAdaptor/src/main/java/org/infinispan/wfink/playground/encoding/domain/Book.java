package org.infinispan.wfink.playground.encoding.domain;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class Book {

  @ProtoField(number = 1, required = true)
  final String title;

  @ProtoField(number = 2)
  final String description;

  // why is defaultValue needed if required -> compilation error
  @ProtoField(number = 3, required = true, defaultValue = "-1")
  final int publicationYear;

  @ProtoFactory
  public Book(String title, String description, int publicationYear) {
    super();
    this.title = title;
    this.description = description;
    this.publicationYear = publicationYear;
  }

}
