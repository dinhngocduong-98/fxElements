package com.framevault.config;

import com.framevault.usecase.AccountSecurityUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

  private final AccountSecurityUseCase accounts;

  private final String email, password;

  public AdminBootstrap(
      AccountSecurityUseCase accounts,
      @Value("${app.admin-email}") String email,
      @Value("${app.admin-password}") String password) {
    this.accounts = accounts;
    this.email = email;
    this.password = password;
  }

  @Override
  public void run(String... args) {
    accounts.bootstrap(email, password);
  }
}
