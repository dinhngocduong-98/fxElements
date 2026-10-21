package com.framevault.usecase;

import static com.framevault.shared.Failures.*;

import com.framevault.dto.request.AddCartItemRequest;
import com.framevault.dto.request.CheckoutRequest;
import com.framevault.dto.response.OrderResponse;
import com.framevault.dto.response.ProductResponse;
import java.util.List;

public interface CommerceUseCase {

  List<ProductResponse> cart(String email);

  List<ProductResponse> add(String email, AddCartItemRequest f);

  List<ProductResponse> remove(String email, Long pid);

  OrderResponse create(String email, String key, CheckoutRequest f);

  List<OrderResponse> orders(String email);

  OrderResponse detail(String email, String id);

  OrderResponse adminDetail(String id);

  void expire();
}
