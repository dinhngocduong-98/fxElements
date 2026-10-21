package com.framevault.adapter.sql;

import com.framevault.repository.CrudRepository;
import com.framevault.repository.page.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.*;

public abstract class AbstractSqlAdapter<T, ID> implements CrudRepository<T, ID> {

  protected final NamedParameterJdbcTemplate jdbc;

  protected final String selectSql;

  private final String insertSql, updateSql, deleteSql, countSql, keyWhere;

  private final Class<T> type;

  private final List<String> properties, keys;

  private final boolean generated;

  private final RowMapper<T> mapper;

  protected AbstractSqlAdapter(
      NamedParameterJdbcTemplate jdbc,
      Class<T> type,
      String[] properties,
      String[] keys,
      boolean generated,
      String selectSql,
      String insertSql,
      String updateSql,
      String deleteSql,
      String countSql,
      String keyWhere) {
    this.jdbc = jdbc;
    this.type = type;
    this.properties = List.of(properties);
    this.keys = List.of(keys);
    this.generated = generated;
    this.selectSql = selectSql;
    this.insertSql = insertSql;
    this.updateSql = updateSql;
    this.deleteSql = deleteSql;
    this.countSql = countSql;
    this.keyWhere = keyWhere;
    this.mapper = (rs, n) -> map(rs);
  }

  protected static String column(String name) {
    return name.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT);
  }

  private T map(ResultSet rs) throws SQLException {
    try {
      T value = type.getDeclaredConstructor().newInstance();
      BeanWrapperImpl bean = new BeanWrapperImpl(value);
      for (String property : properties) {
        String col = column(property);
        Class<?> t = bean.getPropertyType(property);
        Object v;
        if (t == Instant.class) {
          Timestamp stamp = rs.getTimestamp(col);
          v = stamp == null ? null : stamp.toInstant();
        } else if (t == Long.class) {
          long n = rs.getLong(col);
          v = rs.wasNull() ? null : n;
        } else if (t == long.class) v = rs.getLong(col);
        else if (t == Integer.class) {
          int n = rs.getInt(col);
          v = rs.wasNull() ? null : n;
        } else if (t == int.class) v = rs.getInt(col);
        else if (t == boolean.class || t == Boolean.class) v = rs.getBoolean(col);
        else if (t == BigDecimal.class) v = rs.getBigDecimal(col);
        else v = rs.getString(col);
        bean.setPropertyValue(property, v);
      }
      return hydrate(value);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Cannot map " + type.getName(), e);
    }
  }

  protected T hydrate(T value) {
    return value;
  }

  protected MapSqlParameterSource args(Object... pairs) {
    MapSqlParameterSource out = new MapSqlParameterSource();
    for (int i = 0; i < pairs.length; i += 2) {
      Object value = pairs[i + 1];
      out.addValue(
          (String) pairs[i], value instanceof Instant ? Timestamp.from((Instant) value) : value);
    }
    return out;
  }

  protected List<T> select(String suffix, MapSqlParameterSource parameters) {
    return query(selectSql + suffix, parameters);
  }

  protected List<T> query(String sql, MapSqlParameterSource parameters) {
    return jdbc.query(sql, parameters, mapper);
  }

  protected Optional<T> one(String suffix, MapSqlParameterSource parameters) {
    return select(suffix, parameters).stream().findFirst();
  }

  private MapSqlParameterSource key(ID id) {
    if (keys.size() == 1) return args(keys.get(0), id);
    BeanWrapperImpl bean = new BeanWrapperImpl(id);
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    for (String name : keys) parameters.addValue(name, bean.getPropertyValue(name));
    return parameters;
  }

  private MapSqlParameterSource values(T value) {
    BeanWrapperImpl bean = new BeanWrapperImpl(value);
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    for (String name : properties) {
      Object v = bean.getPropertyValue(name);
      parameters.addValue(name, v instanceof Instant ? Timestamp.from((Instant) v) : v);
    }
    return parameters;
  }

  public Optional<T> findById(ID id) {
    return one(" WHERE " + keyWhere, key(id));
  }

  public boolean existsById(ID id) {
    return findById(id).isPresent();
  }

  public T save(T value) {
    MapSqlParameterSource parameters = values(value);
    boolean insert;
    if (generated) insert = parameters.getValue("id") == null;
    else {
      insert = jdbc.queryForObject(countSql + " WHERE " + keyWhere, parameters, Long.class) == 0;
    }
    if (insert) {
      if (generated) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbc.update(insertSql, parameters, holder, new String[] {"id"});
        new BeanWrapperImpl(value)
            .setPropertyValue("id", Objects.requireNonNull(holder.getKey()).longValue());
      } else jdbc.update(insertSql, parameters);
    } else jdbc.update(updateSql, parameters);
    return value;
  }

  public void delete(T value) {
    jdbc.update(deleteSql, values(value));
  }

  public void deleteById(ID id) {
    jdbc.update(deleteSql, key(id));
  }

  public long count() {
    return jdbc.queryForObject(countSql, new MapSqlParameterSource(), Long.class);
  }

  protected String orderBy(Sort sort) {
    List<String> parts = new ArrayList<>();
    for (Sort.Order order : sort.getOrders()) {
      if (!properties.contains(order.getProperty()))
        throw new IllegalArgumentException("Unsupported sort property");
      parts.add(column(order.getProperty()) + " " + order.getDirection().name());
    }
    return parts.isEmpty() ? "" : " ORDER BY " + String.join(",", parts);
  }

  public List<T> findAll(Sort sort) {
    return select(orderBy(sort), new MapSqlParameterSource());
  }

  public Page<T> findAll(Pageable page) {
    return new Page<>(
        select(
            orderBy(page.getSort()) + " LIMIT :_limit OFFSET :_offset",
            args("_limit", page.getPageSize(), "_offset", page.getOffset())),
        count());
  }
}
