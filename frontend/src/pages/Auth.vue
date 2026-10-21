<script setup>
import { computed, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useSession, useCart } from "../stores";
import { api, errorMessage } from "../api";
const route = useRoute(),
  router = useRouter(),
  session = useSession(),
  cart = useCart();
const email = ref(""),
  name = ref(""),
  password = ref(""),
  error = ref(""),
  message = ref(""),
  busy = ref(false);
const mode = computed(() => route.path.slice(1));
const title = computed(
  () =>
    ({
      login: "Chào mừng trở lại.",
      register: "Bắt đầu sáng tạo.",
      "forgot-password": "Quên mật khẩu?",
      "reset-password": "Mật khẩu mới.",
      "verify-email": "Xác thực email.",
    })[mode.value],
);
watch(mode, () => {
  message.value = "";
  error.value = "";
  password.value = "";
});
const token = () =>
  new URLSearchParams(route.hash.replace(/^#/, "")).get("token") || "";
async function submit() {
  busy.value = true;
  error.value = "";
  message.value = "";
  try {
    if (mode.value === "login") {
      await session.login({ email: email.value, password: password.value });
      const skipped = await cart.merge();
      if (skipped.length)
        sessionStorage.setItem(
          "cart-merge-note",
          "Một số sản phẩm không thể thêm: " + skipped.join(", "),
        );
      const next = route.query.next;
      router.push(
        typeof next === "string" &&
          next.startsWith("/") &&
          !next.startsWith("//")
          ? next
          : "/account",
      );
      return;
    }
    let result;
    if (mode.value === "register")
      result = await api.post("/auth/register", {
        email: email.value,
        name: name.value,
        password: password.value,
      });
    if (mode.value === "forgot-password")
      result = await api.post("/auth/forgot-password", { email: email.value });
    if (mode.value === "reset-password")
      result = await api.post("/auth/reset-password", {
        token: token(),
        password: password.value,
      });
    if (mode.value === "verify-email") {
      result = await api.post("/auth/verify-email", { token: token() });
      if (session.user) await session.init();
    }
    message.value = result.data.message;
  } catch (e) {
    error.value = errorMessage(e);
  } finally {
    busy.value = false;
  }
}
</script>
<template>
  <main class="auth-shell container">
    <div class="auth-art">
      <span class="eyebrow accent">WELCOME TO FRAMEVAULT</span>
      <h1>Khung hình mới.<br />Khả năng mới.</h1>
      <p>Một nơi cho những ý tưởng đang chờ được kể.</p>
      <div class="auth-symbol">◈</div>
    </div>
    <section class="auth-box">
      <span class="eyebrow muted">YOUR CREATIVE SPACE</span>
      <h2>{{ title }}</h2>
      <p class="muted">
        {{
          mode === "verify-email"
            ? "Nhấn nút bên dưới để xác nhận liên kết trong email."
            : "Kết nối với thư viện tài nguyên của bạn."
        }}
      </p>
      <div class="alert" v-if="error" role="alert">{{ error }}</div>
      <div class="success" v-if="message" role="status">{{ message }}</div>
      <form @submit.prevent="submit">
        <label v-if="mode === 'register'"
          >Họ tên<input
            v-model="name"
            required
            maxlength="100"
            autocomplete="name" /></label
        ><label v-if="['login', 'register', 'forgot-password'].includes(mode)"
          >Email<input
            v-model="email"
            type="email"
            required
            maxlength="254"
            autocomplete="email" /></label
        ><label v-if="['login', 'register', 'reset-password'].includes(mode)"
          >Mật khẩu<input
            v-model="password"
            type="password"
            :minlength="mode === 'login' ? 1 : 10"
            maxlength="64"
            required
            :autocomplete="
              mode === 'login' ? 'current-password' : 'new-password'
            "
          /><small v-if="mode !== 'login'" class="muted"
            >Từ 10 đến 64 ký tự.</small
          ></label
        ><button class="button full" :disabled="busy">
          {{
            busy
              ? "Đang xử lý…"
              : mode === "login"
                ? "Đăng nhập ↗"
                : mode === "register"
                  ? "Tạo tài khoản ↗"
                  : mode === "verify-email"
                    ? "Xác thực email"
                    : "Xác nhận"
          }}
        </button>
      </form>
      <div class="auth-links">
        <RouterLink v-if="mode === 'login'" to="/forgot-password"
          >Quên mật khẩu?</RouterLink
        ><RouterLink v-if="mode === 'login'" to="/register"
          >Tạo tài khoản →</RouterLink
        ><RouterLink v-else to="/login">← Đăng nhập</RouterLink>
      </div>
    </section>
  </main>
</template>
