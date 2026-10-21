package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class ProductCollectionSqlAdapter
    extends AbstractSqlAdapter<ProductCollection, ProductCollectionId>
    implements ProductCollectionRepository {

  private static final String SELECT_SQL =
      "SELECT product_id,collection_id FROM product_collections";

  private static final String INSERT_SQL =
      "INSERT INTO product_collections(product_id,collection_id) VALUES(:productId,:collectionId)";

  private static final String UPDATE_SQL =
      "UPDATE product_collections SET product_id=:productId,collection_id=:collectionId WHERE"
          + " product_id=:productId AND collection_id=:collectionId";

  private static final String DELETE_SQL =
      "DELETE FROM product_collections WHERE product_id=:productId AND collection_id=:collectionId";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM product_collections";

  private static final String KEY_WHERE = "product_id=:productId AND collection_id=:collectionId";

  public ProductCollectionSqlAdapter(NamedParameterJdbcTemplate jdbc) {
    super(
        jdbc,
        ProductCollection.class,
        new String[] {"productId", "collectionId"},
        new String[] {"productId", "collectionId"},
        false,
        SELECT_SQL,
        INSERT_SQL,
        UPDATE_SQL,
        DELETE_SQL,
        COUNT_SQL,
        KEY_WHERE);
  }

  public List<ProductCollection> findByProductId(Long pid) {
    return select(" WHERE product_id=:pid", args("pid", pid));
  }

  public void deleteByProductId(Long pid) {
    jdbc.update("DELETE FROM product_collections WHERE product_id=:pid", args("pid", pid));
  }
}
