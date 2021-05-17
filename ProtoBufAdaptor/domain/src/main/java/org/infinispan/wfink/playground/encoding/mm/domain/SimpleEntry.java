package org.infinispan.wfink.playground.encoding.mm.domain;

import java.util.Collection;
import java.util.LinkedList;

import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.descriptors.Type;

/**
 * <p>
 * Simple class example to be stored with the Marshaller implementation with Infinispan HotRod client. The example is meant to show how the legacy implementation can be migrated to the new ProtoAdapter approach with Infinispan 12.
 * </p>
 * <p>
 * This is the migrated class and it should be the same as the ProtoBufMessageMarshaller example but have the different fields with @ProtoField annotations.
 * </p>
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class SimpleEntry {

  private String description;
  private Collection<String> text;
  private int intDefault;
  private Integer fixed32;

  public SimpleEntry() {
  }

  public SimpleEntry(String desc, Collection<String> text, int intDefault, int fixed32) {
    this.description = desc;
    this.text = text;
    this.intDefault = intDefault;
    this.fixed32 = fixed32;
  }

  @ProtoField(number = 1)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * As the legacy implementation Marshaller used the LinkedList as implementation we ensure it will be the same.
   */
  @ProtoField(number = 4, collectionImplementation = LinkedList.class)
  public Collection<String> getText() {
    return text;
  }

  public void setText(Collection<String> text) {
    this.text = text;
  }

  /**
   * A scalar int type is not nullable and a default must be set, using "0" will have the same result as the Java default.
   */
  @ProtoField(number = 2, defaultValue = "0")
  public int getIntDefault() {
    return intDefault;
  }

  public void setIntDefault(int intDefault) {
    this.intDefault = intDefault;
  }

  /**
   * Set the type for the Protobuf message as the legacy implementation used that and from the storage format it is not compatible with the default.
   */
  @ProtoField(number = 3, type = Type.FIXED32)
  public Integer getFixed32() {
    return fixed32;
  }

  public void setFixed32(Integer fixed32) {
    this.fixed32 = fixed32;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((fixed32 == null) ? 0 : fixed32.hashCode());
    result = prime * result + intDefault;
    result = prime * result + ((text == null) ? 0 : text.hashCode());
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
    SimpleEntry other = (SimpleEntry) obj;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (fixed32 == null) {
      if (other.fixed32 != null)
        return false;
    } else if (!fixed32.equals(other.fixed32))
      return false;
    if (intDefault != other.intDefault)
      return false;
    if (text == null) {
      if (other.text != null)
        return false;
    } else if (!text.equals(other.text))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "SimpleEntry [description=" + description + ", text=" + text + ", intDefault=" + intDefault + ", fixed32=" + fixed32 + "]   collection is " + text.getClass();
  }
}
