package com.framevault.shared;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class Failures {

  private Failures() {}

  public static ResponseStatusException missing() {
    return new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Không tìm thấy dữ liệu hoặc không có quyền truy cập");
  }

  public static ResponseStatusException bad(String text) {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, text);
  }

  public static ResponseStatusException conflict(String text) {
    return new ResponseStatusException(HttpStatus.CONFLICT, text);
  }
}
