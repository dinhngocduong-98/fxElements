package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class MediaAssetSqlAdapter extends AbstractSqlAdapter<MediaAsset, String>
    implements MediaAssetRepository {

  private static final String SELECT_SQL =
      "SELECT id,kind,storage_key,original_name,mime_type,size_bytes,checksum,created_at FROM"
          + " media_assets";

  private static final String INSERT_SQL =
      "INSERT INTO"
          + " media_assets(id,kind,storage_key,original_name,mime_type,size_bytes,checksum,created_at)"
          + " VALUES(:id,:kind,:storageKey,:originalName,:mimeType,:sizeBytes,:checksum,:createdAt)";

  private static final String UPDATE_SQL =
      "UPDATE media_assets SET"
          + " kind=:kind,storage_key=:storageKey,original_name=:originalName,mime_type=:mimeType,size_bytes=:sizeBytes,checksum=:checksum,created_at=:createdAt"
          + " WHERE id=:id";

  private static final String DELETE_SQL = "DELETE FROM media_assets WHERE id=:id";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM media_assets";

  private static final String KEY_WHERE = "id=:id";

  public MediaAssetSqlAdapter(NamedParameterJdbcTemplate jdbc) {
    super(
        jdbc,
        MediaAsset.class,
        new String[] {
          "id",
          "kind",
          "storageKey",
          "originalName",
          "mimeType",
          "sizeBytes",
          "checksum",
          "createdAt"
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

  public boolean existsByIdAndKind(String id, String kind) {
    return jdbc.queryForObject(
            "SELECT COUNT(*) FROM media_assets WHERE id=:id AND kind=:kind",
            args("id", id, "kind", kind),
            Long.class)
        > 0;
  }
}
