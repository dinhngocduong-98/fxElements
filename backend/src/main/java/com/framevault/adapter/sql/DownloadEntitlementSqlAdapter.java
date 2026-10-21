package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class DownloadEntitlementSqlAdapter extends AbstractSqlAdapter<DownloadEntitlement, String>
    implements DownloadEntitlementRepository {

  private static final String SELECT_SQL =
      "SELECT id,user_id,order_item_id,status,created_at FROM download_entitlements";

  private static final String INSERT_SQL =
      "INSERT INTO download_entitlements(id,user_id,order_item_id,status,created_at)"
          + " VALUES(:id,:userId,:orderItemId,:status,:createdAt)";

  private static final String UPDATE_SQL =
      "UPDATE download_entitlements SET"
          + " user_id=:userId,order_item_id=:orderItemId,status=:status,created_at=:createdAt WHERE"
          + " id=:id";

  private static final String DELETE_SQL = "DELETE FROM download_entitlements WHERE id=:id";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM download_entitlements";

  private static final String KEY_WHERE = "id=:id";

  public DownloadEntitlementSqlAdapter(NamedParameterJdbcTemplate jdbc) {
    super(
        jdbc,
        DownloadEntitlement.class,
        new String[] {"id", "userId", "orderItemId", "status", "createdAt"},
        new String[] {"id"},
        false,
        SELECT_SQL,
        INSERT_SQL,
        UPDATE_SQL,
        DELETE_SQL,
        COUNT_SQL,
        KEY_WHERE);
  }

  public List<DownloadEntitlement> findByUserIdOrderByCreatedAtDesc(Long uid) {
    return select(" WHERE user_id=:uid ORDER BY created_at DESC", args("uid", uid));
  }

  public Optional<DownloadEntitlement> findByOrderItemId(Long itemId) {
    return one(" WHERE order_item_id=:id", args("id", itemId));
  }

  public long owned(Long uid, Long pid) {
    return jdbc.queryForObject(
        "SELECT COUNT(*) FROM download_entitlements e JOIN order_items i ON i.id=e.order_item_id"
            + " WHERE e.user_id=:uid AND i.product_id=:pid AND e.status='ACTIVE'",
        args("uid", uid, "pid", pid),
        Long.class);
  }

  public Optional<DownloadEntitlement> lockById(String id) {
    return one(" WHERE id=:id FOR UPDATE", args("id", id));
  }
}
