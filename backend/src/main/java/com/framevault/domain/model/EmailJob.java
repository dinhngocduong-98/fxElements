package com.framevault.domain.model;

import java.time.Instant;

public class EmailJob {

  private String id;

  private String recipient;

  private String subject;

  private String body;

  private String status;

  private int attempts;

  private Instant nextAttempt;

  private Instant leaseUntil;

  private String lastError;

  private Instant createdAt = Instant.now();

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

  public String getBody() {
    return body;
  }

  public void setBody(String value) {
    this.body = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
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

  public Instant getLeaseUntil() {
    return leaseUntil;
  }

  public void setLeaseUntil(Instant value) {
    this.leaseUntil = value;
  }

  public String getLastError() {
    return lastError;
  }

  public void setLastError(String value) {
    this.lastError = value;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant value) {
    this.createdAt = value;
  }
}
