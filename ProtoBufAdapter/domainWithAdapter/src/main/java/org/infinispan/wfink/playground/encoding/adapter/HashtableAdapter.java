package org.infinispan.wfink.playground.encoding.adapter;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.infinispan.protostream.WrappedMessage;
import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

/**
 * The adapter is needed because the current Infinispan 14 does not support HashMap by default, this is tracked by <a href="https://issues.redhat.com/browse/ISPN-14438">ISPN-14438</a> </br>
 * Read <a href="https://infinispan.org/docs/stable/titles/encoding/encoding.html#protostream-annotations_marshalling">ProtoStream annotations</a> for more information.
 *
 * @author <a href="mailto:WolfDieter.Fink@gmail.com">Wolf-Dieter Fink</a>
 */
@ProtoAdapter(Hashtable.class)
public class HashtableAdapter {
  private static final Logger log = Logger.getLogger(HashtableAdapter.class.getName());

  public static class HashTableEntry {
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
  public List<HashTableEntry> getMapEntries(Hashtable<?, ?> table) {
    log.finer("HashtableAdapter.getMapEntries() is called.");
    return table.entrySet().stream().map(this::createMapEntry).collect(Collectors.toList());
  }

  /**
   * The method name allows an arbitrary name. Will be called when executing `cache.get`.
   *
   * @param mapEntries param name must match the getMapEntries method
   * @return
   */
  @ProtoFactory
  public Hashtable<?, ?> create(List<HashTableEntry> mapEntries) {
    log.finer("HashtableAdapter.create() is called.");

    Hashtable<Object, Object> hashtable = new Hashtable<>();
    mapEntries.forEach(e -> {
      hashtable.put(e.getKey().getValue(), e.getValue().getValue());
      log.finest("KEY: " + e.getKey().getValue() + ", VALUE: " + e.getValue().getValue() + "\n");
    });
    return hashtable;
  }

  protected HashTableEntry createMapEntry(Map.Entry<?, ?> entry) {
    HashTableEntry mapEntry = new HashTableEntry();
    mapEntry.setKey(new WrappedMessage(entry.getKey()));
    mapEntry.setValue(new WrappedMessage(entry.getValue()));
    return mapEntry;
  }
}
