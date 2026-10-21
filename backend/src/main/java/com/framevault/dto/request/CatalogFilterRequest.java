package com.framevault.dto.request;

import java.math.BigDecimal;
import javax.validation.constraints.*;

public class CatalogFilterRequest {

  private String q;

  public String getQ() {
    return q;
  }

  public void setQ(String value) {
    q = value;
  }

  private Long category_id;

  public Long getCategory_id() {
    return category_id;
  }

  public void setCategory_id(Long value) {
    category_id = value;
  }

  private Long collection_id;

  public Long getCollection_id() {
    return collection_id;
  }

  public void setCollection_id(Long value) {
    collection_id = value;
  }

  private Integer height;

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer value) {
    height = value;
  }

  private BigDecimal fps;

  public BigDecimal getFps() {
    return fps;
  }

  public void setFps(BigDecimal value) {
    fps = value;
  }

  private String format;

  public String getFormat() {
    return format;
  }

  public void setFormat(String value) {
    format = value;
  }

  private Boolean alpha_channel;

  public Boolean getAlpha_channel() {
    return alpha_channel;
  }

  public void setAlpha_channel(Boolean value) {
    alpha_channel = value;
  }

  private BigDecimal minPrice;

  public BigDecimal getMinPrice() {
    return minPrice;
  }

  public void setMinPrice(BigDecimal value) {
    minPrice = value;
  }

  private BigDecimal maxPrice;

  public BigDecimal getMaxPrice() {
    return maxPrice;
  }

  public void setMaxPrice(BigDecimal value) {
    maxPrice = value;
  }

  private Boolean featured;

  public Boolean getFeatured() {
    return featured;
  }

  public void setFeatured(Boolean value) {
    featured = value;
  }

  private String sort = "newest";

  public String getSort() {
    return sort;
  }

  public void setSort(String value) {
    sort = value;
  }

  @Min(0)
  @Max(100000)
  private int page;

  public int getPage() {
    return page;
  }

  public void setPage(int value) {
    page = value;
  }

  @Min(1)
  @Max(60)
  private int size = 12;

  public int getSize() {
    return size;
  }

  public void setSize(int value) {
    size = value;
  }

  // HTTP query aliases retain compatibility with the Vue query-string contract.
  public Long getCategoryId() {
    return category_id;
  }

  public Long getCollectionId() {
    return collection_id;
  }

  public Boolean getAlphaChannel() {
    return alpha_channel;
  }
}
