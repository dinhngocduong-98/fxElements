package com.framevault.dto.response;

import java.time.Instant;

public class PurchaseResponse {

  private String id;

  private String status;

  private String name;

  @com.fasterxml.jackson.annotation.JsonProperty("license_version")
  private String licenseVersion;

  @com.fasterxml.jackson.annotation.JsonProperty("license_body")
  private String licenseBody;

  @com.fasterxml.jackson.annotation.JsonProperty("product_id")
  private Long productId;

  @com.fasterxml.jackson.annotation.JsonProperty("created_at")
  private Instant createdAt;

  public String getId() {
    return id;
  }

  public void setId(String value) {
    this.id = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
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

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long value) {
    this.productId = value;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant value) {
    this.createdAt = value;
  }
}
