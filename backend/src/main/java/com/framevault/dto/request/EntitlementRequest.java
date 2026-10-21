package com.framevault.dto.request;

import javax.validation.constraints.*;

public class EntitlementRequest {

  @NotBlank
  @Pattern(regexp = "ACTIVE|REVOKED")
  private String status;

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
  }
}
