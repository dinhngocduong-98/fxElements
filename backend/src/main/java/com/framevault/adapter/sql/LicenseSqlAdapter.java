package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class LicenseSqlAdapter extends AbstractSqlAdapter<License, Long>
    implements LicenseRepository {

  private static final String SELECT_SQL = "SELECT id,version,body FROM licenses";

  private static final String INSERT_SQL =
      "INSERT INTO licenses(version,body) VALUES(:version,:body)";

  private static final String UPDATE_SQL =
      "UPDATE licenses SET version=:version,body=:body WHERE id=:id";

  private static final String DELETE_SQL = "DELETE FROM licenses WHERE id=:id";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM licenses";

  private static final String KEY_WHERE = "id=:id";

  public LicenseSqlAdapter(NamedParameterJdbcTemplate jdbc) {
    super(
        jdbc,
        License.class,
        new String[] {"id", "version", "body"},
        new String[] {"id"},
        true,
        SELECT_SQL,
        INSERT_SQL,
        UPDATE_SQL,
        DELETE_SQL,
        COUNT_SQL,
        KEY_WHERE);
  }

  public Optional<License> findFirstByOrderByIdDesc() {
    return one(" ORDER BY id DESC LIMIT 1", args());
  }
}
