package com.framevault.controller;

import com.framevault.dto.request.CatalogFilterRequest;
import com.framevault.dto.response.NamedResponse;
import com.framevault.dto.response.PageResponse;
import com.framevault.dto.response.ProductResponse;
import com.framevault.usecase.CatalogUseCase;
import java.security.Principal;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CatalogController {

  private final CatalogUseCase catalog;

  public CatalogController(CatalogUseCase catalog) {
    this.catalog = catalog;
  }

  @GetMapping("/products")
  public PageResponse<ProductResponse> list(@ModelAttribute CatalogFilterRequest f) {
    return catalog.list(f, false);
  }

  @GetMapping("/products/{slug}")
  public ProductResponse detail(@PathVariable("slug") String slug) {
    return catalog.detail(slug, false);
  }

  @GetMapping("/categories")
  public List<NamedResponse> categories() {
    return catalog.taxonomy("categories");
  }

  @GetMapping("/tags")
  public List<NamedResponse> tags() {
    return catalog.taxonomy("tags");
  }

  @GetMapping("/collections")
  public List<NamedResponse> collections() {
    return catalog.taxonomy("collections");
  }

  private String name(Principal p) {
    return p == null ? null : p.getName();
  }
}
