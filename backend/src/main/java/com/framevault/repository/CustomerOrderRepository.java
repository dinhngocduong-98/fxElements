package com.framevault.repository;

import com.framevault.domain.model.CustomerOrder;
import com.framevault.repository.page.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

public interface CustomerOrderRepository extends CrudRepository<CustomerOrder, String> {

  List<CustomerOrder> findByUserIdOrderByCreatedAtDesc(Long uid);

  Optional<CustomerOrder> findByIdAndUserId(String id, Long uid);

  Optional<CustomerOrder> findByUserIdAndIdempotencyKey(Long uid, String key);

  List<CustomerOrder> findTop8ByOrderByCreatedAtDesc();

  BigDecimal revenue();

  int expire(Instant now);
}
