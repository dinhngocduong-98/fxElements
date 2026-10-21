package com.framevault.repository;

import com.framevault.domain.model.EmailJob;
import com.framevault.repository.page.*;
import java.time.Instant;
import java.util.*;

public interface EmailJobRepository extends CrudRepository<EmailJob, String> {

  long countByStatus(String status);

  List<EmailJob> due(Instant now, Pageable page);

  Optional<EmailJob> lockById(String id);
}
