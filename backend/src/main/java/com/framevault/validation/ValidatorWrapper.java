package com.framevault.validation;

import java.util.*;
import java.util.stream.Collectors;
import javax.validation.Validator;
import org.springframework.stereotype.Component;

/** Shared validation boundary used by Interactors, including non-HTTP callers. */
@Component
public class ValidatorWrapper {

  private final Validator validator;

  public ValidatorWrapper(Validator validator) {
    this.validator = validator;
  }

  public <T> T validate(T value) {
    if (value == null) throw new InputValidationException(List.of("request: không được null"));
    List<String> errors =
        validator.validate(value).stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .sorted()
            .collect(Collectors.toList());
    if (!errors.isEmpty()) throw new InputValidationException(errors);
    return value;
  }

  public void requireText(String field, String value) {
    if (value == null || value.isBlank()) fail(field, "không được trống");
  }

  public void requirePositive(String field, Long value) {
    if (value == null || value <= 0) fail(field, "phải lớn hơn 0");
  }

  public void requirePage(int value) {
    if (value < 0 || value > 100000) fail("page", "ngoài phạm vi cho phép");
  }

  public void requireOneOf(String field, String value, String... allowed) {
    if (value == null || !Arrays.asList(allowed).contains(value)) fail(field, "không hợp lệ");
  }

  private void fail(String field, String message) {
    throw new InputValidationException(List.of(field + ": " + message));
  }
}
