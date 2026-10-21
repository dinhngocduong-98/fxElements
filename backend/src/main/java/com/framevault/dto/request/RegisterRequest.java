package com.framevault.dto.request;

import javax.validation.constraints.*;

public class RegisterRequest {

  @Email @NotBlank private String email;

  @NotBlank
  @Size(max = 100)
  private String name;

  @NotNull
  @Size(min = 10, max = 64)
  private String password;

  public String getEmail() {
    return email;
  }

  public void setEmail(String value) {
    this.email = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String value) {
    this.password = value;
  }
}
