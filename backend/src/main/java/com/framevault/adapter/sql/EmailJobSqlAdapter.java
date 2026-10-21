package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.time.Instant;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class EmailJobSqlAdapter extends AbstractSqlAdapter<EmailJob, String>
    implements EmailJobRepository {

  private static final String SELECT_SQL =
      "SELECT"
          + " id,recipient,subject,body,status,attempts,next_attempt,lease_until,last_error,created_at"
          + " FROM email_jobs";

  private static final String INSERT_SQL =
      "INSERT INTO"
          + " email_jobs(id,recipient,subject,body,status,attempts,next_attempt,lease_until,last_error,created_at)"
          + " VALUES(:id,:recipient,:subject,:body,:status,:attempts,:nextAttempt,:leaseUntil,:lastError,:createdAt)";

  private static final String UPDATE_SQL =
      "UPDATE email_jobs SET"
          + " recipient=:recipient,subject=:subject,body=:body,status=:status,attempts=:attempts,next_attempt=:nextAttempt,lease_until=:leaseUntil,last_error=:lastError,created_at=:createdAt"
          + " WHERE id=:id";

  private static final String DELETE_SQL = "DELETE FROM email_jobs WHERE id=:id";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM email_jobs";

  private static final String KEY_WHERE = "id=:id";

  public EmailJobSqlAdapter(NamedParameterJdbcTemplate jdbc) {
    super(
        jdbc,
        EmailJob.class,
        new String[] {
          "id",
          "recipient",
          "subject",
          "body",
          "status",
          "attempts",
          "nextAttempt",
          "leaseUntil",
          "lastError",
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

  public long countByStatus(String status) {
    return jdbc.queryForObject(
        "SELECT COUNT(*) FROM email_jobs WHERE status=:status", args("status", status), Long.class);
  }

  public List<EmailJob> due(Instant now, Pageable page) {
    return select(
        " WHERE (status IN ('PENDING','RETRY_WAIT') AND next_attempt<=:now) OR (status='PROCESSING'"
            + " AND lease_until<:now) ORDER BY created_at LIMIT :_limit OFFSET :_offset",
        args("now", now, "_limit", page.getPageSize(), "_offset", page.getOffset()));
  }

  public Optional<EmailJob> lockById(String id) {
    return one(" WHERE id=:id FOR UPDATE", args("id", id));
  }
}
