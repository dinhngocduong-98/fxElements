import { describe, it, expect, vi, beforeEach } from "vitest";
import { mount, flushPromises } from "@vue/test-utils";
import { createPinia, setActivePinia } from "pinia";
import { useSession, useCart } from "../stores";
import Checkout from "../pages/Checkout.vue";
import ProductCard from "../components/ProductCard.vue";
import { api } from "../api";
vi.mock("../api", () => ({
  api: { get: vi.fn(), post: vi.fn(), delete: vi.fn() },
  refreshCsrf: vi.fn(),
  errorMessage: (e) => e.message,
}));
const asset = {
  id: 1,
  name: "Cinematic smoke",
  slug: "smoke",
  price: "249000.00",
  width: 3840,
  height: 2160,
  fps: 30,
  format: "MOV",
  duration_seconds: 10,
  category_name: "Smoke",
};
beforeEach(() => {
  vi.clearAllMocks();
  localStorage.clear();
  setActivePinia(createPinia());
});
describe("Checkout without payment API", () => {
  it("disables actual payment and creates only a pending order", async () => {
    useSession().user = {
      id: 1,
      name: "Buyer",
      email: "buyer@example.com",
      verified: true,
    };
    api.get
      .mockResolvedValueOnce({ data: [asset] })
      .mockResolvedValue({ data: [] });
    api.post.mockResolvedValue({
      data: { id: "order-1", total: "249000.00", status: "PENDING_PAYMENT" },
    });
    const wrapper = mount(Checkout, {
      global: { stubs: { RouterLink: true } },
    });
    await flushPromises();
    const payment = wrapper
      .findAll("button")
      .find((b) => b.text().includes("Thanh toán —"));
    expect(payment.attributes("disabled")).toBeDefined();
    await wrapper
      .findAll("button")
      .find((b) => b.text().includes("Tạo đơn chờ"))
      .trigger("click");
    await flushPromises();
    expect(api.post).toHaveBeenCalledTimes(1);
    expect(api.post.mock.calls[0][0]).toBe("/orders");
    expect(api.post.mock.calls[0][1]).toEqual({
      expectedPrices: { 1: "249000.00" },
    });
    expect(wrapper.text()).toContain("Đơn chưa được thanh toán");
  });
  it("blocks order creation for unverified email", async () => {
    useSession().user = {
      name: "Buyer",
      email: "buyer@example.com",
      verified: false,
    };
    api.get.mockResolvedValue({ data: [asset] });
    const wrapper = mount(Checkout, {
      global: { stubs: { RouterLink: true } },
    });
    await flushPromises();
    expect(
      wrapper
        .findAll("button")
        .find((b) => b.text().includes("Tạo đơn chờ"))
        .attributes("disabled"),
    ).toBeDefined();
    expect(api.post).not.toHaveBeenCalled();
  });
});
describe("Guest cart", () => {
  it("deduplicates products and saves the cart locally", async () => {
    const wrapper = mount(ProductCard, {
      props: { product: asset },
      global: { stubs: { RouterLink: true } },
    });
    await wrapper.get("button").trigger("click");
    await flushPromises();
    await wrapper.get("button").trigger("click");
    await flushPromises();
    expect(useCart().items).toHaveLength(1);
    expect(JSON.parse(localStorage.getItem("framevault-cart"))).toHaveLength(1);
    expect(api.post).not.toHaveBeenCalled();
  });
});
