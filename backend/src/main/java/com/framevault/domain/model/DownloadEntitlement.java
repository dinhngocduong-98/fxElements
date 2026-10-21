package com.framevault.domain.model;

import java.time.Instant;

public class DownloadEntitlement {

  private String id;

  private Long userId;

  private Long orderItemId;

  private String status;

  private Instant createdAt = Instant.now();

  private User user;

  private OrderItem orderItem;

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

  public Long getOrderItemId() {
    return orderItemId;
  }

  public void setOrderItemId(Long value) {
    this.orderItemId = value;
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

  public User getUser() {
    return user;
  }

  public void setUser(User value) {
    this.user = value;
  }

  public OrderItem getOrderItem() {
    return orderItem;
  }

  public void setOrderItem(OrderItem value) {
    this.orderItem = value;
  }
}
