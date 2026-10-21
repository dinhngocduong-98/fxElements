package com.framevault.mapper;

import com.framevault.domain.model.AdminAudit;
import com.framevault.domain.model.CustomerOrder;
import com.framevault.domain.model.EmailAttempt;
import com.framevault.domain.model.EmailJob;
import com.framevault.domain.model.License;
import com.framevault.domain.model.OrderItem;
import com.framevault.domain.model.Product;
import com.framevault.domain.model.User;
import com.framevault.dto.response.AdminProductResponse;
import com.framevault.dto.response.AdminUserResponse;
import com.framevault.dto.response.AuditResponse;
import com.framevault.dto.response.EmailAttemptResponse;
import com.framevault.dto.response.EmailJobResponse;
import com.framevault.dto.response.LicenseResponse;
import com.framevault.dto.response.NamedResponse;
import com.framevault.dto.response.OrderItemResponse;
import com.framevault.dto.response.OrderResponse;
import com.framevault.dto.response.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ApiMapper {

  public AdminUserResponse user(User u) {
    AdminUserResponse r = new AdminUserResponse();
    r.setId(u.getId());
    r.setEmail(u.getEmail());
    r.setName(u.getName());
    r.setRole(u.getRole());
    r.setVerified(u.getVerified());
    r.setEnabled(u.getEnabled());
    r.setCreatedAt(u.getCreatedAt());
    return r;
  }

  public NamedResponse named(Long id, String name, String slug) {
    NamedResponse r = new NamedResponse();
    r.setId(id);
    r.setName(name);
    r.setSlug(slug);
    return r;
  }

  public LicenseResponse license(License l) {
    LicenseResponse r = new LicenseResponse();
    r.setVersion(l.getVersion());
    r.setBody(l.getBody());
    return r;
  }

  public ProductResponse product(Product p, boolean admin) {
    ProductResponse r = admin ? new AdminProductResponse() : new ProductResponse();
    r.setId(p.getId());
    r.setCategoryId(p.getCategoryId());
    r.setCategoryName(p.getCategory().getName());
    r.setName(p.getName());
    r.setSlug(p.getSlug());
    r.setDescription(p.getDescription());
    r.setPrice(p.getPrice().toPlainString());
    r.setStatus(p.getStatus());
    r.setWidth(p.getWidth());
    r.setHeight(p.getHeight());
    r.setFps(p.getFps());
    r.setFormat(p.getFormat());
    r.setAlphaChannel(p.getAlphaChannel());
    r.setDurationSeconds(p.getDurationSeconds());
    r.setFeatured(p.getFeatured());
    r.setThumbnailId(p.getThumbnailId());
    r.setPreviewId(p.getPreviewId());
    r.setThumbnailUrl(
        p.getThumbnailId() == null ? "" : "/api/v1/media/public/" + p.getThumbnailId());
    r.setPreviewUrl(p.getPreviewId() == null ? "" : "/api/v1/media/public/" + p.getPreviewId());
    return r;
  }

  public OrderResponse order(CustomerOrder o) {
    OrderResponse r = new OrderResponse();
    r.setId(o.getId());
    r.setEmail(o.getEmail());
    r.setCurrency(o.getCurrency());
    r.setTotal(o.getTotal().toPlainString());
    r.setStatus(o.getStatus());
    r.setCreatedAt(o.getCreatedAt());
    r.setExpiresAt(o.getExpiresAt());
    return r;
  }

  public OrderItemResponse item(OrderItem i) {
    OrderItemResponse r = new OrderItemResponse();
    r.setId(i.getId());
    r.setProductId(i.getProductId());
    r.setName(i.getName());
    r.setPrice(i.getPrice().toPlainString());
    r.setLicenseVersion(i.getLicenseVersion());
    r.setLicenseBody(i.getLicenseBody());
    return r;
  }

  public EmailJobResponse email(EmailJob e) {
    EmailJobResponse r = new EmailJobResponse();
    r.setId(e.getId());
    r.setRecipient(e.getRecipient());
    r.setSubject(e.getSubject());
    r.setStatus(e.getStatus());
    r.setAttempts(e.getAttempts());
    r.setNextAttempt(e.getNextAttempt());
    r.setLastError(e.getLastError());
    r.setCreatedAt(e.getCreatedAt());
    return r;
  }

  public EmailAttemptResponse attempt(EmailAttempt e) {
    EmailAttemptResponse r = new EmailAttemptResponse();
    r.setSuccess(e.getSuccess());
    r.setErrorCode(e.getErrorCode());
    r.setCreatedAt(e.getCreatedAt());
    return r;
  }

  public AuditResponse audit(AdminAudit a) {
    AuditResponse r = new AuditResponse();
    r.setId(a.getId());
    r.setActor(a.getActor());
    r.setAction(a.getAction());
    r.setTarget(a.getTarget());
    r.setCreatedAt(a.getCreatedAt());
    return r;
  }
}
