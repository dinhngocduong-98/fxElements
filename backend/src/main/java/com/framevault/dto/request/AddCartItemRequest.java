package com.framevault.dto.request;

import javax.validation.constraints.*;

public class AddCartItemRequest {

  @NotNull @Positive private Long productId;

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long value) {
    this.productId = value;
  }
}
