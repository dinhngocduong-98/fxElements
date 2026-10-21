package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class EmailAttemptSqlAdapter extends AbstractSqlAdapter<EmailAttempt, Long>
    implements EmailAttemptRepository {

  private static final String SELECT_SQL =
      "SELECT id,job_id,success,error_code,created_at FROM email_attempts";

  private static final String INSERT_SQL =
      "INSERT INTO email_attempts(job_id,success,error_code,created_at)"
          + " VALUES(:jobId,:success,:errorCode,:createdAt)";

  private static final String UPDATE_SQL =
      "UPDATE email_attempts SET"
          + " job_id=:jobId,success=:success,error_code=:errorCode,created_at=:createdAt WHERE"
          + " id=:id";

  private static final String DELETE_SQL = "DELETE FROM email_attempts WHERE id=:id";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM email_attempts";

  private static final String KEY_WHERE = "id=:id";

  public EmailAttemptSqlAdapter(NamedParameterJdbcTemplate jdbc) {
    super(
        jdbc,
        EmailAttempt.class,
        new String[] {"id", "jobId", "success", "errorCode", "createdAt"},
        new String[] {"id"},
        true,
        SELECT_SQL,
        INSERT_SQL,
        UPDATE_SQL,
        DELETE_SQL,
        COUNT_SQL,
        KEY_WHERE);
  }

  public List<EmailAttempt> findByJobIdOrderByIdDesc(String id) {
    return select(" WHERE job_id=:id ORDER BY id DESC", args("id", id));
  }
}
