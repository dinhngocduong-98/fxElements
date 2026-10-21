package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class AdminAuditSqlAdapter extends AbstractSqlAdapter<AdminAudit, Long>
    implements AdminAuditRepository {

  private static final String SELECT_SQL =
      "SELECT id,actor,action,target,created_at FROM admin_audit";

  private static final String INSERT_SQL =
      "INSERT INTO admin_audit(actor,action,target,created_at)"
          + " VALUES(:actor,:action,:target,:createdAt)";

  private static final String UPDATE_SQL =
      "UPDATE admin_audit SET actor=:actor,action=:action,target=:target,created_at=:createdAt"
          + " WHERE id=:id";

  private static final String DELETE_SQL = "DELETE FROM admin_audit WHERE id=:id";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM admin_audit";

  private static final String KEY_WHERE = "id=:id";

  public AdminAuditSqlAdapter(NamedParameterJdbcTemplate jdbc) {
    super(
        jdbc,
        AdminAudit.class,
        new String[] {"id", "actor", "action", "target", "createdAt"},
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
