package com.framevault.dto.request;

import javax.validation.constraints.*;

public class ResetPasswordRequest {

  @NotBlank private String token;

  @NotNull
  @Size(min = 10, max = 64)
  private String password;

  public String getToken() {
    return token;
  }

  public void setToken(String value) {
    this.token = value;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String value) {
    this.password = value;
  }
}
