package com.framevault.controller;

import com.framevault.dto.request.CatalogFilterRequest;
import com.framevault.dto.request.NamedRequest;
import com.framevault.dto.request.ProductRequest;
import com.framevault.dto.response.NamedResponse;
import com.framevault.dto.response.PageResponse;
import com.framevault.dto.response.ProductResponse;
import com.framevault.usecase.CatalogUseCase;
import java.security.Principal;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminCatalogController {

  private final CatalogUseCase catalog;

  public AdminCatalogController(CatalogUseCase catalog) {
    this.catalog = catalog;
  }

  @GetMapping("/products")
  public PageResponse<ProductResponse> list(@ModelAttribute CatalogFilterRequest f) {
    return catalog.list(f, true);
  }

  @GetMapping("/products/{slug}")
  public ProductResponse detail(@PathVariable("slug") String slug) {
    return catalog.detail(slug, true);
  }

  @PostMapping("/products")
  public ProductResponse create(@RequestBody ProductRequest f, Principal p) {
    return catalog.save(null, f, name(p));
  }

  @PutMapping("/products/{id}")
  public ProductResponse update(
      @PathVariable("id") Long id, @RequestBody ProductRequest f, Principal p) {
    return catalog.save(id, f, name(p));
  }

  @GetMapping("/taxonomy/{kind}")
  public List<NamedResponse> taxonomy(@PathVariable("kind") String kind) {
    return catalog.taxonomy(kind);
  }

  @PostMapping("/taxonomy/{kind}")
  public List<NamedResponse> add(
      @PathVariable("kind") String kind, @RequestBody NamedRequest f, Principal p) {
    return catalog.saveTaxonomy(kind, null, f, name(p));
  }

  @PutMapping("/taxonomy/{kind}/{id}")
  public List<NamedResponse> edit(
      @PathVariable("kind") String kind,
      @PathVariable("id") Long id,
      @RequestBody NamedRequest f,
      Principal p) {
    return catalog.saveTaxonomy(kind, id, f, name(p));
  }

  @DeleteMapping("/taxonomy/{kind}/{id}")
  public List<NamedResponse> delete(
      @PathVariable("kind") String kind, @PathVariable("id") Long id, Principal p) {
    return catalog.deleteTaxonomy(kind, id, name(p));
  }

  private String name(Principal p) {
    return p == null ? null : p.getName();
  }
}
