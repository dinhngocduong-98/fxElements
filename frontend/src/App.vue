<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useSession, useCart } from "./stores";
import { errorMessage } from "./api";
const session = useSession(),
  cart = useCart(),
  router = useRouter(),
  error = ref(""),
  menu = ref(false);
onMounted(async () => {
  try {
    await cart.load();
  } catch (e) {
    error.value = errorMessage(e);
  }
});
async function logout() {
  try {
    await session.logout();
    await cart.load();
    router.push("/");
  } catch (e) {
    error.value = errorMessage(e);
  }
}
</script>
<template>
  <header class="header">
    <div class="container nav">
      <RouterLink class="brand" to="/" aria-label="FrameVault trang chủ"
        ><span class="brand-mark">F</span>FRAME<span>VAULT</span
        ><i>®</i></RouterLink
      ><button
        class="mobile-menu ghost"
        @click="menu = !menu"
        aria-label="Mở menu"
      >
        ☰
      </button>
      <nav :class="{ open: menu }" @click="menu = false">
        <RouterLink to="/library">Thư viện hiệu ứng</RouterLink
        ><RouterLink to="/library?featured=true"
          >Nổi bật <span class="tiny-dot"></span></RouterLink
        ><RouterLink v-if="session.admin" to="/admin">Quản trị ↗</RouterLink>
      </nav>
      <div class="nav-actions">
        <RouterLink class="cart-link" to="/cart"
          >Giỏ hàng <b>{{ cart.items.length }}</b></RouterLink
        ><RouterLink v-if="!session.user" class="button small" to="/login"
          >Đăng nhập ↗</RouterLink
        ><template v-else
          ><RouterLink class="account-link" to="/account">{{
            session.user.name
          }}</RouterLink
          ><button class="ghost small" @click="logout">Thoát</button></template
        >
      </div>
    </div>
  </header>
  <div v-if="error" class="container">
    <div role="alert" class="alert">
      {{ error }} <button class="ghost" @click="error = ''">Đóng</button>
    </div>
  </div>
  <RouterView />
  <footer class="footer">
    <div class="container footer-row">
      <div>
        <RouterLink class="brand" to="/">FRAME<span>VAULT</span></RouterLink>
        <p>Hiệu ứng cho những khung hình khác biệt.</p>
      </div>
      <div>
        <RouterLink to="/library">Khám phá thư viện ↗</RouterLink>
        <p>Preview có watermark · File gốc giao qua email</p>
      </div>
      <small>© {{ new Date().getFullYear() }} FrameVault</small>
    </div>
  </footer>
</template>
