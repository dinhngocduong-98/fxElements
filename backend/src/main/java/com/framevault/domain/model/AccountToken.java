package com.framevault.domain.model;

import java.time.Instant;

public class AccountToken {

  private Long id;

  private Long userId;

  private String tokenHash;

  private String purpose;

  private Instant expiresAt;

  private Instant usedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long value) {
    this.userId = value;
  }

  public String getTokenHash() {
    return tokenHash;
  }

  public void setTokenHash(String value) {
    this.tokenHash = value;
  }

  public String getPurpose() {
    return purpose;
  }

  public void setPurpose(String value) {
    this.purpose = value;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(Instant value) {
    this.expiresAt = value;
  }

  public Instant getUsedAt() {
    return usedAt;
  }

  public void setUsedAt(Instant value) {
    this.usedAt = value;
  }
}
