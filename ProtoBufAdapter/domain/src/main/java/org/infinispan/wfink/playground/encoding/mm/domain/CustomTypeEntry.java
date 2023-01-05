package org.infinispan.wfink.playground.encoding.mm.domain;

import java.math.BigInteger;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

/**
 * A custom immutable object to demonstrate migration.<br/>
 * Note the @Proto annotations are ignored if the @ProtoAdapter approache is used to generate the Marshaller and ProtoBuf Schema to generate a data compatible migration<br/>
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class CustomTypeEntry {

  @ProtoField(number = 1)
  final String description;
  @ProtoField(number = 2)
  final BigInteger bigInt;

  /**
   * <b>Note:</b> The parameter names are required to match the field names!
   *
   * @param description
   * @param bigInt
   */
  @ProtoFactory
  public CustomTypeEntry(String description, BigInteger bigInt) {
    this.description = description;
    this.bigInt = bigInt;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((bigInt == null) ? 0 : bigInt.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
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
    CustomTypeEntry other = (CustomTypeEntry) obj;
    if (bigInt == null) {
      if (other.bigInt != null)
        return false;
    } else if (!bigInt.equals(other.bigInt))
      return false;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "CustomTypeEntry [description=" + description + ", bigInt=" + bigInt + "]";
  }
}
