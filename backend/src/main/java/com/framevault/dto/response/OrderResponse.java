package com.framevault.dto.response;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OrderResponse {

  private String id;

  private String email;

  private String currency;

  private String total;

  private String status;

  @com.fasterxml.jackson.annotation.JsonProperty("created_at")
  private Instant createdAt;

  @com.fasterxml.jackson.annotation.JsonProperty("expires_at")
  private Instant expiresAt;

  private List<OrderItemResponse> items = new ArrayList<>();

  public String getId() {
    return id;
  }

  public void setId(String value) {
    this.id = value;
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

  public String getTotal() {
    return total;
  }

  public void setTotal(String value) {
    this.total = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
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

  public List<OrderItemResponse> getItems() {
    return items;
  }

  public void setItems(List<OrderItemResponse> value) {
    this.items = value;
  }
}
