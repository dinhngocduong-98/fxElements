package com.framevault.domain.model;

import java.time.Instant;

public class DownloadAudit {

  private Long id;

  private Long userId;

  private String entitlementId;

  private String action;

  private Instant createdAt = Instant.now();

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

  public String getEntitlementId() {
    return entitlementId;
  }

  public void setEntitlementId(String value) {
    this.entitlementId = value;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String value) {
    this.action = value;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant value) {
    this.createdAt = value;
  }
}
