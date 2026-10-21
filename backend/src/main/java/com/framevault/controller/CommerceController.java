package com.framevault.controller;

import com.framevault.dto.request.AddCartItemRequest;
import com.framevault.dto.request.CheckoutRequest;
import com.framevault.dto.response.OrderResponse;
import com.framevault.dto.response.ProductResponse;
import com.framevault.usecase.CommerceUseCase;
import java.security.Principal;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CommerceController {

  private final CommerceUseCase commerce;

  public CommerceController(CommerceUseCase commerce) {
    this.commerce = commerce;
  }

  @GetMapping("/cart")
  public List<ProductResponse> cart(Principal p) {
    return commerce.cart(name(p));
  }

  @PostMapping("/cart/items")
  public List<ProductResponse> add(@RequestBody AddCartItemRequest f, Principal p) {
    return commerce.add(name(p), f);
  }

  @DeleteMapping("/cart/items/{id}")
  public List<ProductResponse> remove(@PathVariable("id") Long id, Principal p) {
    return commerce.remove(name(p), id);
  }

  @PostMapping("/orders")
  public OrderResponse create(
      @RequestHeader("Idempotency-Key") String key, @RequestBody CheckoutRequest f, Principal p) {
    return commerce.create(name(p), key, f);
  }

  @GetMapping("/orders")
  public List<OrderResponse> orders(Principal p) {
    return commerce.orders(name(p));
  }

  @GetMapping("/orders/{id}")
  public OrderResponse detail(@PathVariable("id") String id, Principal p) {
    return commerce.detail(name(p), id);
  }

  private String name(Principal p) {
    return p == null ? null : p.getName();
  }
}
