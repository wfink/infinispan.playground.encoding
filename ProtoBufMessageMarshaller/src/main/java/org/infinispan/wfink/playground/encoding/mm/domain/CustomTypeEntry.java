package org.infinispan.wfink.playground.encoding.mm.domain;

import java.math.BigInteger;

/**
 * Simple custom immutable object to demonstrate migration from deprecated MessageMarshaller to the new ProtoAdapter approach with Infinispan 12.<br/>
 * The migration result will be shown within the ProtoBufAdaptor project.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class CustomTypeEntry {

  final String description;
  final BigInteger bigInt;

  public CustomTypeEntry(String desc, BigInteger bigInt) {
    this.description = desc;
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
