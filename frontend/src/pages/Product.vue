<script setup>
import { ref, watch } from "vue";
import { useRoute } from "vue-router";
import { api, errorMessage } from "../api";
import { useCart } from "../stores";
const route = useRoute(),
  cart = useCart(),
  p = ref(null),
  error = ref(""),
  message = ref("");
const money = (v) => Number(v).toLocaleString("vi-VN") + " ₫";
watch(
  () => route.params.slug,
  async () => {
    p.value = null;
    error.value = "";
    message.value = "";
    try {
      p.value = (await api.get("/products/" + route.params.slug)).data;
    } catch (e) {
      error.value = errorMessage(e);
    }
  },
  { immediate: true },
);
async function add() {
  try {
    await cart.add(p.value);
    message.value = "Đã thêm sản phẩm vào giỏ hàng";
  } catch (e) {
    error.value = errorMessage(e);
  }
}
</script>
<template>
  <main class="container page">
    <RouterLink class="muted" to="/library">← Trở về thư viện</RouterLink>
    <div v-if="error" class="alert">{{ error }}</div>
    <div v-if="p" class="detail-layout">
      <div>
        <div class="video-panel">
          <video
            v-if="p.preview_url"
            :src="p.preview_url"
            :poster="p.thumbnail_url"
            controls
            controlslist="nodownload"
            playsinline
            preload="metadata"
          ></video>
          <div v-else class="empty">Chưa có demo</div>
        </div>
        <div class="preview-notice">
          ▶ Bản xem thử · File gốc chỉ được cấp sau khi xác nhận thanh toán
        </div>
        <h2>Thông tin tài nguyên</h2>
        <p class="pre-line">{{ p.description }}</p>
        <div class="tag-list">
          <span v-for="t in p.tags" class="badge">{{ t.name }}</span>
        </div>
        <h3>Giấy phép {{ p.license.version }}</h3>
        <p class="pre-line muted">{{ p.license.body }}</p>
      </div>
      <aside class="purchase-panel">
        <span class="eyebrow accent">{{ p.category_name }}</span>
        <h1>{{ p.name }}</h1>
        <div class="price">{{ money(p.price) }}</div>
        <dl class="specs">
          <dt>Độ phân giải</dt>
          <dd>{{ p.width }} × {{ p.height }}</dd>
          <dt>Khung hình</dt>
          <dd>{{ Number(p.fps) }} FPS</dd>
          <dt>Định dạng</dt>
          <dd>{{ p.format }}</dd>
          <dt>Thời lượng</dt>
          <dd>{{ p.duration_seconds }} giây</dd>
          <dt>Alpha channel</dt>
          <dd>{{ p.alpha_channel ? "Có" : "Không" }}</dd>
        </dl>
        <button class="button full" @click="add">
          Thêm vào giỏ <span>+</span>
        </button>
        <p class="success" v-if="message">
          {{ message }} <RouterLink to="/cart">Xem giỏ →</RouterLink>
        </p>
        <p class="muted small-text">
          Liên kết tải file gốc được gửi qua email sau khi thanh toán được xác
          nhận. Bạn cũng có thể tải lại trong tài khoản.
        </p>
      </aside>
    </div>
    <div v-else-if="!error" class="empty">Đang tải…</div>
  </main>
</template>
