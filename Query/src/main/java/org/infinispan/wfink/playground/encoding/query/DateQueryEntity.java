package org.infinispan.wfink.playground.encoding.query;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoField;

@ProtoDoc("@Indexed")
public class DateQueryEntity {

  private String text;

  private long timeUTC = 0;

  private int number = 0;

  public DateQueryEntity() {
  }

  public DateQueryEntity(String text, int number, long timeUTC) {
    this.text = text;
    this.number = number;
    this.timeUTC = timeUTC;
  }

  @ProtoDoc("@Field(store = Store.NO, index=Index.YES, analyze = Analyze.NO)")
  @ProtoField(number = 1)
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @ProtoDoc("@Field(store = Store.NO, index=Index.YES, analyze = Analyze.NO)")
  @ProtoField(number = 2, required = true)
  public long getTimeUTC() {
    return timeUTC;
  }

  public void setTimeUTC(long timeUTC) {
    this.timeUTC = timeUTC;
  }

  @ProtoDoc("@Field(store = Store.NO, index=Index.YES, analyze = Analyze.NO)")
  @ProtoField(number = 3, defaultValue = "0")
  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  @Override
  public String toString() {
    return "DateQueryEntity [text=" + text + ", timeUTC=" + timeUTC + ", number=" + number + "]";
  }

}
