package com.framevault.repository.query;

public class ProductQuery {

  private String query;

  public String getQuery() {
    return query;
  }

  public void setQuery(String value) {
    this.query = value;
  }

  private Long categoryId;

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long value) {
    this.categoryId = value;
  }

  private Long collectionId;

  public Long getCollectionId() {
    return collectionId;
  }

  public void setCollectionId(Long value) {
    this.collectionId = value;
  }

  private Integer height;

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer value) {
    this.height = value;
  }

  private java.math.BigDecimal fps;

  public java.math.BigDecimal getFps() {
    return fps;
  }

  public void setFps(java.math.BigDecimal value) {
    this.fps = value;
  }

  private String format;

  public String getFormat() {
    return format;
  }

  public void setFormat(String value) {
    this.format = value;
  }

  private Boolean alphaChannel;

  public Boolean getAlphaChannel() {
    return alphaChannel;
  }

  public void setAlphaChannel(Boolean value) {
    this.alphaChannel = value;
  }

  private java.math.BigDecimal minPrice;

  public java.math.BigDecimal getMinPrice() {
    return minPrice;
  }

  public void setMinPrice(java.math.BigDecimal value) {
    this.minPrice = value;
  }

  private java.math.BigDecimal maxPrice;

  public java.math.BigDecimal getMaxPrice() {
    return maxPrice;
  }

  public void setMaxPrice(java.math.BigDecimal value) {
    this.maxPrice = value;
  }

  private Boolean featured;

  public Boolean getFeatured() {
    return featured;
  }

  public void setFeatured(Boolean value) {
    this.featured = value;
  }

  private boolean publishedOnly;

  public boolean getPublishedOnly() {
    return publishedOnly;
  }

  public void setPublishedOnly(boolean value) {
    this.publishedOnly = value;
  }
}
