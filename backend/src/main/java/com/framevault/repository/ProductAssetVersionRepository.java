package com.framevault.repository;

import com.framevault.domain.model.ProductAssetVersion;
import com.framevault.repository.page.*;
import java.util.*;

public interface ProductAssetVersionRepository extends CrudRepository<ProductAssetVersion, Long> {

  Optional<ProductAssetVersion> findFirstByProductIdOrderByVersionNumberDesc(Long productId);
}
