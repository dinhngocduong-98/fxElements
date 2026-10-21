<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";
const route = useRoute();
const token = computed(() =>
  new URLSearchParams(route.hash.slice(1)).get("token"),
);
const url = computed(
  () => "/api/v1/downloads/" + encodeURIComponent(token.value || ""),
);
</script>
<template>
  <main class="container page">
    <section class="panel narrow">
      <span class="eyebrow accent">YOUR ORIGINAL ASSET</span>
      <h1>Tải tài nguyên</h1>
      <p>
        Liên kết chỉ dùng với tài khoản đã mua và có thời hạn 24 giờ. Backend sẽ
        kiểm tra quyền trước khi gửi file.
      </p>
      <a v-if="token" class="button" :href="url">Tải file gốc ↓</a>
      <p v-else class="alert">Liên kết không có token hợp lệ.</p>
      <p class="muted">
        Nếu link hết hạn, cấp lại trong
        <RouterLink to="/account/purchases">Tài nguyên đã mua</RouterLink>.
      </p>
    </section>
  </main>
</template>
