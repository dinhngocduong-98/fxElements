package com.framevault.domain.model;

import java.time.Instant;

public class EmailAttempt {

  private Long id;

  private String jobId;

  private boolean success;

  private String errorCode;

  private Instant createdAt = Instant.now();

  public Long getId() {
    return id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public String getJobId() {
    return jobId;
  }

  public void setJobId(String value) {
    this.jobId = value;
  }

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
