package com.framevault.repository;

import com.framevault.domain.model.DownloadEntitlement;
import com.framevault.repository.page.*;
import java.util.*;

public interface DownloadEntitlementRepository extends CrudRepository<DownloadEntitlement, String> {

  List<DownloadEntitlement> findByUserIdOrderByCreatedAtDesc(Long uid);

  Optional<DownloadEntitlement> findByOrderItemId(Long itemId);

  long owned(Long uid, Long pid);

  Optional<DownloadEntitlement> lockById(String id);
}
