package com.framevault.dto.request;

import java.util.Map;
import javax.validation.constraints.*;

public class CheckoutRequest {

  @NotNull private Map<String, String> expectedPrices;

  public Map<String, String> getExpectedPrices() {
    return expectedPrices;
  }

  public void setExpectedPrices(Map<String, String> value) {
    this.expectedPrices = value;
  }
}
