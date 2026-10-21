package com.framevault.dto.request;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;

public class ProductRequest {

  @NotBlank
  @Size(max = 160)
  private String name;

  @NotBlank
  @Pattern(regexp = "[a-z0-9]+(?:-[a-z0-9]+)*")
  @Size(max = 180)
  private String slug;

  @NotBlank
  @Size(max = 20000)
  private String description;

  @NotNull
  @DecimalMin("0.01")
  @Digits(integer = 16, fraction = 2)
  private BigDecimal price;

  @Pattern(regexp = "DRAFT|PUBLISHED|ARCHIVED")
  @NotNull
  private String status = "DRAFT";

  @NotNull @Positive private Long categoryId;

  @Min(1)
  @Max(16384)
  private int width = 3840;

  @Min(1)
  @Max(16384)
  private int height = 2160;

  @NotNull
  @DecimalMin("1")
  @DecimalMax("1000")
  @Digits(integer = 3, fraction = 3)
  private BigDecimal fps = new BigDecimal("30");

  @Pattern(regexp = "MOV|MP4|WEBM|ZIP")
  @NotNull
  private String format = "MOV";

  private boolean alphaChannel;

  private boolean featured;

  @NotNull
  @DecimalMin("0.01")
  @Digits(integer = 8, fraction = 2)
  private BigDecimal durationSeconds = BigDecimal.ONE;

  private String thumbnailId;

  private String previewId;

  private String originalAssetId;

  @NotNull
  @Size(max = 50)
  private List<Long> tagIds = new ArrayList<>();

  @NotNull
  @Size(max = 50)
  private List<Long> collectionIds = new ArrayList<>();

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

  public boolean getFeatured() {
    return featured;
  }

  public void setFeatured(boolean value) {
    this.featured = value;
  }

  public BigDecimal getDurationSeconds() {
    return durationSeconds;
  }

  public void setDurationSeconds(BigDecimal value) {
    this.durationSeconds = value;
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

  public String getOriginalAssetId() {
    return originalAssetId;
  }

  public void setOriginalAssetId(String value) {
    this.originalAssetId = value;
  }

  public List<Long> getTagIds() {
    return tagIds;
  }

  public void setTagIds(List<Long> value) {
    this.tagIds = value;
  }

  public List<Long> getCollectionIds() {
    return collectionIds;
  }

  public void setCollectionIds(List<Long> value) {
    this.collectionIds = value;
  }
}
