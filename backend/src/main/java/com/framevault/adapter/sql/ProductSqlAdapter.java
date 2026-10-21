package com.framevault.adapter.sql;

import com.framevault.domain.model.*;
import com.framevault.repository.*;
import com.framevault.repository.page.*;
import java.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class ProductSqlAdapter extends AbstractSqlAdapter<Product, Long>
    implements ProductRepository {

  private static final String SELECT_SQL =
      "SELECT"
          + " id,name,slug,description,price,status,category_id,width,height,fps,format,alpha_channel,duration_seconds,featured,thumbnail_id,preview_id,created_at"
          + " FROM products";

  private static final String INSERT_SQL =
      "INSERT INTO"
          + " products(name,slug,description,price,status,category_id,width,height,fps,format,alpha_channel,duration_seconds,featured,thumbnail_id,preview_id,created_at)"
          + " VALUES(:name,:slug,:description,:price,:status,:categoryId,:width,:height,:fps,:format,:alphaChannel,:durationSeconds,:featured,:thumbnailId,:previewId,:createdAt)";

  private static final String UPDATE_SQL =
      "UPDATE products SET"
          + " name=:name,slug=:slug,description=:description,price=:price,status=:status,category_id=:categoryId,width=:width,height=:height,fps=:fps,format=:format,alpha_channel=:alphaChannel,duration_seconds=:durationSeconds,featured=:featured,thumbnail_id=:thumbnailId,preview_id=:previewId,created_at=:createdAt"
          + " WHERE id=:id";

  private static final String DELETE_SQL = "DELETE FROM products WHERE id=:id";

  private static final String COUNT_SQL = "SELECT COUNT(*) FROM products";

  private static final String KEY_WHERE = "id=:id";

  public ProductSqlAdapter(NamedParameterJdbcTemplate jdbc, CategoryRepository categories) {
    super(
        jdbc,
        Product.class,
        new String[] {
          "id",
          "name",
          "slug",
          "description",
          "price",
          "status",
          "categoryId",
          "width",
          "height",
          "fps",
          "format",
          "alphaChannel",
          "durationSeconds",
          "featured",
          "thumbnailId",
          "previewId",
          "createdAt"
        },
        new String[] {"id"},
        true,
        SELECT_SQL,
        INSERT_SQL,
        UPDATE_SQL,
        DELETE_SQL,
        COUNT_SQL,
        KEY_WHERE);
    this.categories = categories;
  }

  private final CategoryRepository categories;

  @Override
  protected Product hydrate(Product p) {
    p.setCategory(categories.findById(p.getCategoryId()).orElse(null));
    return p;
  }

  public Optional<Product> findBySlug(String slug) {
    return one(" WHERE slug=:slug", args("slug", slug));
  }

  public boolean existsByStatusAndThumbnailIdOrStatusAndPreviewId(
      String s, String thumbnail, String s2, String preview) {
    return jdbc.queryForObject(
            "SELECT COUNT(*) FROM products WHERE (status=:s AND thumbnail_id=:thumbnail) OR"
                + " (status=:s2 AND preview_id=:preview)",
            args("s", s, "thumbnail", thumbnail, "s2", s2, "preview", preview),
            Long.class)
        > 0;
  }

  public Optional<Product> lockById(Long id) {
    return one(" WHERE id=:id FOR UPDATE", args("id", id));
  }

  public Page<Product> search(com.framevault.repository.query.ProductQuery q, Pageable page) {
    StringBuilder where = new StringBuilder(" WHERE 1=1");
    MapSqlParameterSource params = args();
    if (q.getPublishedOnly()) where.append(" AND p.status='PUBLISHED'");
    if (q.getQuery() != null && !q.getQuery().isBlank()) {
      where.append(
          " AND (p.name LIKE :q OR p.description LIKE :q OR EXISTS(SELECT 1 FROM product_tags pt"
              + " JOIN tags t ON t.id=pt.tag_id WHERE pt.product_id=p.id AND t.name LIKE :q))");
      params.addValue("q", "%" + q.getQuery() + "%");
    }
    if (q.getCategoryId() != null) {
      where.append(" AND p.category_id=:category");
      params.addValue("category", q.getCategoryId());
    }
    if (q.getCollectionId() != null) {
      where.append(
          " AND EXISTS(SELECT 1 FROM product_collections pc WHERE pc.product_id=p.id AND"
              + " pc.collection_id=:collection)");
      params.addValue("collection", q.getCollectionId());
    }
    if (q.getHeight() != null) {
      where.append(" AND p.height=:height");
      params.addValue("height", q.getHeight());
    }
    if (q.getFps() != null) {
      where.append(" AND p.fps=:fps");
      params.addValue("fps", q.getFps());
    }
    if (q.getFormat() != null && !q.getFormat().isBlank()) {
      where.append(" AND p.format=:format");
      params.addValue("format", q.getFormat());
    }
    if (q.getAlphaChannel() != null) {
      where.append(" AND p.alpha_channel=:alpha");
      params.addValue("alpha", q.getAlphaChannel());
    }
    if (q.getMinPrice() != null) {
      where.append(" AND p.price>=:min");
      params.addValue("min", q.getMinPrice());
    }
    if (q.getMaxPrice() != null) {
      where.append(" AND p.price<=:max");
      params.addValue("max", q.getMaxPrice());
    }
    if (Boolean.TRUE.equals(q.getFeatured())) where.append(" AND p.featured=TRUE");
    long total = jdbc.queryForObject("SELECT COUNT(*) FROM products p" + where, params, Long.class);
    params.addValue("_limit", page.getPageSize()).addValue("_offset", page.getOffset());
    return new Page<>(
        query(
            "SELECT p.* FROM products p"
                + where
                + orderBy(page.getSort())
                + " LIMIT :_limit OFFSET :_offset",
            params),
        total);
  }
}
