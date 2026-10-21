package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class ProductAssetVersionSqlAdapter extends AbstractSqlAdapter<ProductAssetVersion, Long>
    implements ProductAssetVersionRepository {

  private static final String SELECT_SQL =
      "SELECT id,product_id,asset_id,version_number,created_at FROM product_asset_versions";

  private static final String INSERT_SQL =
      "INSERT INTO product_asset_versions(product_id,asset_id,version_number,created_at)"
          + " VALUES(:productId,:assetId,:versionNumber,:createdAt)";

  private static final String UPDATE_SQL =
      "UPDATE product_asset_versions SET"
          + " product_id=:productId,asset_id=:assetId,version_number=:versionNumber,created_at=:createdAt"
          + " WHERE id=:id";

  private static final String DELETE_SQL = "DELETE FROM product_asset_versions WHERE id=:id";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM product_asset_versions";

  private static final String KEY_WHERE = "id=:id";

  public ProductAssetVersionSqlAdapter(NamedParameterJdbcTemplate jdbc) {
    super(
        jdbc,
        ProductAssetVersion.class,
        new String[] {"id", "productId", "assetId", "versionNumber", "createdAt"},
        new String[] {"id"},
        true,
        SELECT_SQL,
        INSERT_SQL,
        UPDATE_SQL,
        DELETE_SQL,
        COUNT_SQL,
        KEY_WHERE);
  }

  public Optional<ProductAssetVersion> findFirstByProductIdOrderByVersionNumberDesc(Long pid) {
    return one(" WHERE product_id=:pid ORDER BY version_number DESC LIMIT 1", args("pid", pid));
  }
}
