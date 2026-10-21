package com.framevault.usecase;

import static com.framevault.shared.Failures.*;

import com.framevault.dto.response.UploadResponse;
import com.framevault.usecase.model.FilePayload;

public interface MediaUseCase {

  UploadResponse upload(
      String kind, org.springframework.web.multipart.MultipartFile file, String actor)
      throws java.io.IOException;

  FilePayload publicFile(String id);
}
