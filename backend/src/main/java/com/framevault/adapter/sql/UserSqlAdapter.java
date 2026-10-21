package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class UserSqlAdapter extends AbstractSqlAdapter<User, Long> implements UserRepository {

  private static final String SELECT_SQL =
      "SELECT id,email,name,password_hash,role,enabled,verified,created_at FROM users";

  private static final String INSERT_SQL =
      "INSERT INTO users(email,name,password_hash,role,enabled,verified,created_at)"
          + " VALUES(:email,:name,:passwordHash,:role,:enabled,:verified,:createdAt)";

  private static final String UPDATE_SQL =
      "UPDATE users SET"
          + " email=:email,name=:name,password_hash=:passwordHash,role=:role,enabled=:enabled,verified=:verified,created_at=:createdAt"
          + " WHERE id=:id";

  private static final String DELETE_SQL = "DELETE FROM users WHERE id=:id";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM users";

  private static final String KEY_WHERE = "id=:id";

  public UserSqlAdapter(NamedParameterJdbcTemplate jdbc) {
    super(
        jdbc,
        User.class,
        new String[] {
          "id", "email", "name", "passwordHash", "role", "enabled", "verified", "createdAt"
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

  public Optional<User> findByEmail(String email) {
    return one(" WHERE email=:email", args("email", email));
  }

  public long countByRole(String role) {
    return jdbc.queryForObject(
        "SELECT COUNT(*) FROM users WHERE role=:role", args("role", role), Long.class);
  }

  public Optional<User> lockById(Long id) {
    return one(" WHERE id=:id FOR UPDATE", args("id", id));
  }
}
