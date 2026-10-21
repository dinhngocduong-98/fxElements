package com.framevault.dto.response;

import java.util.ArrayList;
import java.util.List;

public class AdminProductResponse extends ProductResponse {

  @com.fasterxml.jackson.annotation.JsonProperty("original_asset_id")
  private List<AssetReferenceResponse> originalAssetId = new ArrayList<>();

  public List<AssetReferenceResponse> getOriginalAssetId() {
    return originalAssetId;
  }

  public void setOriginalAssetId(List<AssetReferenceResponse> value) {
    this.originalAssetId = value;
  }
}
