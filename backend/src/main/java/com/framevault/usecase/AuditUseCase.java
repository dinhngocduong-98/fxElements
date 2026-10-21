package com.framevault.usecase;

import static com.framevault.shared.Failures.*;

public interface AuditUseCase {

  void record(String actor, String action, String target);
}
