package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class CartItemSqlAdapter extends AbstractSqlAdapter<CartItem, CartItemId>
    implements CartItemRepository {

  private static final String SELECT_SQL = "SELECT user_id,product_id FROM cart_items";

  private static final String INSERT_SQL =
      "INSERT INTO cart_items(user_id,product_id) VALUES(:userId,:productId)";

  private static final String UPDATE_SQL =
      "UPDATE cart_items SET user_id=:userId,product_id=:productId WHERE user_id=:userId AND"
          + " product_id=:productId";

  private static final String DELETE_SQL =
      "DELETE FROM cart_items WHERE user_id=:userId AND product_id=:productId";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM cart_items";

  private static final String KEY_WHERE = "user_id=:userId AND product_id=:productId";

  public CartItemSqlAdapter(NamedParameterJdbcTemplate jdbc) {
    super(
        jdbc,
        CartItem.class,
        new String[] {"userId", "productId"},
        new String[] {"userId", "productId"},
        false,
        SELECT_SQL,
        INSERT_SQL,
        UPDATE_SQL,
        DELETE_SQL,
        COUNT_SQL,
        KEY_WHERE);
  }

  public List<CartItem> findByUserIdOrderByProductId(Long uid) {
    return select(" WHERE user_id=:uid ORDER BY product_id", args("uid", uid));
  }

  public void deleteByUserId(Long uid) {
    jdbc.update("DELETE FROM cart_items WHERE user_id=:uid", args("uid", uid));
  }
}
