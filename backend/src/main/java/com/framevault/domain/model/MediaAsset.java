package com.framevault.domain.model;

import java.time.Instant;

public class MediaAsset {

  private String id;

  private String kind;

  private String storageKey;

  private String originalName;

  private String mimeType;

  private long sizeBytes;

  private String checksum;

  private Instant createdAt = Instant.now();

  public String getId() {
    return id;
  }

  public void setId(String value) {
    this.id = value;
  }

  public String getKind() {
    return kind;
  }

  public void setKind(String value) {
    this.kind = value;
  }

  public String getStorageKey() {
    return storageKey;
  }

  public void setStorageKey(String value) {
    this.storageKey = value;
  }

  public String getOriginalName() {
    return originalName;
  }

  public void setOriginalName(String value) {
    this.originalName = value;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String value) {
    this.mimeType = value;
  }

  public long getSizeBytes() {
    return sizeBytes;
  }

  public void setSizeBytes(long value) {
    this.sizeBytes = value;
  }

  public String getChecksum() {
    return checksum;
  }

  public void setChecksum(String value) {
    this.checksum = value;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant value) {
    this.createdAt = value;
  }
}
