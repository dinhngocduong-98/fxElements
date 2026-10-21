package com.framevault.interactor;

import static com.framevault.shared.Failures.*;

import com.framevault.domain.model.MediaAsset;
import com.framevault.dto.response.UploadResponse;
import com.framevault.repository.MediaAssetRepository;
import com.framevault.repository.ProductRepository;
import com.framevault.shared.Failures;
import com.framevault.shared.Tokens;
import com.framevault.storage.StorageService;
import com.framevault.usecase.AuditUseCase;
import com.framevault.usecase.MediaUseCase;
import com.framevault.usecase.model.FilePayload;
import com.framevault.validation.ValidatorWrapper;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MediaInteractor implements MediaUseCase {

  private final MediaAssetRepository assets;

  private final ProductRepository products;

  private final StorageService storage;

  private final AuditUseCase audit;

  public MediaInteractor(
      MediaAssetRepository assets,
      ProductRepository products,
      StorageService storage,
      AuditUseCase audit,
      ValidatorWrapper validator) {
    this.assets = assets;
    this.products = products;
    this.storage = storage;
    this.audit = audit;
    this.validator = validator;
  }

  @Transactional(rollbackFor = Exception.class)
  public UploadResponse upload(
      String kind, org.springframework.web.multipart.MultipartFile file, String actor)
      throws java.io.IOException {
    validator.validate(new com.framevault.dto.request.UploadRequest(kind, file));
    if (!Set.of("THUMBNAIL", "PREVIEW", "ORIGINAL").contains(kind) || file.isEmpty())
      throw bad("Loại upload không hợp lệ");
    if (kind.equals("THUMBNAIL") && file.getSize() > 10 * 1024 * 1024L)
      throw bad("Ảnh tối đa 10MB");
    if (kind.equals("PREVIEW") && file.getSize() > 200 * 1024 * 1024L)
      throw bad("Demo tối đa 200MB");
    String name =
        Optional.ofNullable(file.getOriginalFilename())
            .orElse("asset")
            .replaceAll("[^\\p{L}\\p{N}._ -]", "_");
    if (name.length() > 240) name = name.substring(name.length() - 240);
    byte[] head;
    try (java.io.InputStream in = file.getInputStream()) {
      head = in.readNBytes(32);
    }
    String mime = detect(head);
    if (kind.equals("THUMBNAIL") && !Set.of("image/jpeg", "image/png", "image/webp").contains(mime))
      throw bad("Thumbnail phải là JPEG, PNG hoặc WebP");
    if (kind.equals("PREVIEW") && !Set.of("video/mp4", "video/webm").contains(mime))
      throw bad("Demo phải là MP4 hoặc WebM");
    if (kind.equals("ORIGINAL")
        && !Set.of("video/mp4", "video/quicktime", "video/webm", "application/zip").contains(mime))
      throw bad("File gốc phải là MOV/MP4/WebM hoặc ZIP");
    java.security.MessageDigest digest;
    try {
      digest = java.security.MessageDigest.getInstance("SHA-256");
    } catch (java.security.NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
    try (java.io.InputStream in = file.getInputStream()) {
      byte[] buffer = new byte[65536];
      int n;
      while ((n = in.read(buffer)) != -1) digest.update(buffer, 0, n);
    }
    MediaAsset m = new MediaAsset();
    m.setId(UUID.randomUUID().toString());
    m.setKind(kind);
    m.setStorageKey((kind.equals("ORIGINAL") ? "private/" : "public/") + m.getId());
    m.setOriginalName(name);
    m.setMimeType(mime);
    m.setSizeBytes(file.getSize());
    m.setChecksum(Tokens.hex(digest.digest()));
    storage.store(m.getStorageKey(), file);
    // File systems do not join a DB transaction: clean up the staged file on rollback.
    org.springframework.transaction.support.TransactionSynchronizationManager
        .registerSynchronization(
            new org.springframework.transaction.support.TransactionSynchronization() {

              @Override
              public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED)
                  try {
                    storage.delete(m.getStorageKey());
                  } catch (java.io.IOException e) {
                    org.slf4j.LoggerFactory.getLogger(MediaInteractor.class)
                        .warn("Could not clean up rolled-back upload {}", m.getId());
                  }
              }
            });
    assets.save(m);
    audit.record(actor, "UPLOAD", m.getId());
    UploadResponse r = new UploadResponse();
    r.setId(m.getId());
    r.setName(m.getOriginalName());
    r.setSize(m.getSizeBytes());
    return r;
  }

  @Transactional(readOnly = true)
  public FilePayload publicFile(String id) {
    validator.requireText("id", id);
    MediaAsset m =
        assets
            .findById(id)
            .filter(x -> Set.of("THUMBNAIL", "PREVIEW").contains(x.getKind()))
            .orElseThrow(Failures::missing);
    if (!products.existsByStatusAndThumbnailIdOrStatusAndPreviewId(
        "PUBLISHED", id, "PUBLISHED", id)) throw missing();
    org.springframework.core.io.Resource r = storage.resource(m.getStorageKey());
    if (!r.exists() || !r.isReadable()) throw missing();
    return new FilePayload(r, m.getOriginalName(), m.getMimeType());
  }

  private String detect(byte[] h) {
    if (h.length >= 3 && (h[0] & 255) == 255 && (h[1] & 255) == 216 && (h[2] & 255) == 255)
      return "image/jpeg";
    if (h.length >= 8 && (h[0] & 255) == 137 && h[1] == 80 && h[2] == 78 && h[3] == 71)
      return "image/png";
    if (h.length >= 12
        && new String(h, 0, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("RIFF")
        && new String(h, 8, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("WEBP"))
      return "image/webp";
    if (h.length >= 12
        && new String(h, 4, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("ftyp"))
      return new String(h, 8, 4, java.nio.charset.StandardCharsets.US_ASCII).startsWith("qt")
          ? "video/quicktime"
          : "video/mp4";
    if (h.length >= 4
        && (h[0] & 255) == 26
        && (h[1] & 255) == 69
        && (h[2] & 255) == 223
        && (h[3] & 255) == 163) return "video/webm";
    if (h.length >= 4 && h[0] == 80 && h[1] == 75 && (h[2] == 3 || h[2] == 5 || h[2] == 7))
      return "application/zip";
    return "application/octet-stream";
  }

  private final ValidatorWrapper validator;
}
