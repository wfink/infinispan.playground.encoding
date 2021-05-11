package org.infinispan.wfink.playground.encoding.mm.domain;

import java.util.Collection;

/**
 * <p>
 * Simple class example to be stored with the Marshaller implementation with Infinispan HotRod client. The example is meant to show how the legacy implementation can be migrated to the new ProtoAdapter approach with Infinispan 12.
 * </p>
 * <p>
 * The migrated class will be shown within the ProtoBufAdaptor project.
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Collection<String> getText() {
    return text;
  }

  public void setText(Collection<String> text) {
    this.text = text;
  }

  public int getIntDefault() {
    return intDefault;
  }

  public void setIntDefault(int intDefault) {
    this.intDefault = intDefault;
  }

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
