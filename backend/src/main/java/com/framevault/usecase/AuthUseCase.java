package com.framevault.usecase;

import static com.framevault.shared.Failures.*;

import com.framevault.dto.request.EmailRequest;
import com.framevault.dto.request.LoginRequest;
import com.framevault.dto.request.RegisterRequest;
import com.framevault.dto.request.ResetPasswordRequest;
import com.framevault.dto.request.TokenRequest;
import com.framevault.dto.response.MessageResponse;
import com.framevault.dto.response.UserResponse;

public interface AuthUseCase {

  UserResponse me(String email);

  MessageResponse register(RegisterRequest f, String ip);

  UserResponse login(
      LoginRequest f,
      javax.servlet.http.HttpServletRequest q,
      javax.servlet.http.HttpServletResponse r);

  MessageResponse logout(javax.servlet.http.HttpServletRequest q);

  MessageResponse requestEmail(EmailRequest f, String ip, String purpose);

  MessageResponse verify(TokenRequest f);

  MessageResponse reset(ResetPasswordRequest f);
}
