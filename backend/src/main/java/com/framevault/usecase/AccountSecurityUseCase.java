package com.framevault.usecase;

public interface AccountSecurityUseCase
    extends org.springframework.security.core.userdetails.UserDetailsService {

  boolean isActive(String email);

  void bootstrap(String email, String password);
}
