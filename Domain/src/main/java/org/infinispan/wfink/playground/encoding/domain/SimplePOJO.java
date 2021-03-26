package org.infinispan.wfink.playground.encoding.domain;

import java.io.Serializable;

/**
 * A simple POJO implementation to demonstrate the behavior of Marshalling and Encoding for Infinispan caches.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
public class SimplePOJO implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  public final String firstValue;
  public final String secondValue;

  public SimplePOJO(String firstValue, String secondValue) {
    super();
    this.firstValue = firstValue;
    this.secondValue = secondValue;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((firstValue == null) ? 0 : firstValue.hashCode());
    result = prime * result + ((secondValue == null) ? 0 : secondValue.hashCode());
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
    SimplePOJO other = (SimplePOJO) obj;
    if (firstValue == null) {
      if (other.firstValue != null)
        return false;
    } else if (!firstValue.equals(other.firstValue))
      return false;
    if (secondValue == null) {
      if (other.secondValue != null)
        return false;
    } else if (!secondValue.equals(other.secondValue))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "SimplePOJO [firstValue=" + firstValue + ", secondValue=" + secondValue + "]";
  }
}
