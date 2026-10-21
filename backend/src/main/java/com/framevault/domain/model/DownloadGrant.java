package com.framevault.domain.model;

import java.time.Instant;

public class DownloadGrant {

  private String id;

  private String entitlementId;

  private String tokenHash;

  private Instant expiresAt;

  private boolean revoked;

  private DownloadEntitlement entitlement;

  public String getId() {
    return id;
  }

  public void setId(String value) {
    this.id = value;
  }

  public String getEntitlementId() {
    return entitlementId;
  }

  public void setEntitlementId(String value) {
    this.entitlementId = value;
  }

  public String getTokenHash() {
    return tokenHash;
  }

  public void setTokenHash(String value) {
    this.tokenHash = value;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(Instant value) {
    this.expiresAt = value;
  }

  public boolean getRevoked() {
    return revoked;
  }

  public void setRevoked(boolean value) {
    this.revoked = value;
  }

  public DownloadEntitlement getEntitlement() {
    return entitlement;
  }

  public void setEntitlement(DownloadEntitlement value) {
    this.entitlement = value;
  }
}
