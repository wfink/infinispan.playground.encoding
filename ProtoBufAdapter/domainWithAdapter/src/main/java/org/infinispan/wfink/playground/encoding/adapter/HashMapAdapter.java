package org.infinispan.wfink.playground.encoding.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.infinispan.protostream.WrappedMessage;
import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

/**
 * The adapter is needed because the current Infinispan 14 does not support HashMap by default, this is tracked by <a href="https://issues.redhat.com/browse/ISPN-14438">ISPN-14438</a>.</br>
 * Read <a href="https://infinispan.org/docs/stable/titles/encoding/encoding.html#protostream-annotations_marshalling">ProtoStream annotations</a> for more information.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
@ProtoAdapter(HashMap.class)
public class HashMapAdapter {
  private static final Logger log = Logger.getLogger(HashMapAdapter.class.getName());

  public static class HashMapEntry {
    @ProtoField(number = 1)
    WrappedMessage key;

    @ProtoField(number = 2)
    WrappedMessage value;

    public WrappedMessage getKey() {
      return key;
    }

    public void setKey(WrappedMessage key) {
      this.key = key;
    }

    public WrappedMessage getValue() {
      return value;
    }

    public void setValue(WrappedMessage value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return "key = " + this.key + ", value = " + this.value;
    }
  }

  /**
   * Will be called this when executing `cache.put`. Needs to follow getter/setter naming rules, as it is used by this#create parameter name.
   *
   * @param map
   * @return the list of HashMap Entries
   */
  @ProtoField(number = 1)
  public List<HashMapEntry> getMapEntries(HashMap<?, ?> map) {
    log.finer("HashMapAdapter.getMapEntries() is called.");
    return map.entrySet().stream().map(this::createMapEntry).collect(Collectors.toList());
  }

  /**
   * The method name allows an arbitrary name. Will be called when executing `cache.get`.
   *
   * @param mapEntries param name must match the getMapEntries method
   * @return
   */
  @ProtoFactory
  public HashMap<?, ?> create(List<HashMapEntry> mapEntries) {
    log.finer("HashMapAdapter.create() is called.");

    HashMap<Object, Object> map = new HashMap<>();
    mapEntries.forEach(e -> {
      map.put(e.getKey().getValue(), e.getValue().getValue());
      log.finest("KEY: " + e.getKey().getValue() + ", VALUE: " + e.getValue().getValue() + "\n");
    });
    return map;
  }

  protected HashMapEntry createMapEntry(Map.Entry<?, ?> entry) {
    HashMapEntry mapEntry = new HashMapEntry();
    mapEntry.setKey(new WrappedMessage(entry.getKey()));
    mapEntry.setValue(new WrappedMessage(entry.getValue()));
    return mapEntry;
  }
}
