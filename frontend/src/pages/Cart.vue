<script setup>
import { onMounted, ref } from "vue";
import { useCart, useSession } from "../stores";
import { errorMessage } from "../api";
const cart = useCart(),
  session = useSession(),
  error = ref("");
const money = (v) => Number(v).toLocaleString("vi-VN") + " ₫";
onMounted(async () => {
  try {
    await cart.load();
  } catch (e) {
    error.value = errorMessage(e);
  }
});
async function remove(id) {
  try {
    await cart.remove(id);
  } catch (e) {
    error.value = errorMessage(e);
  }
}
</script>
<template>
  <main class="container page">
    <span class="eyebrow accent">YOUR NEXT PROJECT</span>
    <h1>
      Giỏ hàng <span class="muted">({{ cart.items.length }})</span>
    </h1>
    <div class="alert" v-if="error">{{ error }}</div>
    <div v-if="cart.items.length" class="detail-layout">
      <div>
        <article class="cart-item" v-for="p in cart.items" :key="p.id">
          <img v-if="p.thumbnail_url" :src="p.thumbnail_url" alt="" />
          <div>
            <RouterLink :to="'/products/' + p.slug"
              ><h3>{{ p.name }}</h3></RouterLink
            >
            <p class="muted">{{ p.width }} × {{ p.height }} · {{ p.format }}</p>
            <button class="text-button" @click="remove(p.id)">
              Xóa khỏi giỏ
            </button>
          </div>
          <strong>{{ money(p.price) }}</strong>
        </article>
      </div>
      <aside class="purchase-panel">
        <h2>Tóm tắt đơn hàng</h2>
        <div class="summary-line">
          <span>{{ cart.items.length }} tài nguyên</span
          ><b>{{ money(cart.total) }}</b>
        </div>
        <p class="muted">
          Giá và tình trạng sản phẩm được kiểm tra lại khi tạo đơn.
        </p>
        <RouterLink
          class="button full"
          :to="session.user ? '/checkout' : '/login?next=/checkout'"
          >Tiếp tục ↗</RouterLink
        >
        <p class="small-text muted">
          Thanh toán hiện mới có giao diện. Đơn hàng chưa được thanh toán sẽ
          chưa có quyền tải.
        </p>
      </aside>
    </div>
    <div v-else class="empty">
      <h2>Giỏ hàng đang trống</h2>
      <RouterLink class="button" to="/library">Khám phá thư viện ↗</RouterLink>
    </div>
  </main>
</template>
