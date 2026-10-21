package com.framevault.dto.response;

import java.time.Instant;

public class EmailJobResponse {

  private String id;

  private String recipient;

  private String subject;

  private String status;

  @com.fasterxml.jackson.annotation.JsonProperty("last_error")
  private String lastError;

  private int attempts;

  @com.fasterxml.jackson.annotation.JsonProperty("next_attempt")
  private Instant nextAttempt;

  @com.fasterxml.jackson.annotation.JsonProperty("created_at")
  private Instant createdAt;

  public String getId() {
    return id;
  }

  public void setId(String value) {
    this.id = value;
  }

  public String getRecipient() {
    return recipient;
  }

  public void setRecipient(String value) {
    this.recipient = value;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String value) {
    this.subject = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
  }

  public String getLastError() {
    return lastError;
  }

  public void setLastError(String value) {
    this.lastError = value;
  }

  public int getAttempts() {
    return attempts;
  }

  public void setAttempts(int value) {
    this.attempts = value;
  }

  public Instant getNextAttempt() {
    return nextAttempt;
  }

  public void setNextAttempt(Instant value) {
    this.nextAttempt = value;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant value) {
    this.createdAt = value;
  }
}
