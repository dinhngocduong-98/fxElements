package com.framevault.usecase;

import static com.framevault.shared.Failures.*;

import java.time.Instant;
import java.util.List;

public interface MailUseCase {

  String enqueue(String to, String subject, String body);

  List<String> due();

  com.framevault.usecase.model.MailPayload claim(String id);

  void complete(String id, Instant lease, String error);
}
