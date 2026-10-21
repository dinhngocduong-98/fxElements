package com.framevault.repository;

import com.framevault.repository.page.*;
import java.util.*;

/** Persistence port; contains no SQL, JDBC, JPA or Spring annotations. */
public interface CrudRepository<T, ID> {

  Optional<T> findById(ID id);

  boolean existsById(ID id);

  T save(T model);

  void delete(T model);

  void deleteById(ID id);

  long count();

  List<T> findAll(Sort sort);

  Page<T> findAll(Pageable page);
}
