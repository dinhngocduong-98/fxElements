package com.framevault.interactor;

import static com.framevault.shared.Failures.*;

import com.framevault.domain.model.AccountToken;
import com.framevault.domain.model.User;
import com.framevault.dto.request.EmailRequest;
import com.framevault.dto.request.LoginRequest;
import com.framevault.dto.request.RegisterRequest;
import com.framevault.dto.request.ResetPasswordRequest;
import com.framevault.dto.request.TokenRequest;
import com.framevault.dto.response.MessageResponse;
import com.framevault.dto.response.UserResponse;
import com.framevault.mapper.ApiMapper;
import com.framevault.repository.AccountTokenRepository;
import com.framevault.repository.UserRepository;
import com.framevault.shared.Failures;
import com.framevault.shared.Tokens;
import com.framevault.usecase.AuthUseCase;
import com.framevault.usecase.CurrentUserUseCase;
import com.framevault.usecase.MailUseCase;
import com.framevault.validation.ValidatorWrapper;
import java.time.Instant;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthInteractor implements AuthUseCase {

  private final UserRepository users;

  private final AccountTokenRepository tokens;

  private final CurrentUserUseCase current;

  private final org.springframework.security.crypto.password.PasswordEncoder encoder;

  private final MailUseCase mail;

  private final com.framevault.security.SessionManager sessions;

  private final com.framevault.security.RateLimit limits;

  private final ApiMapper mapper;

  private final String url;

  public AuthInteractor(
      UserRepository users,
      AccountTokenRepository tokens,
      CurrentUserUseCase current,
      org.springframework.security.crypto.password.PasswordEncoder encoder,
      MailUseCase mail,
      com.framevault.security.SessionManager sessions,
      com.framevault.security.RateLimit limits,
      ApiMapper mapper,
      @org.springframework.beans.factory.annotation.Value("${app.public-url}") String url,
      ValidatorWrapper validator) {
    this.users = users;
    this.tokens = tokens;
    this.current = current;
    this.encoder = encoder;
    this.mail = mail;
    this.sessions = sessions;
    this.limits = limits;
    this.mapper = mapper;
    this.url = url;
    this.validator = validator;
  }

  public UserResponse me(String email) {
    User u = current.require(email);
    UserResponse r = new UserResponse();
    r.setId(u.getId());
    r.setEmail(u.getEmail());
    r.setName(u.getName());
    r.setRole(u.getRole());
    r.setVerified(u.getVerified());
    return r;
  }

  public MessageResponse register(RegisterRequest f, String ip) {
    validator.validate(f);
    limits.check("register:" + ip, 10, 3600000);
    checkPassword(f.getPassword());
    String email = normalize(f.getEmail());
    if (!users.findByEmail(email).isPresent()) {
      User u = new User();
      u.setEmail(email);
      u.setName(f.getName());
      u.setPasswordHash(encoder.encode(f.getPassword()));
      u.setRole("CUSTOMER");
      u.setEnabled(true);
      u.setVerified(false);
      users.save(u);
      issue(u, "VERIFY", 86400);
    }
    return new MessageResponse(
        "Nếu email có thể đăng ký, liên kết xác thực sẽ được gửi tới hộp thư.");
  }

  public UserResponse login(
      LoginRequest f,
      javax.servlet.http.HttpServletRequest q,
      javax.servlet.http.HttpServletResponse r) {
    validator.validate(f);
    String email = normalize(f.getEmail());
    limits.check("login-ip:" + q.getRemoteAddr(), 50, 900000);
    limits.check("login:" + email, 5, 900000);
    return me(sessions.login(email, f.getPassword(), q, r));
  }

  public MessageResponse logout(javax.servlet.http.HttpServletRequest q) {
    sessions.logout(q);
    return new MessageResponse("Đã đăng xuất");
  }

  public MessageResponse requestEmail(EmailRequest f, String ip, String purpose) {
    validator.validate(f);
    validator.requireOneOf("purpose", purpose, "VERIFY", "RESET");
    String email = normalize(f.getEmail());
    limits.check("email:" + email, 3, 3600000);
    limits.check("mail-ip:" + ip, 20, 3600000);
    users
        .findByEmail(email)
        .filter(u -> u.getEnabled())
        .ifPresent(
            u -> {
              if (!purpose.equals("VERIFY") || !u.getVerified())
                issue(u, purpose, purpose.equals("RESET") ? 1800 : 86400);
            });
    return new MessageResponse("Nếu tài khoản phù hợp tồn tại, email sẽ được gửi.");
  }

  private void issue(User u, String purpose, long ttl) {
    tokens.invalidate(u.getId(), purpose, Instant.now());
    String raw = Tokens.create();
    AccountToken t = new AccountToken();
    t.setUserId(u.getId());
    t.setTokenHash(Tokens.hash(raw));
    t.setPurpose(purpose);
    t.setExpiresAt(Instant.now().plusSeconds(ttl));
    tokens.save(t);
    mail.enqueue(
        u.getEmail(),
        purpose.equals("VERIFY") ? "Xác thực tài khoản FrameVault" : "Đặt lại mật khẩu FrameVault",
        url
            + "/"
            + (purpose.equals("VERIFY") ? "verify-email" : "reset-password")
            + "#token="
            + raw);
  }

  public MessageResponse verify(TokenRequest f) {
    validator.validate(f);
    consume(f.getToken(), "VERIFY", null);
    return new MessageResponse("Email đã được xác thực");
  }

  public MessageResponse reset(ResetPasswordRequest f) {
    validator.validate(f);
    consume(f.getToken(), "RESET", f.getPassword());
    return new MessageResponse("Đã đổi mật khẩu. Vui lòng đăng nhập lại.");
  }

  private void consume(String raw, String purpose, String password) {
    AccountToken t =
        tokens
            .findByTokenHashAndPurposeAndUsedAtIsNullAndExpiresAtAfter(
                Tokens.hash(raw), purpose, Instant.now())
            .orElseThrow(() -> bad("Liên kết không hợp lệ hoặc đã hết hạn"));
    User u = users.findById(t.getUserId()).orElseThrow(Failures::missing);
    if (!u.getEnabled())
      throw new org.springframework.web.server.ResponseStatusException(
          org.springframework.http.HttpStatus.FORBIDDEN);
    if (purpose.equals("VERIFY")) u.setVerified(true);
    else {
      checkPassword(password);
      u.setPasswordHash(encoder.encode(password));
      sessions.revoke(u.getEmail());
    }
    users.save(u);
    tokens.invalidate(u.getId(), purpose, Instant.now());
  }

  private String normalize(String email) {
    return email.trim().toLowerCase(Locale.ROOT);
  }

  private void checkPassword(String value) {
    if (value.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72)
      throw bad("Mật khẩu tối đa 72 byte UTF-8");
  }

  private final ValidatorWrapper validator;
}
