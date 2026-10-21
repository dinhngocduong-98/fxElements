package com.framevault.repository;

import com.framevault.domain.model.License;
import com.framevault.repository.page.*;
import java.util.*;

public interface LicenseRepository extends CrudRepository<License, Long> {

  Optional<License> findFirstByOrderByIdDesc();
}
