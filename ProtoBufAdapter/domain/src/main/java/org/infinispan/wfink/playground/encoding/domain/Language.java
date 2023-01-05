package org.infinispan.wfink.playground.encoding.domain;

import org.infinispan.protostream.annotations.ProtoEnumValue;

public enum Language {
  @ProtoEnumValue()
  ENGLISH,
  @ProtoEnumValue(number = 1, name = "DE")
  GERMAN,
  @ProtoEnumValue(number = 2, name = "IT")
  ITALIAN,
  @ProtoEnumValue(number = 3, name = "E")
  SPANISH,
  @ProtoEnumValue(number = 4, name = "FR")
  FRENCH;

}