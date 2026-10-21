package com.framevault.repository;

import com.framevault.domain.model.User;
import com.framevault.repository.page.*;
import java.util.*;

public interface UserRepository extends CrudRepository<User, Long> {

  Optional<User> findByEmail(String email);

  long countByRole(String role);

  Optional<User> lockById(Long id);
}
