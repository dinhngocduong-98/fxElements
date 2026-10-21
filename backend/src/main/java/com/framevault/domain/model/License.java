package com.framevault.domain.model;

public class License {

  private Long id;

  private String version;

  private String body;

  public Long getId() {
    return id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String value) {
    this.version = value;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String value) {
    this.body = value;
  }
}
