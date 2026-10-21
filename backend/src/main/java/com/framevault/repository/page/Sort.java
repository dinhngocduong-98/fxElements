package com.framevault.repository.page;

import java.util.*;

public final class Sort {

  public enum Direction {
    ASC,
    DESC
  }

  public static final class Order {

    private final String property;

    private final Direction direction;

    private Order(String property, Direction direction) {
      this.property = property;
      this.direction = direction;
    }

    public String getProperty() {
      return property;
    }

    public Direction getDirection() {
      return direction;
    }
  }

  private final List<Order> orders;

  private Sort(List<Order> orders) {
    this.orders = List.copyOf(orders);
  }

  public List<Order> getOrders() {
    return orders;
  }

  public static Sort by(String property) {
    return by(Direction.ASC, property);
  }

  public static Sort by(Direction direction, String property) {
    return new Sort(List.of(new Order(property, direction)));
  }

  public Sort ascending() {
    return direction(Direction.ASC);
  }

  public Sort descending() {
    return direction(Direction.DESC);
  }

  private Sort direction(Direction d) {
    List<Order> result = new ArrayList<>();
    for (Order o : orders) result.add(new Order(o.property, d));
    return new Sort(result);
  }

  public Sort and(Sort other) {
    List<Order> result = new ArrayList<>(orders);
    result.addAll(other.orders);
    return new Sort(result);
  }
}
