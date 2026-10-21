<script setup>
import { ref } from "vue";
import { useCart } from "../stores";
import { errorMessage } from "../api";
defineProps({ product: Object });
const cart = useCart(),
  message = ref(""),
  busy = ref(false);
const money = (v) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
    maximumFractionDigits: 0,
  }).format(v);
async function add(p) {
  busy.value = true;
  try {
    await cart.add(p);
    message.value = "Đã thêm vào giỏ";
  } catch (e) {
    message.value = errorMessage(e);
  } finally {
    busy.value = false;
  }
}
</script>
<template>
  <article class="product-card">
    <RouterLink
      :to="'/products/' + product.slug"
      class="visual"
      :class="'tone-' + (product.id % 4)"
      ><img
        v-if="product.thumbnail_url"
        :src="product.thumbnail_url"
        :alt="product.name"
        loading="lazy"
      /><span v-else class="visual-orb"></span
      ><span class="visual-label"
        >{{ product.height >= 2160 ? "4K" : "HD" }}
        <span v-if="product.alpha_channel">ALPHA</span></span
      ><span class="play">▶</span
      ><span class="visual-bottom"
        >DEMO PREVIEW <b>{{ product.duration_seconds }}s</b></span
      ></RouterLink
    >
    <div class="card-body">
      <span class="eyebrow muted">{{ product.category_name }}</span
      ><RouterLink :to="'/products/' + product.slug"
        ><h3>{{ product.name }}</h3></RouterLink
      >
      <div class="card-meta">
        {{ product.width }} × {{ product.height }} <span>·</span>
        {{ Number(product.fps) }} FPS <span>·</span> {{ product.format }}
      </div>
      <div class="card-bottom">
        <strong>{{ money(product.price) }}</strong
        ><button
          class="add-button"
          :disabled="busy"
          @click="add(product)"
          :aria-label="'Thêm ' + product.name + ' vào giỏ'"
        >
          +
        </button>
      </div>
      <small v-if="message" role="status">{{ message }}</small>
    </div>
  </article>
</template>
