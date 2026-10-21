package com.framevault.interactor;

import static com.framevault.shared.Failures.*;

import com.framevault.domain.model.User;
import com.framevault.repository.UserRepository;
import com.framevault.usecase.CurrentUserUseCase;
import com.framevault.validation.ValidatorWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CurrentUserInteractor implements CurrentUserUseCase {

  private final UserRepository users;

  public CurrentUserInteractor(UserRepository users, ValidatorWrapper validator) {
    this.users = users;
    this.validator = validator;
  }

  public User require(String email) {
    if (email == null)
      throw new org.springframework.web.server.ResponseStatusException(
          org.springframework.http.HttpStatus.UNAUTHORIZED);
    validator.requireText("email", email);
    return users
        .findByEmail(email)
        .filter(u -> u.getEnabled())
        .orElseThrow(
            () ->
                new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED));
  }

  private final ValidatorWrapper validator;
}
