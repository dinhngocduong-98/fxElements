package com.framevault.dto.response;

public class UploadResponse {

  private String id;

  private String name;

  private long size;

  public String getId() {
    return id;
  }

  public void setId(String value) {
    this.id = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long value) {
    this.size = value;
  }
}
