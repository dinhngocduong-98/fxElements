<script setup>
import { ref, computed, watch, onMounted } from "vue";
import { useRoute } from "vue-router";
import { useSession } from "../stores";
import { api, errorMessage } from "../api";
const route = useRoute(),
  session = useSession(),
  orders = ref([]),
  purchases = ref([]),
  selected = ref(null),
  error = ref(""),
  message = ref(""),
  loading = ref(false);
const section = computed(() => route.params.section || "profile");
const money = (v) => Number(v).toLocaleString("vi-VN") + " ₫";
async function load() {
  error.value = "";
  loading.value = true;
  try {
    if (section.value === "orders")
      orders.value = (await api.get("/orders")).data;
    if (section.value === "purchases")
      purchases.value = (await api.get("/me/purchases")).data;
  } catch (e) {
    error.value = errorMessage(e);
  } finally {
    loading.value = false;
  }
}
async function verify() {
  try {
    message.value = (
      await api.post("/auth/resend-verification", { email: session.user.email })
    ).data.message;
  } catch (e) {
    error.value = errorMessage(e);
  }
}
async function show(id) {
  try {
    selected.value = (await api.get("/orders/" + id)).data;
  } catch (e) {
    error.value = errorMessage(e);
  }
}
async function download(p) {
  try {
    const { data } = await api.post("/purchases/" + p.id + "/download-grants");
    window.location.assign(data.url);
  } catch (e) {
    error.value = errorMessage(e);
  }
}
onMounted(() => {
  message.value = sessionStorage.getItem("cart-merge-note") || "";
  sessionStorage.removeItem("cart-merge-note");
  load();
});
watch(section, () => {
  selected.value = null;
  load();
});
</script>
<template>
  <main class="container page">
    <span class="eyebrow accent">YOUR CREATIVE SPACE</span>
    <h1>Tài khoản của bạn</h1>
    <nav class="subnav">
      <RouterLink to="/account">Hồ sơ</RouterLink
      ><RouterLink to="/account/orders">Đơn hàng</RouterLink
      ><RouterLink to="/account/purchases">Tài nguyên đã mua</RouterLink>
    </nav>
    <div v-if="error" class="alert">{{ error }}</div>
    <div v-if="message" class="success">{{ message }}</div>
    <div v-if="loading" class="empty">Đang tải…</div>
    <section v-else-if="section === 'profile'" class="panel narrow">
      <h2>{{ session.user.name }}</h2>
      <p>{{ session.user.email }}</p>
      <span class="badge">{{
        session.user.verified ? "Email đã xác thực" : "Chưa xác thực email"
      }}</span>
      <p>
        <button v-if="!session.user.verified" class="button" @click="verify">
          Gửi lại email xác thực
        </button>
      </p>
    </section>
    <section v-else-if="section === 'orders'" class="panel">
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Mã đơn</th>
              <th>Ngày tạo</th>
              <th>Tổng</th>
              <th>Trạng thái</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="o in orders">
              <td class="mono">{{ o.id.slice(0, 8) }}</td>
              <td>{{ new Date(o.created_at).toLocaleString("vi-VN") }}</td>
              <td>{{ money(o.total) }}</td>
              <td>
                <span class="badge">{{ o.status }}</span>
              </td>
              <td>
                <button class="ghost small" @click="show(o.id)">
                  Chi tiết
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <p v-if="!orders.length" class="muted">Chưa có đơn hàng.</p>
      <section v-if="selected" class="order-detail">
        <h3>Đơn {{ selected.id }}</h3>
        <p>Trạng thái: {{ selected.status }}</p>
        <article v-for="i in selected.items">
          <b>{{ i.name }} — {{ money(i.price) }}</b>
          <details>
            <summary>Giấy phép {{ i.license_version }}</summary>
            <p class="pre-line">{{ i.license_body }}</p>
          </details>
        </article>
      </section>
    </section>
    <section v-else-if="section === 'purchases'">
      <div v-if="!purchases.length" class="empty">
        <h2>Chưa có tài nguyên đã mua</h2>
        <p>Chỉ đơn đã xác nhận thanh toán mới có quyền tải.</p>
      </div>
      <article v-for="p in purchases" class="panel purchase-row">
        <div>
          <h3>{{ p.name }}</h3>
          <span class="badge">{{ p.status }}</span>
          <details>
            <summary>Giấy phép {{ p.license_version }}</summary>
            <p class="pre-line">{{ p.license_body }}</p>
          </details>
        </div>
        <button
          class="button"
          :disabled="p.status !== 'ACTIVE'"
          @click="download(p)"
        >
          Tạo liên kết tải ↗
        </button>
      </article>
    </section>
  </main>
</template>
