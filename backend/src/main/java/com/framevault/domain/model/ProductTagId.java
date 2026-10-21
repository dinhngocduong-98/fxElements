package com.framevault.domain.model;

import java.io.Serializable;

public class ProductTagId implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long productId;

  private Long tagId;

  public ProductTagId() {}

  public ProductTagId(Long productId, Long tagId) {
    this.productId = productId;
    this.tagId = tagId;
  }

  @Override
  public boolean equals(Object value) {
    if (this == value) return true;
    if (!(value instanceof ProductTagId)) return false;
    ProductTagId other = (ProductTagId) value;
    return java.util.Objects.equals(productId, other.productId)
        && java.util.Objects.equals(tagId, other.tagId);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(productId, tagId);
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long value) {
    this.productId = value;
  }

  public Long getTagId() {
    return tagId;
  }

  public void setTagId(Long value) {
    this.tagId = value;
  }
}
