package com.framevault.repository;

import com.framevault.domain.model.Product;
import com.framevault.repository.page.*;
import java.util.*;

public interface ProductRepository extends CrudRepository<Product, Long> {

  Optional<Product> findBySlug(String slug);

  boolean existsByStatusAndThumbnailIdOrStatusAndPreviewId(
      String s, String thumbnail, String s2, String preview);

  Optional<Product> lockById(Long id);

  com.framevault.repository.page.Page<Product> search(
      com.framevault.repository.query.ProductQuery query, Pageable page);
}
