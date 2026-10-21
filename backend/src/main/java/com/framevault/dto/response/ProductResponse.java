package com.framevault.dto.response;

import java.util.ArrayList;
import java.util.List;

public class ProductResponse {

  private Long id;

  @com.fasterxml.jackson.annotation.JsonProperty("category_id")
  private Long categoryId;

  private String name;

  private String slug;

  private String description;

  private String price;

  private String status;

  @com.fasterxml.jackson.annotation.JsonProperty("category_name")
  private String categoryName;

  private String format;

  @com.fasterxml.jackson.annotation.JsonProperty("thumbnail_id")
  private String thumbnailId;

  @com.fasterxml.jackson.annotation.JsonProperty("preview_id")
  private String previewId;

  @com.fasterxml.jackson.annotation.JsonProperty("thumbnail_url")
  private String thumbnailUrl;

  @com.fasterxml.jackson.annotation.JsonProperty("preview_url")
  private String previewUrl;

  private int width;

  private int height;

  private java.math.BigDecimal fps;

  @com.fasterxml.jackson.annotation.JsonProperty("duration_seconds")
  private java.math.BigDecimal durationSeconds;

  @com.fasterxml.jackson.annotation.JsonProperty("alpha_channel")
  private boolean alphaChannel;

  private boolean featured;

  private List<NamedResponse> tags = new ArrayList<>();

  private List<NamedResponse> collections = new ArrayList<>();

  private LicenseResponse license;

  public Long getId() {
    return id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long value) {
    this.categoryId = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String value) {
    this.slug = value;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String value) {
    this.description = value;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String value) {
    this.price = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String value) {
    this.categoryName = value;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String value) {
    this.format = value;
  }

  public String getThumbnailId() {
    return thumbnailId;
  }

  public void setThumbnailId(String value) {
    this.thumbnailId = value;
  }

  public String getPreviewId() {
    return previewId;
  }

  public void setPreviewId(String value) {
    this.previewId = value;
  }

  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public void setThumbnailUrl(String value) {
    this.thumbnailUrl = value;
  }

  public String getPreviewUrl() {
    return previewUrl;
  }

  public void setPreviewUrl(String value) {
    this.previewUrl = value;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int value) {
    this.width = value;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int value) {
    this.height = value;
  }

  public java.math.BigDecimal getFps() {
    return fps;
  }

  public void setFps(java.math.BigDecimal value) {
    this.fps = value;
  }

  public java.math.BigDecimal getDurationSeconds() {
    return durationSeconds;
  }

  public void setDurationSeconds(java.math.BigDecimal value) {
    this.durationSeconds = value;
  }

  public boolean getAlphaChannel() {
    return alphaChannel;
  }

  public void setAlphaChannel(boolean value) {
    this.alphaChannel = value;
  }

  public boolean getFeatured() {
    return featured;
  }

  public void setFeatured(boolean value) {
    this.featured = value;
  }

  public List<NamedResponse> getTags() {
    return tags;
  }

  public void setTags(List<NamedResponse> value) {
    this.tags = value;
  }

  public List<NamedResponse> getCollections() {
    return collections;
  }

  public void setCollections(List<NamedResponse> value) {
    this.collections = value;
  }

  public LicenseResponse getLicense() {
    return license;
  }

  public void setLicense(LicenseResponse value) {
    this.license = value;
  }
}
