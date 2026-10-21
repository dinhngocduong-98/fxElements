package com.framevault.controller;

import com.framevault.dto.response.PurchaseResponse;
import com.framevault.dto.response.UrlResponse;
import com.framevault.usecase.DeliveryUseCase;
import com.framevault.usecase.model.FilePayload;
import java.security.Principal;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class DeliveryController {

  private final DeliveryUseCase delivery;

  public DeliveryController(DeliveryUseCase delivery) {
    this.delivery = delivery;
  }

  @GetMapping("/me/purchases")
  public List<PurchaseResponse> purchases(Principal p) {
    return delivery.purchases(name(p));
  }

  @PostMapping("/purchases/{id}/download-grants")
  public UrlResponse grant(@PathVariable("id") String id, Principal p) {
    return delivery.grant(name(p), id);
  }

  @GetMapping("/downloads/{token}")
  public ResponseEntity<Resource> download(@PathVariable("token") String token, Principal p) {
    FilePayload f = delivery.download(name(p), token);
    return ResponseEntity.ok()
        .cacheControl(CacheControl.noStore())
        .header("Referrer-Policy", "no-referrer")
        .header("X-Content-Type-Options", "nosniff")
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment()
                .filename(f.getName(), java.nio.charset.StandardCharsets.UTF_8)
                .build()
                .toString())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(f.getResource());
  }

  private String name(Principal p) {
    return p == null ? null : p.getName();
  }
}
