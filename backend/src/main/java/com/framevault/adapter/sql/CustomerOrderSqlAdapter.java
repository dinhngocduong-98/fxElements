package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerOrderSqlAdapter extends AbstractSqlAdapter<CustomerOrder, String>
    implements CustomerOrderRepository {

  private static final String SELECT_SQL =
      "SELECT id,user_id,email,currency,total,status,idempotency_key,created_at,expires_at FROM"
          + " orders";

  private static final String INSERT_SQL =
      "INSERT INTO"
          + " orders(id,user_id,email,currency,total,status,idempotency_key,created_at,expires_at)"
          + " VALUES(:id,:userId,:email,:currency,:total,:status,:idempotencyKey,:createdAt,:expiresAt)";

  private static final String UPDATE_SQL =
      "UPDATE orders SET"
          + " user_id=:userId,email=:email,currency=:currency,total=:total,status=:status,idempotency_key=:idempotencyKey,created_at=:createdAt,expires_at=:expiresAt"
          + " WHERE id=:id";

  private static final String DELETE_SQL = "DELETE FROM orders WHERE id=:id";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM orders";

  private static final String KEY_WHERE = "id=:id";

  public CustomerOrderSqlAdapter(NamedParameterJdbcTemplate jdbc) {
    super(
        jdbc,
        CustomerOrder.class,
        new String[] {
          "id",
          "userId",
          "email",
          "currency",
          "total",
          "status",
          "idempotencyKey",
          "createdAt",
          "expiresAt"
        },
        new String[] {"id"},
        false,
        SELECT_SQL,
        INSERT_SQL,
        UPDATE_SQL,
        DELETE_SQL,
        COUNT_SQL,
        KEY_WHERE);
  }

  public List<CustomerOrder> findByUserIdOrderByCreatedAtDesc(Long uid) {
    return select(" WHERE user_id=:uid ORDER BY created_at DESC", args("uid", uid));
  }

  public Optional<CustomerOrder> findByIdAndUserId(String id, Long uid) {
    return one(" WHERE id=:id AND user_id=:uid", args("id", id, "uid", uid));
  }

  public Optional<CustomerOrder> findByUserIdAndIdempotencyKey(Long uid, String key) {
    return one(" WHERE user_id=:uid AND idempotency_key=:key", args("uid", uid, "key", key));
  }

  public List<CustomerOrder> findTop8ByOrderByCreatedAtDesc() {
    return select(" ORDER BY created_at DESC LIMIT 8", args());
  }

  public BigDecimal revenue() {
    return jdbc.queryForObject(
        "SELECT COALESCE(SUM(total),0) FROM orders WHERE status='PAID'", args(), BigDecimal.class);
  }

  public int expire(Instant now) {
    return jdbc.update(
        "UPDATE orders SET status='EXPIRED' WHERE status='PENDING_PAYMENT' AND expires_at<:now",
        args("now", now));
  }
}
