package com.framevault.repository;

import com.framevault.domain.model.EmailAttempt;
import com.framevault.repository.page.*;
import java.util.*;

public interface EmailAttemptRepository extends CrudRepository<EmailAttempt, Long> {

  List<EmailAttempt> findByJobIdOrderByIdDesc(String id);
}
