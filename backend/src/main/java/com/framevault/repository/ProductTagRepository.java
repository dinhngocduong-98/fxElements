package com.framevault.repository;

import com.framevault.domain.model.ProductTag;
import com.framevault.domain.model.ProductTagId;
import com.framevault.repository.page.*;
import java.util.*;

public interface ProductTagRepository extends CrudRepository<ProductTag, ProductTagId> {

  List<ProductTag> findByProductId(Long productId);

  void deleteByProductId(Long productId);
}
