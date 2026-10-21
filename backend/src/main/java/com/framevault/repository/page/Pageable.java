package com.framevault.repository.page;

public interface Pageable {

  int getPageNumber();

  int getPageSize();

  long getOffset();

  Sort getSort();
}
