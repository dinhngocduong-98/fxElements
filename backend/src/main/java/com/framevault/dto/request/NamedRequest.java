package com.framevault.dto.request;

import javax.validation.constraints.*;

public class NamedRequest {

  @NotBlank
  @Size(max = 100)
  private String name;

  @Pattern(regexp = "[a-z0-9]+(?:-[a-z0-9]+)*")
  @Size(max = 120)
  private String slug;

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
}
