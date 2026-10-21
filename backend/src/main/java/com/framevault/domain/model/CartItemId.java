package com.framevault.domain.model;

import java.io.Serializable;

public class CartItemId implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long userId;

  private Long productId;

  public CartItemId() {}

  public CartItemId(Long userId, Long productId) {
    this.userId = userId;
    this.productId = productId;
  }

  @Override
  public boolean equals(Object value) {
    if (this == value) return true;
    if (!(value instanceof CartItemId)) return false;
    CartItemId other = (CartItemId) value;
    return java.util.Objects.equals(userId, other.userId)
        && java.util.Objects.equals(productId, other.productId);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(userId, productId);
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long value) {
    this.userId = value;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long value) {
    this.productId = value;
  }
}
