package com.framevault.repository;

import com.framevault.domain.model.AccountToken;
import com.framevault.repository.page.*;
import java.time.Instant;
import java.util.*;

public interface AccountTokenRepository extends CrudRepository<AccountToken, Long> {

  int invalidate(Long uid, String purpose, Instant now);

  Optional<AccountToken> findByTokenHashAndPurposeAndUsedAtIsNullAndExpiresAtAfter(
      String hash, String purpose, Instant now);
}
