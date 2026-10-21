package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class AssetCollectionSqlAdapter extends AbstractSqlAdapter<AssetCollection, Long>
    implements AssetCollectionRepository {

  private static final String SELECT_SQL = "SELECT id,name,slug FROM collections";

  private static final String INSERT_SQL = "INSERT INTO collections(name,slug) VALUES(:name,:slug)";

  private static final String UPDATE_SQL =
      "UPDATE collections SET name=:name,slug=:slug WHERE id=:id";

  private static final String DELETE_SQL = "DELETE FROM collections WHERE id=:id";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM collections";

  private static final String KEY_WHERE = "id=:id";

  public AssetCollectionSqlAdapter(NamedParameterJdbcTemplate jdbc) {
    super(
        jdbc,
        AssetCollection.class,
        new String[] {"id", "name", "slug"},
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
