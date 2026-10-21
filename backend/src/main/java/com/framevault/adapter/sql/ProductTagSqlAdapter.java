package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class ProductTagSqlAdapter extends AbstractSqlAdapter<ProductTag, ProductTagId>
    implements ProductTagRepository {

  private static final String SELECT_SQL = "SELECT product_id,tag_id FROM product_tags";

  private static final String INSERT_SQL =
      "INSERT INTO product_tags(product_id,tag_id) VALUES(:productId,:tagId)";

  private static final String UPDATE_SQL =
      "UPDATE product_tags SET product_id=:productId,tag_id=:tagId WHERE product_id=:productId AND"
          + " tag_id=:tagId";

  private static final String DELETE_SQL =
      "DELETE FROM product_tags WHERE product_id=:productId AND tag_id=:tagId";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM product_tags";

  private static final String KEY_WHERE = "product_id=:productId AND tag_id=:tagId";

  public ProductTagSqlAdapter(NamedParameterJdbcTemplate jdbc) {
    super(
        jdbc,
        ProductTag.class,
        new String[] {"productId", "tagId"},
        new String[] {"productId", "tagId"},
        false,
        SELECT_SQL,
        INSERT_SQL,
        UPDATE_SQL,
        DELETE_SQL,
        COUNT_SQL,
        KEY_WHERE);
  }

  public List<ProductTag> findByProductId(Long pid) {
    return select(" WHERE product_id=:pid", args("pid", pid));
  }

  public void deleteByProductId(Long pid) {
    jdbc.update("DELETE FROM product_tags WHERE product_id=:pid", args("pid", pid));
  }
}
