package com.framevault.interactor;

import static com.framevault.shared.Failures.*;

import com.framevault.domain.model.CartItem;
import com.framevault.domain.model.CartItemId;
import com.framevault.domain.model.CustomerOrder;
import com.framevault.domain.model.License;
import com.framevault.domain.model.OrderItem;
import com.framevault.domain.model.Product;
import com.framevault.domain.model.ProductAssetVersion;
import com.framevault.domain.model.User;
import com.framevault.dto.request.AddCartItemRequest;
import com.framevault.dto.request.CheckoutRequest;
import com.framevault.dto.response.OrderItemResponse;
import com.framevault.dto.response.OrderResponse;
import com.framevault.dto.response.ProductResponse;
import com.framevault.mapper.ApiMapper;
import com.framevault.repository.CartItemRepository;
import com.framevault.repository.CustomerOrderRepository;
import com.framevault.repository.DownloadEntitlementRepository;
import com.framevault.repository.LicenseRepository;
import com.framevault.repository.OrderItemRepository;
import com.framevault.repository.ProductAssetVersionRepository;
import com.framevault.repository.ProductRepository;
import com.framevault.repository.UserRepository;
import com.framevault.shared.Failures;
import com.framevault.usecase.CommerceUseCase;
import com.framevault.usecase.CurrentUserUseCase;
import com.framevault.validation.ValidatorWrapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommerceInteractor implements CommerceUseCase {

  private final UserRepository users;

  private final ProductRepository products;

  private final CartItemRepository carts;

  private final CustomerOrderRepository orders;

  private final OrderItemRepository items;

  private final ProductAssetVersionRepository versions;

  private final LicenseRepository licenses;

  private final DownloadEntitlementRepository entitlements;

  private final CurrentUserUseCase current;

  private final ApiMapper mapper;

  public CommerceInteractor(
      UserRepository users,
      ProductRepository products,
      CartItemRepository carts,
      CustomerOrderRepository orders,
      OrderItemRepository items,
      ProductAssetVersionRepository versions,
      LicenseRepository licenses,
      DownloadEntitlementRepository entitlements,
      CurrentUserUseCase current,
      ApiMapper mapper,
      ValidatorWrapper validator) {
    this.users = users;
    this.products = products;
    this.carts = carts;
    this.orders = orders;
    this.items = items;
    this.versions = versions;
    this.licenses = licenses;
    this.entitlements = entitlements;
    this.current = current;
    this.mapper = mapper;
    this.validator = validator;
  }

  @Transactional(readOnly = true)
  public List<ProductResponse> cart(String email) {
    User u = current.require(email);
    return cartFor(u.getId());
  }

  private List<ProductResponse> cartFor(Long uid) {
    return carts.findByUserIdOrderByProductId(uid).stream()
        .map(c -> products.findById(c.getProductId()).orElseThrow(Failures::missing))
        .map(p -> mapper.product(p, false))
        .collect(Collectors.toList());
  }

  public List<ProductResponse> add(String email, AddCartItemRequest f) {
    validator.validate(f);
    User u = lockUser(email);
    Product p =
        products
            .findById(f.getProductId())
            .filter(x -> "PUBLISHED".equals(x.getStatus()))
            .orElseThrow(() -> conflict("Sản phẩm không còn mở bán"));
    if (entitlements.owned(u.getId(), p.getId()) > 0) throw conflict("Bạn đã sở hữu sản phẩm này");
    CartItemId key = new CartItemId(u.getId(), p.getId());
    if (!carts.existsById(key)) {
      CartItem c = new CartItem();
      c.setUserId(u.getId());
      c.setProductId(p.getId());
      carts.save(c);
    }
    return cartFor(u.getId());
  }

  public List<ProductResponse> remove(String email, Long pid) {
    validator.requirePositive("pid", pid);
    User u = lockUser(email);
    carts.findById(new CartItemId(u.getId(), pid)).ifPresent(carts::delete);
    return cartFor(u.getId());
  }

  private User lockUser(String email) {
    return users.lockById(current.require(email).getId()).orElseThrow(Failures::missing);
  }

  public OrderResponse create(String email, String key, CheckoutRequest f) {
    validator.validate(f);
    if (key == null || !key.matches("[a-zA-Z0-9_-]{16,100}"))
      throw bad("Idempotency-Key không hợp lệ");
    User u = lockUser(email);
    if (!u.getVerified())
      throw new org.springframework.web.server.ResponseStatusException(
          org.springframework.http.HttpStatus.FORBIDDEN,
          "Vui lòng xác thực email trước khi đặt hàng");
    Optional<CustomerOrder> previous = orders.findByUserIdAndIdempotencyKey(u.getId(), key);
    if (previous.isPresent()) return orderDetail(previous.get(), false);
    List<CartItem> cart = carts.findByUserIdOrderByProductId(u.getId());
    if (cart.isEmpty()) throw conflict("Giỏ hàng đang trống");
    List<Product> selected = new ArrayList<>();
    List<ProductAssetVersion> selectedVersions = new ArrayList<>();
    BigDecimal total = BigDecimal.ZERO;
    for (CartItem ci : cart) {
      Product p = products.lockById(ci.getProductId()).orElseThrow(Failures::missing);
      if (!"PUBLISHED".equals(p.getStatus())) throw conflict("Sản phẩm chưa sẵn sàng bán");
      ProductAssetVersion v =
          versions
              .findFirstByProductIdOrderByVersionNumberDesc(p.getId())
              .orElseThrow(() -> conflict("Sản phẩm chưa sẵn sàng bán"));
      if (entitlements.owned(u.getId(), p.getId()) > 0)
        throw conflict("Giỏ hàng có sản phẩm đã sở hữu");
      try {
        String expected = f.getExpectedPrices().get(p.getId().toString());
        if (expected == null || new BigDecimal(expected).compareTo(p.getPrice()) != 0)
          throw conflict("Giá đã thay đổi. Vui lòng tải lại giỏ hàng.");
      } catch (NumberFormatException e) {
        throw conflict("Giá không hợp lệ");
      }
      selected.add(p);
      selectedVersions.add(v);
      total = total.add(p.getPrice());
    }
    CustomerOrder o = new CustomerOrder();
    o.setId(UUID.randomUUID().toString());
    o.setUserId(u.getId());
    o.setEmail(u.getEmail());
    o.setCurrency("VND");
    o.setTotal(total);
    o.setStatus("PENDING_PAYMENT");
    o.setIdempotencyKey(key);
    o.setExpiresAt(Instant.now().plusSeconds(1800));
    orders.save(o);
    License license = licenses.findFirstByOrderByIdDesc().orElseThrow(Failures::missing);
    for (int n = 0; n < selected.size(); n++) {
      Product p = selected.get(n);
      OrderItem i = new OrderItem();
      i.setOrderId(o.getId());
      i.setProductId(p.getId());
      i.setAssetVersionId(selectedVersions.get(n).getId());
      i.setName(p.getName());
      i.setPrice(p.getPrice());
      i.setLicenseVersion(license.getVersion());
      i.setLicenseBody(license.getBody());
      items.save(i);
    }
    carts.deleteByUserId(u.getId());
    return orderDetail(o, false);
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> orders(String email) {
    return orders.findByUserIdOrderByCreatedAtDesc(current.require(email).getId()).stream()
        .map(mapper::order)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public OrderResponse detail(String email, String id) {
    validator.requireText("id", id);
    CustomerOrder o =
        orders.findByIdAndUserId(id, current.require(email).getId()).orElseThrow(Failures::missing);
    return orderDetail(o, false);
  }

  @Transactional(readOnly = true)
  public OrderResponse adminDetail(String id) {
    validator.requireText("id", id);
    return orderDetail(orders.findById(id).orElseThrow(Failures::missing), true);
  }

  private OrderResponse orderDetail(CustomerOrder o, boolean admin) {
    OrderResponse r = mapper.order(o);
    r.setItems(
        items.findByOrderIdOrderById(o.getId()).stream()
            .map(
                i -> {
                  OrderItemResponse d = mapper.item(i);
                  if (admin)
                    entitlements
                        .findByOrderItemId(i.getId())
                        .ifPresent(
                            e -> {
                              d.setEntitlementId(e.getId());
                              d.setEntitlementStatus(e.getStatus());
                            });
                  return d;
                })
            .collect(Collectors.toList()));
    return r;
  }

  public void expire() {
    orders.expire(Instant.now());
  }

  private final ValidatorWrapper validator;
}
