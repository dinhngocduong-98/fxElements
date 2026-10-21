package com.framevault.dto.response;

public class OrderItemResponse {

  private Long id;

  @com.fasterxml.jackson.annotation.JsonProperty("product_id")
  private Long productId;

  private String name;

  private String price;

  @com.fasterxml.jackson.annotation.JsonProperty("license_version")
  private String licenseVersion;

  @com.fasterxml.jackson.annotation.JsonProperty("license_body")
  private String licenseBody;

  @com.fasterxml.jackson.annotation.JsonProperty("entitlement_id")
  private String entitlementId;

  @com.fasterxml.jackson.annotation.JsonProperty("entitlement_status")
  private String entitlementStatus;

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

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String value) {
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

  public String getEntitlementId() {
    return entitlementId;
  }

  public void setEntitlementId(String value) {
    this.entitlementId = value;
  }

  public String getEntitlementStatus() {
    return entitlementStatus;
  }

  public void setEntitlementStatus(String value) {
    this.entitlementStatus = value;
  }
}
