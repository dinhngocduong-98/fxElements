package com.framevault.dto.response;

public class UserResponse {

  private Long id;

  private String email;

  private String name;

  private String role;

  private boolean verified;

  public Long getId() {
    return id;
  }

  public void setId(Long value) {
    this.id = value;
  }

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

  public String getRole() {
    return role;
  }

  public void setRole(String value) {
    this.role = value;
  }

  public boolean getVerified() {
    return verified;
  }

  public void setVerified(boolean value) {
    this.verified = value;
  }
}
