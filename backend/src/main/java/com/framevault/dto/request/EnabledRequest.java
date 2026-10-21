package com.framevault.dto.request;

import javax.validation.constraints.*;

public class EnabledRequest {

  @javax.validation.constraints.NotNull private Boolean enabled;

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean value) {
    this.enabled = value;
  }
}
