package com.framevault.interactor;

import static com.framevault.shared.Failures.*;

import com.framevault.domain.model.CustomerOrder;
import com.framevault.domain.model.DownloadAudit;
import com.framevault.domain.model.DownloadEntitlement;
import com.framevault.domain.model.DownloadGrant;
import com.framevault.domain.model.MediaAsset;
import com.framevault.domain.model.OrderItem;
import com.framevault.domain.model.ProductAssetVersion;
import com.framevault.domain.model.User;
import com.framevault.dto.response.PurchaseResponse;
import com.framevault.dto.response.UrlResponse;
import com.framevault.repository.CustomerOrderRepository;
import com.framevault.repository.DownloadAuditRepository;
import com.framevault.repository.DownloadEntitlementRepository;
import com.framevault.repository.DownloadGrantRepository;
import com.framevault.repository.MediaAssetRepository;
import com.framevault.repository.OrderItemRepository;
import com.framevault.repository.ProductAssetVersionRepository;
import com.framevault.repository.UserRepository;
import com.framevault.shared.Failures;
import com.framevault.shared.Tokens;
import com.framevault.storage.StorageService;
import com.framevault.usecase.CurrentUserUseCase;
import com.framevault.usecase.DeliveryUseCase;
import com.framevault.usecase.MailUseCase;
import com.framevault.usecase.model.FilePayload;
import com.framevault.validation.ValidatorWrapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeliveryInteractor implements DeliveryUseCase {

  private final DownloadEntitlementRepository entitlements;

  private final DownloadGrantRepository grants;

  private final DownloadAuditRepository audits;

  private final CustomerOrderRepository orders;

  private final OrderItemRepository items;

  private final ProductAssetVersionRepository versions;

  private final MediaAssetRepository media;

  private final UserRepository users;

  private final StorageService storage;

  private final CurrentUserUseCase current;

  private final MailUseCase mail;

  private final com.framevault.security.RateLimit limits;

  private final String url;

  public DeliveryInteractor(
      DownloadEntitlementRepository entitlements,
      DownloadGrantRepository grants,
      DownloadAuditRepository audits,
      CustomerOrderRepository orders,
      OrderItemRepository items,
      ProductAssetVersionRepository versions,
      MediaAssetRepository media,
      UserRepository users,
      StorageService storage,
      CurrentUserUseCase current,
      MailUseCase mail,
      com.framevault.security.RateLimit limits,
      @org.springframework.beans.factory.annotation.Value("${app.public-url}") String url,
      ValidatorWrapper validator) {
    this.entitlements = entitlements;
    this.grants = grants;
    this.audits = audits;
    this.orders = orders;
    this.items = items;
    this.versions = versions;
    this.media = media;
    this.users = users;
    this.storage = storage;
    this.current = current;
    this.mail = mail;
    this.limits = limits;
    this.url = url;
    this.validator = validator;
  }

  @Transactional(readOnly = true)
  public List<PurchaseResponse> purchases(String email) {
    User u = current.require(email);
    List<PurchaseResponse> result = new ArrayList<>();
    for (DownloadEntitlement e : entitlements.findByUserIdOrderByCreatedAtDesc(u.getId())) {
      OrderItem i = items.findById(e.getOrderItemId()).orElseThrow(Failures::missing);
      CustomerOrder o = orders.findById(i.getOrderId()).orElseThrow(Failures::missing);
      if (!"PAID".equals(o.getStatus())) continue;
      PurchaseResponse r = new PurchaseResponse();
      r.setId(e.getId());
      r.setStatus(e.getStatus());
      r.setName(i.getName());
      r.setProductId(i.getProductId());
      r.setLicenseVersion(i.getLicenseVersion());
      r.setLicenseBody(i.getLicenseBody());
      r.setCreatedAt(o.getCreatedAt());
      result.add(r);
    }
    return result;
  }

  public UrlResponse grant(String email, String id) {
    validator.requireText("id", id);
    User u = current.require(email);
    limits.check("grant:" + u.getId(), 30, 3600000);
    return new UrlResponse(issue(u.getId(), id));
  }

  private String issue(Long uid, String id) {
    DownloadEntitlement e = entitlements.lockById(id).orElseThrow(Failures::missing);
    authorize(e, uid);
    String raw = Tokens.create();
    DownloadGrant g = new DownloadGrant();
    g.setId(UUID.randomUUID().toString());
    g.setEntitlementId(id);
    g.setTokenHash(Tokens.hash(raw));
    g.setExpiresAt(Instant.now().plusSeconds(86400));
    grants.save(g);
    audit(uid, id, "GRANT");
    return url + "/download#token=" + raw;
  }

  private void authorize(DownloadEntitlement e, Long uid) {
    if (!uid.equals(e.getUserId())
        || !"ACTIVE".equals(e.getStatus())
        || !users.findById(uid).map(u -> u.getEnabled()).orElse(false)) throw missing();
    OrderItem i = items.findById(e.getOrderItemId()).orElseThrow(Failures::missing);
    if (!orders.findById(i.getOrderId()).map(o -> "PAID".equals(o.getStatus())).orElse(false))
      throw missing();
  }

  public FilePayload download(String email, String token) {
    validator.requireText("token", token);
    User u = current.require(email);
    DownloadGrant g = grants.findByTokenHash(Tokens.hash(token)).orElseThrow(Failures::missing);
    if (g.getRevoked() || !g.getExpiresAt().isAfter(Instant.now())) throw missing();
    DownloadEntitlement e =
        entitlements.findById(g.getEntitlementId()).orElseThrow(Failures::missing);
    authorize(e, u.getId());
    OrderItem i = items.findById(e.getOrderItemId()).orElseThrow(Failures::missing);
    ProductAssetVersion v = versions.findById(i.getAssetVersionId()).orElseThrow(Failures::missing);
    MediaAsset m = media.findById(v.getAssetId()).orElseThrow(Failures::missing);
    org.springframework.core.io.Resource resource = storage.resource(m.getStorageKey());
    if (!resource.exists() || !resource.isReadable()) throw missing();
    audit(u.getId(), e.getId(), "DOWNLOAD");
    return new FilePayload(resource, m.getOriginalName(), "application/octet-stream");
  }

  public void resend(String id) {
    validator.requireText("id", id);
    CustomerOrder o =
        orders
            .findById(id)
            .filter(x -> "PAID".equals(x.getStatus()))
            .orElseThrow(() -> conflict("Chỉ gửi file cho đơn đã thanh toán"));
    StringBuilder body =
        new StringBuilder(
            "Cảm ơn bạn đã mua hàng. Đăng nhập tài khoản để tải file. Liên kết có hạn 24 giờ.\n\n");
    int count = 0;
    for (OrderItem i : items.findByOrderIdOrderById(id)) {
      Optional<DownloadEntitlement> e =
          entitlements.findByOrderItemId(i.getId()).filter(x -> "ACTIVE".equals(x.getStatus()));
      if (e.isPresent()) {
        body.append(i.getName())
            .append(": ")
            .append(issue(o.getUserId(), e.get().getId()))
            .append("\n");
        count++;
      }
    }
    if (count == 0) throw conflict("Đơn chưa được cấp quyền tải");
    mail.enqueue(o.getEmail(), "File tải cho đơn " + id, body.toString());
  }

  private void audit(Long uid, String id, String action) {
    DownloadAudit a = new DownloadAudit();
    a.setUserId(uid);
    a.setEntitlementId(id);
    a.setAction(action);
    audits.save(a);
  }

  private final ValidatorWrapper validator;
}
