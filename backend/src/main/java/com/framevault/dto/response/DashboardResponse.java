package com.framevault.dto.response;

import java.util.List;

public class DashboardResponse {

  private long products;

  private long customers;

  private long orders;

  private long failedEmails;

  private String revenue;

  private List<OrderResponse> recentOrders;

  public long getProducts() {
    return products;
  }

  public void setProducts(long value) {
    this.products = value;
  }

  public long getCustomers() {
    return customers;
  }

  public void setCustomers(long value) {
    this.customers = value;
  }

  public long getOrders() {
    return orders;
  }

  public void setOrders(long value) {
    this.orders = value;
  }

  public long getFailedEmails() {
    return failedEmails;
  }

  public void setFailedEmails(long value) {
    this.failedEmails = value;
  }

  public String getRevenue() {
    return revenue;
  }

  public void setRevenue(String value) {
    this.revenue = value;
  }

  public List<OrderResponse> getRecentOrders() {
    return recentOrders;
  }

  public void setRecentOrders(List<OrderResponse> value) {
    this.recentOrders = value;
  }
}
