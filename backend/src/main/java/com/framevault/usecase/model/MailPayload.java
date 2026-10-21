package com.framevault.usecase.model;

import java.time.Instant;

public class MailPayload {

  private final String id, to, subject, body;

  private final int attempts;

  private final Instant lease;

  public MailPayload(
      String id, String to, String subject, String body, int attempts, Instant lease) {
    this.id = id;
    this.to = to;
    this.subject = subject;
    this.body = body;
    this.attempts = attempts;
    this.lease = lease;
  }

  public String getId() {
    return id;
  }

  public String getTo() {
    return to;
  }

  public String getSubject() {
    return subject;
  }

  public String getBody() {
    return body;
  }

  public int getAttempts() {
    return attempts;
  }

  public Instant getLease() {
    return lease;
  }
}
