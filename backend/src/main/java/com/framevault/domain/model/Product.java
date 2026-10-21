package com.framevault.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Product {

  private Long id;

  private String name;

  private String slug;

  private String description;

  private BigDecimal price;

  private String status;

  private Long categoryId;

  private int width;

  private int height;

  private BigDecimal fps;

  private String format;

  private boolean alphaChannel;

  private BigDecimal durationSeconds;

  private boolean featured;

  private String thumbnailId;

  private String previewId;

  private Instant createdAt = Instant.now();

  private Category category;

  public Long getId() {
    return id;
  }

  public void setId(Long value) {
    this.id = value;
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

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal value) {
    this.price = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long value) {
    this.categoryId = value;
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

  public BigDecimal getFps() {
    return fps;
  }

  public void setFps(BigDecimal value) {
    this.fps = value;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String value) {
    this.format = value;
  }

  public boolean getAlphaChannel() {
    return alphaChannel;
  }

  public void setAlphaChannel(boolean value) {
    this.alphaChannel = value;
  }

  public BigDecimal getDurationSeconds() {
    return durationSeconds;
  }

  public void setDurationSeconds(BigDecimal value) {
    this.durationSeconds = value;
  }

  public boolean getFeatured() {
    return featured;
  }

  public void setFeatured(boolean value) {
    this.featured = value;
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

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant value) {
    this.createdAt = value;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category value) {
    this.category = value;
  }
}
