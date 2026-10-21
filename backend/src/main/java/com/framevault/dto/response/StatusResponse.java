package com.framevault.dto.response;

public class StatusResponse {

  private String status;

  public StatusResponse(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
  }
}
