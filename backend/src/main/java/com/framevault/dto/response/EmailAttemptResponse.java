package com.framevault.dto.response;

import java.time.Instant;

public class EmailAttemptResponse {

  private boolean success;

  @com.fasterxml.jackson.annotation.JsonProperty("error_code")
  private String errorCode;

  @com.fasterxml.jackson.annotation.JsonProperty("created_at")
  private Instant createdAt;

  public boolean getSuccess() {
    return success;
  }

  public void setSuccess(boolean value) {
    this.success = value;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String value) {
    this.errorCode = value;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant value) {
    this.createdAt = value;
  }
}
