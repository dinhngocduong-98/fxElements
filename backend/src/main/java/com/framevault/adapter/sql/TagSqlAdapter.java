package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class TagSqlAdapter extends AbstractSqlAdapter<Tag, Long> implements TagRepository {

  private static final String SELECT_SQL = "SELECT id,name FROM tags";

  private static final String INSERT_SQL = "INSERT INTO tags(name) VALUES(:name)";

  private static final String UPDATE_SQL = "UPDATE tags SET name=:name WHERE id=:id";

  private static final String DELETE_SQL = "DELETE FROM tags WHERE id=:id";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM tags";

  private static final String KEY_WHERE = "id=:id";

  public TagSqlAdapter(NamedParameterJdbcTemplate jdbc) {
    super(
        jdbc,
        Tag.class,
        new String[] {"id", "name"},
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
