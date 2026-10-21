package com.framevault.shared;

import com.framevault.dto.response.ErrorResponse;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiError {

  @ExceptionHandler(com.framevault.validation.InputValidationException.class)
  public ResponseEntity<ErrorResponse> inputValidation(
      com.framevault.validation.InputValidationException e) {
    return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), e.getFields()));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> status(ResponseStatusException e) {
    return ResponseEntity.status(e.getStatus())
        .body(new ErrorResponse(Objects.toString(e.getReason(), "Yêu cầu không hợp lệ")));
  }

  @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
  public ResponseEntity<ErrorResponse> validation(BindException e) {
    return ResponseEntity.badRequest()
        .body(
            new ErrorResponse(
                "Dữ liệu không hợp lệ",
                e.getBindingResult().getFieldErrors().stream()
                    .map(f -> f.getField() + ": " + f.getDefaultMessage())
                    .collect(Collectors.toList())));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> conflict() {
    return ResponseEntity.status(409)
        .body(new ErrorResponse("Dữ liệu trùng hoặc đang được sử dụng"));
  }

  @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> denied() {
    return ResponseEntity.status(403)
        .body(new ErrorResponse("Bạn không có quyền thực hiện thao tác"));
  }

  @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
  public ResponseEntity<ErrorResponse> auth() {
    return ResponseEntity.status(401).body(new ErrorResponse("Email hoặc mật khẩu không đúng"));
  }

  @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
  public ResponseEntity<ErrorResponse> large() {
    return ResponseEntity.status(413).body(new ErrorResponse("File vượt giới hạn upload"));
  }
}
