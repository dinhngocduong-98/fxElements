package com.framevault.dto.response;

public class EnabledResponse {

  private boolean enabled;

  public EnabledResponse(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(boolean value) {
    this.enabled = value;
  }
}
