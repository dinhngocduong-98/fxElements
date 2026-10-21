package com.framevault.controller;

import com.framevault.dto.request.EmailRequest;
import com.framevault.dto.request.LoginRequest;
import com.framevault.dto.request.RegisterRequest;
import com.framevault.dto.request.ResetPasswordRequest;
import com.framevault.dto.request.TokenRequest;
import com.framevault.dto.response.CsrfResponse;
import com.framevault.dto.response.MessageResponse;
import com.framevault.dto.response.UserResponse;
import com.framevault.usecase.AuthUseCase;
import java.security.Principal;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthUseCase auth;

  public AuthController(AuthUseCase auth) {
    this.auth = auth;
  }

  @GetMapping("/csrf")
  public CsrfResponse csrf(org.springframework.security.web.csrf.CsrfToken t) {
    return new CsrfResponse(t.getToken(), t.getHeaderName());
  }

  @GetMapping("/me")
  public UserResponse me(Principal p) {
    return auth.me(name(p));
  }

  @PostMapping("/register")
  public MessageResponse register(
      @RequestBody RegisterRequest f, javax.servlet.http.HttpServletRequest q) {
    return auth.register(f, q.getRemoteAddr());
  }

  @PostMapping("/login")
  public UserResponse login(
      @RequestBody LoginRequest f,
      javax.servlet.http.HttpServletRequest q,
      javax.servlet.http.HttpServletResponse r) {
    return auth.login(f, q, r);
  }

  @PostMapping("/logout")
  public MessageResponse logout(javax.servlet.http.HttpServletRequest q) {
    return auth.logout(q);
  }

  @PostMapping("/forgot-password")
  public MessageResponse forgot(
      @RequestBody EmailRequest f, javax.servlet.http.HttpServletRequest q) {
    return auth.requestEmail(f, q.getRemoteAddr(), "RESET");
  }

  @PostMapping("/resend-verification")
  public MessageResponse resend(
      @RequestBody EmailRequest f, javax.servlet.http.HttpServletRequest q) {
    return auth.requestEmail(f, q.getRemoteAddr(), "VERIFY");
  }

  @PostMapping("/verify-email")
  public MessageResponse verify(@RequestBody TokenRequest f) {
    return auth.verify(f);
  }

  @PostMapping("/reset-password")
  public MessageResponse reset(@RequestBody ResetPasswordRequest f) {
    return auth.reset(f);
  }

  private String name(Principal p) {
    return p == null ? null : p.getName();
  }
}
