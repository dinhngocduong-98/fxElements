package com.framevault.dto.response;

import java.util.List;

public class PageResponse<T> {

  private List<T> items;

  private long total;

  private int page;

  private int size;

  public PageResponse(List<T> items, long total, int page, int size) {
    this.items = items;
    this.total = total;
    this.page = page;
    this.size = size;
  }

  public List<T> getItems() {
    return items;
  }

  public void setItems(List<T> value) {
    this.items = value;
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long value) {
    this.total = value;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int value) {
    this.page = value;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int value) {
    this.size = value;
  }
}
