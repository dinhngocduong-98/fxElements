package com.framevault.dto.response;

import java.time.Instant;

public class AuditResponse {

  private Long id;

  private String actor;

  private String action;

  private String target;

  @com.fasterxml.jackson.annotation.JsonProperty("created_at")
  private Instant createdAt;

  public Long getId() {
    return id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public String getActor() {
    return actor;
  }

  public void setActor(String value) {
    this.actor = value;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String value) {
    this.action = value;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String value) {
    this.target = value;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant value) {
    this.createdAt = value;
  }
}
