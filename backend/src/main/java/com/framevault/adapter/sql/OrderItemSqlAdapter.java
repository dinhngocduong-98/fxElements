package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class OrderItemSqlAdapter extends AbstractSqlAdapter<OrderItem, Long>
    implements OrderItemRepository {

  private static final String SELECT_SQL =
      "SELECT id,order_id,product_id,asset_version_id,name,price,license_version,license_body FROM"
          + " order_items";

  private static final String INSERT_SQL =
      "INSERT INTO"
          + " order_items(order_id,product_id,asset_version_id,name,price,license_version,license_body)"
          + " VALUES(:orderId,:productId,:assetVersionId,:name,:price,:licenseVersion,:licenseBody)";

  private static final String UPDATE_SQL =
      "UPDATE order_items SET"
          + " order_id=:orderId,product_id=:productId,asset_version_id=:assetVersionId,name=:name,price=:price,license_version=:licenseVersion,license_body=:licenseBody"
          + " WHERE id=:id";

  private static final String DELETE_SQL = "DELETE FROM order_items WHERE id=:id";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM order_items";

  private static final String KEY_WHERE = "id=:id";

  public OrderItemSqlAdapter(NamedParameterJdbcTemplate jdbc) {
    super(
        jdbc,
        OrderItem.class,
        new String[] {
          "id",
          "orderId",
          "productId",
          "assetVersionId",
          "name",
          "price",
          "licenseVersion",
          "licenseBody"
        },
        new String[] {"id"},
        true,
        SELECT_SQL,
        INSERT_SQL,
        UPDATE_SQL,
        DELETE_SQL,
        COUNT_SQL,
        KEY_WHERE);
  }

  public List<OrderItem> findByOrderIdOrderById(String id) {
    return select(" WHERE order_id=:id ORDER BY id", args("id", id));
  }
}
