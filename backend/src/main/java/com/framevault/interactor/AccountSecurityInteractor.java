package com.framevault.interactor;

import com.framevault.domain.model.User;
import com.framevault.repository.UserRepository;
import com.framevault.usecase.AccountSecurityUseCase;
import com.framevault.validation.ValidatorWrapper;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountSecurityInteractor implements AccountSecurityUseCase {

  private final UserRepository users;

  private final PasswordEncoder encoder;

  public AccountSecurityInteractor(
      UserRepository users, PasswordEncoder encoder, ValidatorWrapper validator) {
    this.users = users;
    this.encoder = encoder;
    this.validator = validator;
  }

  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) {
    validator.requireText("email", email);
    User u =
        users
            .findByEmail(email.toLowerCase(java.util.Locale.ROOT))
            .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
    return org.springframework.security.core.userdetails.User.withUsername(u.getEmail())
        .password(u.getPasswordHash())
        .roles(u.getRole())
        .disabled(!u.getEnabled())
        .build();
  }

  @Transactional(readOnly = true)
  public boolean isActive(String email) {
    return users.findByEmail(email).map(User::getEnabled).orElse(false);
  }

  public void bootstrap(String email, String password) {
    if (email.isBlank() || password.isBlank()) return;
    email = email.toLowerCase(java.util.Locale.ROOT);
    if (users.findByEmail(email).isPresent()) return;
    if (password.length() < 12
        || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72)
      throw new IllegalStateException(
          "ADMIN_PASSWORD requires at least 12 characters and at most 72 UTF-8 bytes");
    User u = new User();
    u.setEmail(email);
    u.setName("Administrator");
    u.setRole("ADMIN");
    u.setPasswordHash(encoder.encode(password));
    u.setEnabled(true);
    u.setVerified(true);
    users.save(u);
  }

  private final ValidatorWrapper validator;
}
