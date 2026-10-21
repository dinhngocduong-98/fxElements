package com.framevault.dto.request;

import javax.validation.constraints.*;

public class TokenRequest {

  @NotBlank private String token;

  public String getToken() {
    return token;
  }

  public void setToken(String value) {
    this.token = value;
  }
}
