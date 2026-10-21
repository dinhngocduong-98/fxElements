package com.framevault.dto.request;

import javax.validation.constraints.*;

public class EmailRequest {

  @javax.validation.constraints.Email @NotBlank private String email;

  public String getEmail() {
    return email;
  }

  public void setEmail(String value) {
    this.email = value;
  }
}
