package com.framevault.usecase;

import static com.framevault.shared.Failures.*;

import com.framevault.domain.model.User;

public interface CurrentUserUseCase {

  User require(String email);
}
