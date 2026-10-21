package com.framevault.repository.page;

public final class PageRequest implements Pageable {

  private final int page, size;

  private final Sort sort;

  private PageRequest(int page, int size, Sort sort) {
    if (page < 0 || size < 1) throw new IllegalArgumentException("Invalid page");
    this.page = page;
    this.size = size;
    this.sort = sort;
  }

  public static PageRequest of(int page, int size) {
    return new PageRequest(page, size, Sort.by("id"));
  }

  public static PageRequest of(int page, int size, Sort sort) {
    return new PageRequest(page, size, sort);
  }

  public int getPageNumber() {
    return page;
  }

  public int getPageSize() {
    return size;
  }

  public long getOffset() {
    return (long) page * size;
  }

  public Sort getSort() {
    return sort;
  }
}
