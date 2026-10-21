package com.framevault.repository.page;

import java.util.*;

public final class Page<T> {

  private final List<T> content;

  private final long total;

  public Page(List<T> content, long total) {
    this.content = Collections.unmodifiableList(new ArrayList<>(content));
    this.total = total;
  }

  public List<T> getContent() {
    return content;
  }

  public long getTotalElements() {
    return total;
  }
}
