package com.anz.activemq.enumeration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public enum QueueStatus {
  //
  NEW(1, "New", ""),
  //
  SENT(2, "Sent", ""),
  //
  RECEIVED(3, "Received", ""),
  //
  COMPLETED(4, "Completed", ""),
  //
  FAILED(5, "Failed", ""),
  //
  EXPIRED(6, "Expired", "");

  private Integer id;
  private String value;
  private String desc;

  QueueStatus(Integer id, String value, String desc) {
    this.id = id;
    this.value = value;
    this.desc = desc;
  }

  public static List<QueueStatus> getAll() {
    return Arrays.asList(QueueStatus.values());
  }

  public Integer getId() {
    return id;
  }

  public String getValue() {
    return value;
  }

  public String getDesc() {
    return desc;
  }

  public static QueueStatus getStatusById(final Integer id) {
    Optional<QueueStatus> findFirst = Stream.of(QueueStatus.values()).filter(p -> p.getId() == id).findFirst();
    return findFirst.isPresent() ? findFirst.get() : null;
  }
}
