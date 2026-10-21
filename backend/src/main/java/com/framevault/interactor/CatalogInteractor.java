package com.framevault.interactor;

import static com.framevault.shared.Failures.*;

import com.framevault.domain.model.AssetCollection;
import com.framevault.domain.model.Category;
import com.framevault.domain.model.Product;
import com.framevault.domain.model.ProductAssetVersion;
import com.framevault.domain.model.ProductCollection;
import com.framevault.domain.model.ProductTag;
import com.framevault.domain.model.Tag;
import com.framevault.dto.request.CatalogFilterRequest;
import com.framevault.dto.request.NamedRequest;
import com.framevault.dto.request.ProductRequest;
import com.framevault.dto.response.AdminProductResponse;
import com.framevault.dto.response.AssetReferenceResponse;
import com.framevault.dto.response.NamedResponse;
import com.framevault.dto.response.PageResponse;
import com.framevault.dto.response.ProductResponse;
import com.framevault.mapper.ApiMapper;
import com.framevault.repository.AssetCollectionRepository;
import com.framevault.repository.CategoryRepository;
import com.framevault.repository.LicenseRepository;
import com.framevault.repository.MediaAssetRepository;
import com.framevault.repository.ProductAssetVersionRepository;
import com.framevault.repository.ProductCollectionRepository;
import com.framevault.repository.ProductRepository;
import com.framevault.repository.ProductTagRepository;
import com.framevault.repository.TagRepository;
import com.framevault.repository.page.Page;
import com.framevault.repository.page.PageRequest;
import com.framevault.repository.page.Sort;
import com.framevault.shared.Failures;
import com.framevault.usecase.AuditUseCase;
import com.framevault.usecase.CatalogUseCase;
import com.framevault.validation.ValidatorWrapper;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CatalogInteractor implements CatalogUseCase {

  private final ProductRepository products;

  private final CategoryRepository categories;

  private final TagRepository tags;

  private final AssetCollectionRepository collections;

  private final ProductTagRepository productTags;

  private final ProductCollectionRepository productCollections;

  private final LicenseRepository licenses;

  private final MediaAssetRepository media;

  private final ProductAssetVersionRepository versions;

  private final ApiMapper mapper;

  private final AuditUseCase audit;

  public CatalogInteractor(
      ProductRepository products,
      CategoryRepository categories,
      TagRepository tags,
      AssetCollectionRepository collections,
      ProductTagRepository productTags,
      ProductCollectionRepository productCollections,
      LicenseRepository licenses,
      MediaAssetRepository media,
      ProductAssetVersionRepository versions,
      ApiMapper mapper,
      AuditUseCase audit,
      ValidatorWrapper validator) {
    this.products = products;
    this.categories = categories;
    this.tags = tags;
    this.collections = collections;
    this.productTags = productTags;
    this.productCollections = productCollections;
    this.licenses = licenses;
    this.media = media;
    this.versions = versions;
    this.mapper = mapper;
    this.audit = audit;
    this.validator = validator;
  }

  @Transactional(readOnly = true)
  public PageResponse<ProductResponse> list(CatalogFilterRequest f, boolean admin) {
    validator.validate(f);
    Sort sort = Sort.by(Sort.Direction.DESC, "id");
    if ("priceAsc".equals(f.getSort())) sort = Sort.by("price").ascending().and(sort);
    if ("priceDesc".equals(f.getSort())) sort = Sort.by("price").descending().and(sort);
    Page<Product> page =
        products.search(criteria(f, admin), PageRequest.of(f.getPage(), f.getSize(), sort));
    return new PageResponse<>(
        page.getContent().stream().map(p -> mapper.product(p, admin)).collect(Collectors.toList()),
        page.getTotalElements(),
        f.getPage(),
        f.getSize());
  }

  @Transactional(readOnly = true)
  public ProductResponse detail(String slug, boolean admin) {
    validator.requireText("slug", slug);
    Product p =
        products
            .findBySlug(slug)
            .filter(x -> admin || "PUBLISHED".equals(x.getStatus()))
            .orElseThrow(Failures::missing);
    ProductResponse r = mapper.product(p, admin);
    r.setTags(
        productTags.findByProductId(p.getId()).stream()
            .map(pt -> tags.findById(pt.getTagId()).orElseThrow(Failures::missing))
            .map(t -> mapper.named(t.getId(), t.getName(), null))
            .collect(Collectors.toList()));
    r.setCollections(
        productCollections.findByProductId(p.getId()).stream()
            .map(pc -> collections.findById(pc.getCollectionId()).orElseThrow(Failures::missing))
            .map(c -> mapper.named(c.getId(), c.getName(), c.getSlug()))
            .collect(Collectors.toList()));
    r.setLicense(
        mapper.license(licenses.findFirstByOrderByIdDesc().orElseThrow(Failures::missing)));
    if (admin) {
      versions
          .findFirstByProductIdOrderByVersionNumberDesc(p.getId())
          .ifPresent(
              v -> {
                AssetReferenceResponse a = new AssetReferenceResponse();
                a.setAssetId(v.getAssetId());
                ((AdminProductResponse) r).getOriginalAssetId().add(a);
              });
    }
    return r;
  }

  @Transactional(readOnly = true)
  public List<NamedResponse> taxonomy(String kind) {
    switch (kind) {
      case "categories":
        return categories.findAll(Sort.by("name")).stream()
            .map(c -> mapper.named(c.getId(), c.getName(), c.getSlug()))
            .collect(Collectors.toList());
      case "tags":
        return tags.findAll(Sort.by("name")).stream()
            .map(t -> mapper.named(t.getId(), t.getName(), null))
            .collect(Collectors.toList());
      case "collections":
        return collections.findAll(Sort.by("name")).stream()
            .map(c -> mapper.named(c.getId(), c.getName(), c.getSlug()))
            .collect(Collectors.toList());
      default:
        throw missing();
    }
  }

  public List<NamedResponse> saveTaxonomy(String kind, Long id, NamedRequest f, String actor) {
    validator.requirePositive("id", id);
    validator.validate(f);
    if (!kind.equals("tags") && (f.getSlug() == null || f.getSlug().isBlank()))
      throw bad("Cần nhập slug");
    switch (kind) {
      case "categories":
        Category c =
            id == null ? new Category() : categories.findById(id).orElseThrow(Failures::missing);
        c.setName(f.getName());
        c.setSlug(f.getSlug());
        categories.save(c);
        break;
      case "tags":
        Tag t = id == null ? new Tag() : tags.findById(id).orElseThrow(Failures::missing);
        t.setName(f.getName());
        tags.save(t);
        break;
      case "collections":
        AssetCollection a =
            id == null
                ? new AssetCollection()
                : collections.findById(id).orElseThrow(Failures::missing);
        a.setName(f.getName());
        a.setSlug(f.getSlug());
        collections.save(a);
        break;
      default:
        throw missing();
    }
    audit.record(
        actor,
        id == null ? "CREATE_" + kind : "UPDATE_" + kind,
        id == null ? f.getName() : id.toString());
    return taxonomy(kind);
  }

  public List<NamedResponse> deleteTaxonomy(String kind, Long id, String actor) {
    validator.requirePositive("id", id);
    switch (kind) {
      case "categories":
        categories.deleteById(id);
        break;
      case "tags":
        tags.deleteById(id);
        break;
      case "collections":
        collections.deleteById(id);
        break;
      default:
        throw missing();
    }
    audit.record(actor, "DELETE_" + kind, id.toString());
    return taxonomy(kind);
  }

  public ProductResponse save(Long id, ProductRequest f, String actor) {
    if (id != null) validator.requirePositive("id", id);
    validator.validate(f);
    Product p = id == null ? new Product() : products.lockById(id).orElseThrow(Failures::missing);
    check(f.getThumbnailId(), "THUMBNAIL");
    check(f.getPreviewId(), "PREVIEW");
    check(f.getOriginalAssetId(), "ORIGINAL");
    Optional<ProductAssetVersion> previous =
        id == null ? Optional.empty() : versions.findFirstByProductIdOrderByVersionNumberDesc(id);
    if ("PUBLISHED".equals(f.getStatus())
        && (empty(f.getThumbnailId()) == null
            || empty(f.getPreviewId()) == null
            || (empty(f.getOriginalAssetId()) == null && !previous.isPresent())))
      throw bad("Cần đủ thumbnail, demo và file gốc để xuất bản");
    categories.findById(f.getCategoryId()).orElseThrow(Failures::missing);
    p.setName(f.getName());
    p.setSlug(f.getSlug());
    p.setDescription(f.getDescription());
    p.setPrice(f.getPrice().setScale(2));
    p.setStatus(f.getStatus());
    p.setCategoryId(f.getCategoryId());
    p.setWidth(f.getWidth());
    p.setHeight(f.getHeight());
    p.setFps(f.getFps());
    p.setFormat(f.getFormat());
    p.setAlphaChannel(f.getAlphaChannel());
    p.setDurationSeconds(f.getDurationSeconds());
    p.setFeatured(f.getFeatured());
    p.setThumbnailId(empty(f.getThumbnailId()));
    p.setPreviewId(empty(f.getPreviewId()));
    products.save(p);
    if (empty(f.getOriginalAssetId()) != null
        && (!previous.isPresent() || !f.getOriginalAssetId().equals(previous.get().getAssetId()))) {
      ProductAssetVersion v = new ProductAssetVersion();
      v.setProductId(p.getId());
      v.setAssetId(f.getOriginalAssetId());
      v.setVersionNumber(previous.map(x -> x.getVersionNumber() + 1).orElse(1));
      versions.save(v);
    }
    productTags.deleteByProductId(p.getId());
    for (Long tid : new HashSet<>(f.getTagIds())) {
      tags.findById(tid).orElseThrow(Failures::missing);
      ProductTag pt = new ProductTag();
      pt.setProductId(p.getId());
      pt.setTagId(tid);
      productTags.save(pt);
    }
    productCollections.deleteByProductId(p.getId());
    for (Long cid : new HashSet<>(f.getCollectionIds())) {
      collections.findById(cid).orElseThrow(Failures::missing);
      ProductCollection pc = new ProductCollection();
      pc.setProductId(p.getId());
      pc.setCollectionId(cid);
      productCollections.save(pc);
    }
    // Refresh the read-only association when categoryId changes.
    p.setCategory(categories.findById(p.getCategoryId()).orElseThrow(Failures::missing));
    audit.record(actor, id == null ? "CREATE_PRODUCT" : "UPDATE_PRODUCT", p.getId().toString());
    return detail(p.getSlug(), true);
  }

  private String empty(String value) {
    return value == null || value.isBlank() ? null : value;
  }

  private void check(String id, String kind) {
    if (empty(id) != null && !media.existsByIdAndKind(id, kind))
      throw bad("Tài nguyên không đúng loại: " + kind);
  }

  private com.framevault.repository.query.ProductQuery criteria(
      CatalogFilterRequest f, boolean admin) {
    com.framevault.repository.query.ProductQuery q =
        new com.framevault.repository.query.ProductQuery();
    q.setQuery(f.getQ());
    q.setCategoryId(f.getCategoryId());
    q.setCollectionId(f.getCollection_id());
    q.setHeight(f.getHeight());
    q.setFps(f.getFps());
    q.setFormat(f.getFormat());
    q.setAlphaChannel(f.getAlphaChannel());
    q.setMinPrice(f.getMinPrice());
    q.setMaxPrice(f.getMaxPrice());
    q.setFeatured(f.getFeatured());
    q.setPublishedOnly(!admin);
    return q;
  }

  private final ValidatorWrapper validator;
}
