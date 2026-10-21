package com.framevault.validation;

import java.util.List;

public class InputValidationException extends RuntimeException {

  private final List<String> fields;

  public InputValidationException(List<String> fields) {
    super("Dữ liệu không hợp lệ");
    this.fields = List.copyOf(fields);
  }

  public List<String> getFields() {
    return fields;
  }
}
