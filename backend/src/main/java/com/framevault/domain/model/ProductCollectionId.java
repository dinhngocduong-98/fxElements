package com.framevault.domain.model;

import java.io.Serializable;

public class ProductCollectionId implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long productId;

  private Long collectionId;

  public ProductCollectionId() {}

  public ProductCollectionId(Long productId, Long collectionId) {
    this.productId = productId;
    this.collectionId = collectionId;
  }

  @Override
  public boolean equals(Object value) {
    if (this == value) return true;
    if (!(value instanceof ProductCollectionId)) return false;
    ProductCollectionId other = (ProductCollectionId) value;
    return java.util.Objects.equals(productId, other.productId)
        && java.util.Objects.equals(collectionId, other.collectionId);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(productId, collectionId);
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long value) {
    this.productId = value;
  }

  public Long getCollectionId() {
    return collectionId;
  }

  public void setCollectionId(Long value) {
    this.collectionId = value;
  }
}
