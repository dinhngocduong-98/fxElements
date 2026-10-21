package com.framevault.dto.response;

import java.util.Collections;
import java.util.List;

public class ErrorResponse {

  private final String message;

  private final List<String> fields;

  public ErrorResponse(String message) {
    this(message, Collections.emptyList());
  }

  public ErrorResponse(String message, List<String> fields) {
    this.message = message;
    this.fields = fields;
  }

  public String getMessage() {
    return message;
  }

  public List<String> getFields() {
    return fields;
  }
}
