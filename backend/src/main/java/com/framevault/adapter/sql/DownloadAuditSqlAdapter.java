package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class DownloadAuditSqlAdapter extends AbstractSqlAdapter<DownloadAudit, Long>
    implements DownloadAuditRepository {

  private static final String SELECT_SQL =
      "SELECT id,user_id,entitlement_id,action,created_at FROM download_audit";

  private static final String INSERT_SQL =
      "INSERT INTO download_audit(user_id,entitlement_id,action,created_at)"
          + " VALUES(:userId,:entitlementId,:action,:createdAt)";

  private static final String UPDATE_SQL =
      "UPDATE download_audit SET"
          + " user_id=:userId,entitlement_id=:entitlementId,action=:action,created_at=:createdAt"
          + " WHERE id=:id";

  private static final String DELETE_SQL = "DELETE FROM download_audit WHERE id=:id";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM download_audit";

  private static final String KEY_WHERE = "id=:id";

  public DownloadAuditSqlAdapter(NamedParameterJdbcTemplate jdbc) {
    super(
        jdbc,
        DownloadAudit.class,
        new String[] {"id", "userId", "entitlementId", "action", "createdAt"},
        new String[] {"id"},
        true,
        SELECT_SQL,
        INSERT_SQL,
        UPDATE_SQL,
        DELETE_SQL,
        COUNT_SQL,
        KEY_WHERE);
  }
}
