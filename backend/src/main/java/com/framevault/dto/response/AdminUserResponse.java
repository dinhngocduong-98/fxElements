package com.framevault.dto.response;

import java.time.Instant;

public class AdminUserResponse extends UserResponse {

  private boolean enabled;

  @com.fasterxml.jackson.annotation.JsonProperty("created_at")
  private Instant createdAt;

  public boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(boolean value) {
    this.enabled = value;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant value) {
    this.createdAt = value;
  }
}
