package com.framevault.domain.model;

import java.math.BigDecimal;

public class OrderItem {

  private Long id;

  private String orderId;

  private Long productId;

  private Long assetVersionId;

  private String name;

  private BigDecimal price;

  private String licenseVersion;

  private String licenseBody;

  private CustomerOrder order;

  private ProductAssetVersion assetVersion;

  public Long getId() {
    return id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String value) {
    this.orderId = value;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long value) {
    this.productId = value;
  }

  public Long getAssetVersionId() {
    return assetVersionId;
  }

  public void setAssetVersionId(Long value) {
    this.assetVersionId = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal value) {
    this.price = value;
  }

  public String getLicenseVersion() {
    return licenseVersion;
  }

  public void setLicenseVersion(String value) {
    this.licenseVersion = value;
  }

  public String getLicenseBody() {
    return licenseBody;
  }

  public void setLicenseBody(String value) {
    this.licenseBody = value;
  }

  public CustomerOrder getOrder() {
    return order;
  }

  public void setOrder(CustomerOrder value) {
    this.order = value;
  }

  public ProductAssetVersion getAssetVersion() {
    return assetVersion;
  }

  public void setAssetVersion(ProductAssetVersion value) {
    this.assetVersion = value;
  }
}
