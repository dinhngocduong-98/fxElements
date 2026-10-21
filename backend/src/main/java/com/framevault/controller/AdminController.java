package com.framevault.controller;

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
import com.framevault.usecase.AdminUseCase;
import java.security.Principal;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

  private final AdminUseCase admin;

  public AdminController(AdminUseCase admin) {
    this.admin = admin;
  }

  @GetMapping("/dashboard")
  public DashboardResponse dashboard() {
    return admin.dashboard();
  }

  @GetMapping("/users")
  public List<AdminUserResponse> users(@RequestParam(defaultValue = "0") int page) {
    return admin.users(page);
  }

  @PutMapping("/users/{id}/enabled")
  public EnabledResponse enabled(
      @PathVariable("id") Long id, @RequestBody EnabledRequest f, Principal p) {
    return admin.enabled(id, f, name(p));
  }

  @GetMapping("/orders")
  public List<OrderResponse> orders(@RequestParam(defaultValue = "0") int page) {
    return admin.orders(page);
  }

  @GetMapping("/orders/{id}")
  public OrderResponse detail(@PathVariable("id") String id) {
    return admin.order(id);
  }

  @PostMapping("/orders/{id}/resend-delivery")
  public MessageResponse resend(@PathVariable("id") String id, Principal p) {
    return admin.resend(id, name(p));
  }

  @PutMapping("/entitlements/{id}")
  public StatusResponse entitlement(
      @PathVariable("id") String id, @RequestBody EntitlementRequest f, Principal p) {
    return admin.entitlement(id, f, name(p));
  }

  @GetMapping("/email-jobs")
  public List<EmailJobResponse> emails(@RequestParam(defaultValue = "0") int page) {
    return admin.emails(page);
  }

  @GetMapping("/email-jobs/{id}/attempts")
  public List<EmailAttemptResponse> attempts(@PathVariable("id") String id) {
    return admin.attempts(id);
  }

  @PostMapping("/email-jobs/{id}/retry")
  public MessageResponse retry(@PathVariable("id") String id, Principal p) {
    return admin.retry(id, name(p));
  }

  @GetMapping("/audit-logs")
  public List<AuditResponse> logs(@RequestParam(defaultValue = "0") int page) {
    return admin.logs(page);
  }

  private String name(Principal p) {
    return p == null ? null : p.getName();
  }
}
