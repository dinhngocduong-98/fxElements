package com.framevault.usecase;

import static com.framevault.shared.Failures.*;

import com.framevault.dto.request.CatalogFilterRequest;
import com.framevault.dto.request.NamedRequest;
import com.framevault.dto.request.ProductRequest;
import com.framevault.dto.response.NamedResponse;
import com.framevault.dto.response.PageResponse;
import com.framevault.dto.response.ProductResponse;
import java.util.List;

public interface CatalogUseCase {

  PageResponse<ProductResponse> list(CatalogFilterRequest f, boolean admin);

  ProductResponse detail(String slug, boolean admin);

  List<NamedResponse> taxonomy(String kind);

  List<NamedResponse> saveTaxonomy(String kind, Long id, NamedRequest f, String actor);

  List<NamedResponse> deleteTaxonomy(String kind, Long id, String actor);

  ProductResponse save(Long id, ProductRequest f, String actor);
}
