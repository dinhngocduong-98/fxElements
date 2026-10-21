<script setup>
import { ref, onMounted } from "vue";
import { useSession, useCart } from "../stores";
import { api, errorMessage } from "../api";
const session = useSession(),
  cart = useCart(),
  method = ref("card"),
  busy = ref(false),
  error = ref(""),
  order = ref(null),
  key = ref(crypto.randomUUID());
const money = (v) => Number(v).toLocaleString("vi-VN") + " ₫";
onMounted(async () => {
  try {
    await cart.load();
  } catch (e) {
    error.value = errorMessage(e);
  }
});
async function createOrder() {
  busy.value = true;
  try {
    order.value = (
      await api.post(
        "/orders",
        {
          expectedPrices: Object.fromEntries(
            cart.items.map((x) => [x.id, String(x.price)]),
          ),
        },
        { headers: { "Idempotency-Key": key.value } },
      )
    ).data;
    await cart.load();
  } catch (e) {
    error.value = errorMessage(e);
  } finally {
    busy.value = false;
  }
}
</script>
<template>
  <main class="container page">
    <span class="eyebrow accent">CHECKOUT</span>
    <h1>Hoàn tất lựa chọn.</h1>
    <div class="alert" v-if="error">{{ error }}</div>
    <div class="info">
      Thanh toán chưa tích hợp. Bạn có thể tạo đơn chờ thanh toán; file gốc chưa
      được cấp.
    </div>
    <div v-if="order" class="panel">
      <span class="badge">CHỜ THANH TOÁN</span>
      <h2>Đã tạo đơn hàng</h2>
      <p class="mono">{{ order.id }}</p>
      <p>
        Tổng tiền: <b>{{ money(order.total) }}</b>
      </p>
      <p class="muted">
        Đơn chưa được thanh toán. Chưa có email giao file hoặc quyền tải.
      </p>
      <RouterLink class="button" to="/account/orders"
        >Xem đơn hàng →</RouterLink
      >
    </div>
    <div v-else class="detail-layout">
      <div>
        <section class="panel">
          <h2>01 / Thông tin nhận hàng</h2>
          <p>{{ session.user.name }}</p>
          <p>{{ session.user.email }}</p>
          <p v-if="!session.user.verified" class="alert">
            Bạn cần xác thực email trước khi tạo đơn.
            <RouterLink to="/account">Gửi lại email →</RouterLink>
          </p>
        </section>
        <section class="panel">
          <h2>02 / Phương thức thanh toán</h2>
          <p class="muted">
            Bản thiết kế giao diện — chưa kết nối nhà cung cấp.
          </p>
          <label
            v-for="m in [
              { id: 'card', name: 'Thẻ thanh toán', desc: 'Visa / Mastercard' },
              {
                id: 'bank',
                name: 'Chuyển khoản ngân hàng',
                desc: 'QR / Internet Banking',
              },
              {
                id: 'wallet',
                name: 'Ví điện tử',
                desc: 'Nhà cung cấp sẽ được chọn sau',
              },
            ]"
            class="payment-option"
            :class="{ selected: method === m.id }"
            ><input v-model="method" type="radio" :value="m.id" /><span
              ><b>{{ m.name }}</b
              ><small>{{ m.desc }}</small></span
            ><span class="badge">Sắp tích hợp</span></label
          ><button class="button full" disabled>
            Thanh toán — chưa khả dụng
          </button>
          <p class="small-text muted">
            Không nhập thông tin thẻ hoặc chuyển tiền ở phiên bản này.
          </p>
        </section>
      </div>
      <aside class="purchase-panel">
        <h2>Đơn hàng của bạn</h2>
        <div v-for="p in cart.items" class="summary-line">
          <span>{{ p.name }}</span
          ><b>{{ money(p.price) }}</b>
        </div>
        <div class="summary-line price">
          <span>Tổng</span><b>{{ money(cart.total) }}</b>
        </div>
        <button
          class="ghost full"
          :disabled="busy || !cart.items.length || !session.user.verified"
          @click="createOrder"
        >
          {{ busy ? "Đang tạo…" : "Tạo đơn chờ thanh toán" }}
        </button>
        <p class="muted small-text">
          Đơn có hạn 30 phút. Tạo đơn không xác nhận thanh toán.
        </p>
      </aside>
    </div>
  </main>
</template>
