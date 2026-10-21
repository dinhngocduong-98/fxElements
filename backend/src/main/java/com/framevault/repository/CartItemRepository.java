package com.framevault.repository;

import com.framevault.domain.model.CartItem;
import com.framevault.domain.model.CartItemId;
import com.framevault.repository.page.*;
import java.util.*;

public interface CartItemRepository extends CrudRepository<CartItem, CartItemId> {

  List<CartItem> findByUserIdOrderByProductId(Long userId);

  void deleteByUserId(Long userId);
}
