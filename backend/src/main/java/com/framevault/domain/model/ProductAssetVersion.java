package com.framevault.domain.model;

import java.time.Instant;

public class ProductAssetVersion {

  private Long id;

  private Long productId;

  private String assetId;

  private int versionNumber;

  private Instant createdAt = Instant.now();

  private Product product;

  private MediaAsset asset;

  public Long getId() {
    return id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long value) {
    this.productId = value;
  }

  public String getAssetId() {
    return assetId;
  }

  public void setAssetId(String value) {
    this.assetId = value;
  }

  public int getVersionNumber() {
    return versionNumber;
  }

  public void setVersionNumber(int value) {
    this.versionNumber = value;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant value) {
    this.createdAt = value;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product value) {
    this.product = value;
  }

  public MediaAsset getAsset() {
    return asset;
  }

  public void setAsset(MediaAsset value) {
    this.asset = value;
  }
}
