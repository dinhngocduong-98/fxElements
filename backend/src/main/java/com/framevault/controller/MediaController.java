package com.framevault.controller;

import com.framevault.dto.response.UploadResponse;
import com.framevault.usecase.MediaUseCase;
import com.framevault.usecase.model.FilePayload;
import java.security.Principal;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class MediaController {

  private final MediaUseCase media;

  public MediaController(MediaUseCase media) {
    this.media = media;
  }

  @PostMapping("/admin/media/uploads")
  public UploadResponse upload(
      @RequestParam("kind") String kind,
      @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
      Principal p)
      throws java.io.IOException {
    return media.upload(kind, file, name(p));
  }

  @GetMapping("/media/public/{id}")
  public ResponseEntity<Resource> file(@PathVariable("id") String id) {
    FilePayload f = media.publicFile(id);
    return ResponseEntity.ok()
        .header("X-Content-Type-Options", "nosniff")
        .cacheControl(CacheControl.noCache())
        .contentType(MediaType.parseMediaType(f.getMime()))
        .body(f.getResource());
  }

  private String name(Principal p) {
    return p == null ? null : p.getName();
  }
}
