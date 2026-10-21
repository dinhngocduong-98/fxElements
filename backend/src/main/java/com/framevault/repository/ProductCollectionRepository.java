package com.framevault.repository;

import com.framevault.domain.model.ProductCollection;
import com.framevault.domain.model.ProductCollectionId;
import com.framevault.repository.page.*;
import java.util.*;

public interface ProductCollectionRepository
    extends CrudRepository<ProductCollection, ProductCollectionId> {

  List<ProductCollection> findByProductId(Long productId);

  void deleteByProductId(Long productId);
}
