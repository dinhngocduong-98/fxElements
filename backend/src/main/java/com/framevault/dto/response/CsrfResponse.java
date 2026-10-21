package com.framevault.dto.response;

public class CsrfResponse {

  private String token;

  private String headerName;

  public CsrfResponse(String token, String headerName) {
    this.token = token;
    this.headerName = headerName;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String value) {
    this.token = value;
  }

  public String getHeaderName() {
    return headerName;
  }

  public void setHeaderName(String value) {
    this.headerName = value;
  }
}
