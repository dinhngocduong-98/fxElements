package com.framevault.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public class CustomerOrder {

  private String id;

  private Long userId;

  private String email;

  private String currency;

  private BigDecimal total;

  private String status;

  private String idempotencyKey;

  private Instant createdAt = Instant.now();

  private Instant expiresAt;

  private User user;

  public String getId() {
    return id;
  }

  public void setId(String value) {
    this.id = value;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long value) {
    this.userId = value;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String value) {
    this.email = value;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String value) {
    this.currency = value;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public void setTotal(BigDecimal value) {
    this.total = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public void setIdempotencyKey(String value) {
    this.idempotencyKey = value;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant value) {
    this.createdAt = value;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(Instant value) {
    this.expiresAt = value;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User value) {
    this.user = value;
  }
}
