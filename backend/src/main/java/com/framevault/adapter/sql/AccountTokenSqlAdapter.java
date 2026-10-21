package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.time.Instant;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class AccountTokenSqlAdapter extends AbstractSqlAdapter<AccountToken, Long>
    implements AccountTokenRepository {

  private static final String SELECT_SQL =
      "SELECT id,user_id,token_hash,purpose,expires_at,used_at FROM account_tokens";

  private static final String INSERT_SQL =
      "INSERT INTO account_tokens(user_id,token_hash,purpose,expires_at,used_at)"
          + " VALUES(:userId,:tokenHash,:purpose,:expiresAt,:usedAt)";

  private static final String UPDATE_SQL =
      "UPDATE account_tokens SET"
          + " user_id=:userId,token_hash=:tokenHash,purpose=:purpose,expires_at=:expiresAt,used_at=:usedAt"
          + " WHERE id=:id";

  private static final String DELETE_SQL = "DELETE FROM account_tokens WHERE id=:id";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM account_tokens";

  private static final String KEY_WHERE = "id=:id";

  public AccountTokenSqlAdapter(NamedParameterJdbcTemplate jdbc) {
    super(
        jdbc,
        AccountToken.class,
        new String[] {"id", "userId", "tokenHash", "purpose", "expiresAt", "usedAt"},
        new String[] {"id"},
        true,
        SELECT_SQL,
        INSERT_SQL,
        UPDATE_SQL,
        DELETE_SQL,
        COUNT_SQL,
        KEY_WHERE);
  }

  public int invalidate(Long uid, String purpose, Instant now) {
    return jdbc.update(
        "UPDATE account_tokens SET used_at=:now WHERE user_id=:uid AND purpose=:purpose AND used_at"
            + " IS NULL",
        args("uid", uid, "purpose", purpose, "now", now));
  }

  public Optional<AccountToken> findByTokenHashAndPurposeAndUsedAtIsNullAndExpiresAtAfter(
      String hash, String purpose, Instant now) {
    return one(
        " WHERE token_hash=:hash AND purpose=:purpose AND used_at IS NULL AND expires_at>:now FOR"
            + " UPDATE",
        args("hash", hash, "purpose", purpose, "now", now));
  }
}
