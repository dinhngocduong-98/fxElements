package com.framevault.dto.response;

public class AssetReferenceResponse {

  @com.fasterxml.jackson.annotation.JsonProperty("asset_id")
  private String assetId;

  public String getAssetId() {
    return assetId;
  }

  public void setAssetId(String value) {
    this.assetId = value;
  }
}
