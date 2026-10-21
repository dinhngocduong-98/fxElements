package com.framevault.repository;

import com.framevault.domain.model.OrderItem;
import com.framevault.repository.page.*;
import java.util.*;

public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {

  List<OrderItem> findByOrderIdOrderById(String orderId);
}
