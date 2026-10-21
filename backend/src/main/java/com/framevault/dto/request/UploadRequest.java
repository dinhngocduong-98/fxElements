package com.framevault.dto.request;

import javax.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

public class UploadRequest {
  @NotBlank
  @Pattern(regexp = "THUMBNAIL|PREVIEW|ORIGINAL")
  private final String kind;

  @NotNull private final MultipartFile file;

  public UploadRequest(String kind, MultipartFile file) {
    this.kind = kind;
    this.file = file;
  }

  public String getKind() {
    return kind;
  }

  public MultipartFile getFile() {
    return file;
  }
}
