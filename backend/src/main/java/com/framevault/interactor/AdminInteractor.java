package com.framevault.interactor;

import static com.framevault.shared.Failures.*;

import com.framevault.domain.model.DownloadEntitlement;
import com.framevault.domain.model.EmailJob;
import com.framevault.domain.model.User;
import com.framevault.dto.request.EnabledRequest;
import com.framevault.dto.request.EntitlementRequest;
import com.framevault.dto.response.AdminUserResponse;
import com.framevault.dto.response.AuditResponse;
import com.framevault.dto.response.DashboardResponse;
import com.framevault.dto.response.EmailAttemptResponse;
import com.framevault.dto.response.EmailJobResponse;
import com.framevault.dto.response.EnabledResponse;
import com.framevault.dto.response.MessageResponse;
import com.framevault.dto.response.OrderResponse;
import com.framevault.dto.response.StatusResponse;
import com.framevault.mapper.ApiMapper;
import com.framevault.repository.AdminAuditRepository;
import com.framevault.repository.CustomerOrderRepository;
import com.framevault.repository.DownloadEntitlementRepository;
import com.framevault.repository.DownloadGrantRepository;
import com.framevault.repository.EmailAttemptRepository;
import com.framevault.repository.EmailJobRepository;
import com.framevault.repository.ProductRepository;
import com.framevault.repository.UserRepository;
import com.framevault.repository.page.PageRequest;
import com.framevault.repository.page.Pageable;
import com.framevault.repository.page.Sort;
import com.framevault.shared.Failures;
import com.framevault.usecase.AdminUseCase;
import com.framevault.usecase.AuditUseCase;
import com.framevault.usecase.CommerceUseCase;
import com.framevault.usecase.DeliveryUseCase;
import com.framevault.validation.ValidatorWrapper;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminInteractor implements AdminUseCase {

  private final UserRepository users;

  private final ProductRepository products;

  private final CustomerOrderRepository orders;

  private final DownloadEntitlementRepository entitlements;

  private final DownloadGrantRepository grants;

  private final EmailJobRepository jobs;

  private final EmailAttemptRepository attempts;

  private final AdminAuditRepository audits;

  private final ApiMapper mapper;

  private final AuditUseCase audit;

  private final CommerceUseCase commerce;

  private final DeliveryUseCase delivery;

  private final com.framevault.security.SessionManager sessions;

  public AdminInteractor(
      UserRepository users,
      ProductRepository products,
      CustomerOrderRepository orders,
      DownloadEntitlementRepository entitlements,
      DownloadGrantRepository grants,
      EmailJobRepository jobs,
      EmailAttemptRepository attempts,
      AdminAuditRepository audits,
      ApiMapper mapper,
      AuditUseCase audit,
      CommerceUseCase commerce,
      DeliveryUseCase delivery,
      com.framevault.security.SessionManager sessions,
      ValidatorWrapper validator) {
    this.users = users;
    this.products = products;
    this.orders = orders;
    this.entitlements = entitlements;
    this.grants = grants;
    this.jobs = jobs;
    this.attempts = attempts;
    this.audits = audits;
    this.mapper = mapper;
    this.audit = audit;
    this.commerce = commerce;
    this.delivery = delivery;
    this.sessions = sessions;
    this.validator = validator;
  }

  @Transactional(readOnly = true)
  public DashboardResponse dashboard() {
    DashboardResponse r = new DashboardResponse();
    r.setProducts(products.count());
    r.setCustomers(users.countByRole("CUSTOMER"));
    r.setOrders(orders.count());
    r.setRevenue(orders.revenue().toPlainString());
    r.setFailedEmails(jobs.countByStatus("DEAD"));
    r.setRecentOrders(
        orders.findTop8ByOrderByCreatedAtDesc().stream()
            .map(mapper::order)
            .collect(Collectors.toList()));
    return r;
  }

  @Transactional(readOnly = true)
  public List<AdminUserResponse> users(int page) {
    validator.requirePage(page);
    return users.findAll(page(page, "id")).getContent().stream()
        .map(mapper::user)
        .collect(Collectors.toList());
  }

  public EnabledResponse enabled(Long id, EnabledRequest f, String actor) {
    validator.requirePositive("id", id);
    validator.validate(f);
    User u = users.lockById(id).orElseThrow(Failures::missing);
    if (!"CUSTOMER".equals(u.getRole())) throw bad("Chỉ thay đổi trạng thái khách hàng");
    u.setEnabled(f.getEnabled());
    users.save(u);
    sessions.revoke(u.getEmail());
    audit.record(actor, "SET_USER_ENABLED", id.toString());
    return new EnabledResponse(f.getEnabled());
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> orders(int page) {
    validator.requirePage(page);
    return orders.findAll(page(page, "createdAt")).getContent().stream()
        .map(mapper::order)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public OrderResponse order(String id) {
    validator.requireText("id", id);
    return commerce.adminDetail(id);
  }

  public MessageResponse resend(String id, String actor) {
    validator.requireText("id", id);
    delivery.resend(id);
    audit.record(actor, "RESEND_DELIVERY", id);
    return new MessageResponse("Đã xếp hàng gửi lại email");
  }

  public StatusResponse entitlement(String id, EntitlementRequest f, String actor) {
    validator.requireText("id", id);
    validator.validate(f);
    DownloadEntitlement e = entitlements.lockById(id).orElseThrow(Failures::missing);
    e.setStatus(f.getStatus());
    entitlements.save(e);
    if ("REVOKED".equals(f.getStatus())) grants.revoke(id);
    audit.record(actor, "ENTITLEMENT_" + f.getStatus(), id);
    return new StatusResponse(f.getStatus());
  }

  @Transactional(readOnly = true)
  public List<EmailJobResponse> emails(int page) {
    validator.requirePage(page);
    return jobs.findAll(page(page, "createdAt")).getContent().stream()
        .map(mapper::email)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<EmailAttemptResponse> attempts(String id) {
    validator.requireText("id", id);
    return attempts.findByJobIdOrderByIdDesc(id).stream()
        .map(mapper::attempt)
        .collect(Collectors.toList());
  }

  public MessageResponse retry(String id, String actor) {
    validator.requireText("id", id);
    EmailJob j =
        jobs.lockById(id)
            .filter(x -> Set.of("DEAD", "RETRY_WAIT").contains(x.getStatus()))
            .orElseThrow(() -> conflict("Chỉ thử lại email đang lỗi"));
    j.setStatus("PENDING");
    j.setAttempts(0);
    j.setNextAttempt(Instant.now());
    jobs.save(j);
    audit.record(actor, "RETRY_EMAIL", id);
    return new MessageResponse("Đã yêu cầu gửi lại");
  }

  @Transactional(readOnly = true)
  public List<AuditResponse> logs(int page) {
    validator.requirePage(page);
    return audits.findAll(page(page, "id")).getContent().stream()
        .map(mapper::audit)
        .collect(Collectors.toList());
  }

  private Pageable page(int page, String field) {
    if (page < 0 || page > 100000) throw bad("Phân trang không hợp lệ");
    return PageRequest.of(page, 50, Sort.by(Sort.Direction.DESC, field));
  }

  private final ValidatorWrapper validator;
}
