package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class DownloadGrantSqlAdapter extends AbstractSqlAdapter<DownloadGrant, String>
    implements DownloadGrantRepository {

  private static final String SELECT_SQL =
      "SELECT id,entitlement_id,token_hash,expires_at,revoked FROM download_grants";

  private static final String INSERT_SQL =
      "INSERT INTO download_grants(id,entitlement_id,token_hash,expires_at,revoked)"
          + " VALUES(:id,:entitlementId,:tokenHash,:expiresAt,:revoked)";

  private static final String UPDATE_SQL =
      "UPDATE download_grants SET"
          + " entitlement_id=:entitlementId,token_hash=:tokenHash,expires_at=:expiresAt,revoked=:revoked"
          + " WHERE id=:id";

  private static final String DELETE_SQL = "DELETE FROM download_grants WHERE id=:id";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM download_grants";

  private static final String KEY_WHERE = "id=:id";

  public DownloadGrantSqlAdapter(NamedParameterJdbcTemplate jdbc) {
    super(
        jdbc,
        DownloadGrant.class,
        new String[] {"id", "entitlementId", "tokenHash", "expiresAt", "revoked"},
        new String[] {"id"},
        false,
        SELECT_SQL,
        INSERT_SQL,
        UPDATE_SQL,
        DELETE_SQL,
        COUNT_SQL,
        KEY_WHERE);
  }

  public Optional<DownloadGrant> findByTokenHash(String hash) {
    return one(" WHERE token_hash=:hash", args("hash", hash));
  }

  public int revoke(String id) {
    return jdbc.update(
        "UPDATE download_grants SET revoked=TRUE WHERE entitlement_id=:id", args("id", id));
  }
}
