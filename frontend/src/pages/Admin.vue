<script setup>
import { ref, reactive, computed, watch, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { api, errorMessage } from "../api";
const route = useRoute(),
  router = useRouter(),
  section = computed(() => route.params.section || "dashboard");
const nav = [
  ["dashboard", "Tổng quan", "◈"],
  ["products", "Sản phẩm", "▦"],
  ["categories", "Danh mục", "≡"],
  ["tags", "Tags", "⌗"],
  ["collections", "Bộ sưu tập", "▧"],
  ["orders", "Đơn hàng", "▤"],
  ["users", "Khách hàng", "◎"],
  ["email-jobs", "Email", "✉"],
  ["audit-logs", "Nhật ký", "↳"],
];
const rows = ref([]),
  dashboard = ref(null),
  page = ref(0),
  total = ref(0),
  error = ref(""),
  message = ref(""),
  busy = ref(false),
  loading = ref(false),
  editing = ref(false),
  selected = ref(null),
  query = ref("");
const categories = ref([]),
  tags = ref([]),
  collections = ref([]),
  taxonomy = reactive({ id: null, name: "", slug: "" });
const initial = () => ({
  id: null,
  name: "",
  slug: "",
  description: "",
  price: "249000",
  status: "DRAFT",
  categoryId: categories.value[0]?.id || null,
  width: 3840,
  height: 2160,
  fps: 30,
  format: "MOV",
  alphaChannel: false,
  durationSeconds: 10,
  featured: false,
  thumbnailId: "",
  previewId: "",
  originalAssetId: "",
  tagIds: [],
  collectionIds: [],
});
const form = reactive(initial());
const titles = Object.fromEntries(nav.map((x) => [x[0], x[1]]));
const money = (v) => Number(v).toLocaleString("vi-VN") + " ₫";
async function task(fn) {
  busy.value = true;
  error.value = "";
  message.value = "";
  try {
    await fn();
  } catch (e) {
    error.value = errorMessage(e);
  } finally {
    busy.value = false;
  }
}
async function load() {
  loading.value = true;
  error.value = "";
  try {
    const s = section.value;
    if (s === "dashboard")
      dashboard.value = (await api.get("/admin/dashboard")).data;
    else if (s === "products") {
      const data = (
        await api.get("/admin/products", {
          params: { page: page.value, size: 12, q: query.value },
        })
      ).data;
      rows.value = data.items;
      total.value = data.total;
    } else if (["categories", "tags", "collections"].includes(s))
      rows.value = (await api.get("/admin/taxonomy/" + s)).data;
    else
      rows.value = (
        await api.get("/admin/" + s, { params: { page: page.value } })
      ).data;
  } catch (e) {
    error.value = errorMessage(e);
  } finally {
    loading.value = false;
  }
}
async function metadata() {
  const [a, b, c] = await Promise.all([
    api.get("/categories"),
    api.get("/tags"),
    api.get("/collections"),
  ]);
  categories.value = a.data;
  tags.value = b.data;
  collections.value = c.data;
}
function newProduct() {
  Object.assign(form, initial());
  editing.value = true;
  selected.value = null;
}
async function edit(p) {
  await task(async () => {
    const d = (await api.get("/admin/products/" + p.slug)).data;
    Object.assign(form, {
      id: d.id,
      name: d.name,
      slug: d.slug,
      description: d.description,
      price: d.price,
      status: d.status,
      categoryId: d.category_id,
      width: d.width,
      height: d.height,
      fps: d.fps,
      format: d.format,
      alphaChannel: !!d.alpha_channel,
      durationSeconds: d.duration_seconds,
      featured: !!d.featured,
      thumbnailId: d.thumbnail_id || "",
      previewId: d.preview_id || "",
      originalAssetId: d.original_asset_id?.[0]?.asset_id || "",
      tagIds: d.tags.map((t) => t.id),
      collectionIds: d.collections.map((c) => c.id),
    });
    editing.value = true;
  });
}
async function upload(event, kind, field) {
  const file = event.target.files[0];
  if (!file) return;
  await task(async () => {
    const body = new FormData();
    body.append("kind", kind);
    body.append("file", file);
    const data = (
      await api.post("/admin/media/uploads", body, { timeout: 600000 })
    ).data;
    form[field] = data.id;
    message.value = "Upload thành công: " + data.name;
  });
  event.target.value = "";
}
async function saveProduct() {
  await task(async () => {
    const payload = { ...form };
    delete payload.id;
    if (form.id) await api.put("/admin/products/" + form.id, payload);
    else await api.post("/admin/products", payload);
    editing.value = false;
    message.value = "Đã lưu sản phẩm";
    await load();
  });
}
async function saveTaxonomy() {
  await task(async () => {
    const path = "/admin/taxonomy/" + section.value;
    if (taxonomy.id)
      await api.put(path + "/" + taxonomy.id, {
        name: taxonomy.name,
        slug: taxonomy.slug || null,
      });
    else
      await api.post(path, {
        name: taxonomy.name,
        slug: taxonomy.slug || null,
      });
    Object.assign(taxonomy, { id: null, name: "", slug: "" });
    await metadata();
    await load();
    message.value = "Đã lưu";
  });
}
async function deleteTaxonomy(r) {
  if (
    !window.confirm(
      "Xóa " + r.name + "? Dữ liệu đang được dùng sẽ không xóa được.",
    )
  )
    return;
  await task(async () => {
    await api.delete("/admin/taxonomy/" + section.value + "/" + r.id);
    await metadata();
    await load();
  });
}
async function toggleUser(u) {
  await task(async () => {
    await api.put("/admin/users/" + u.id + "/enabled", { enabled: !u.enabled });
    await load();
  });
}
async function showOrder(id) {
  await task(async () => {
    selected.value = (await api.get("/admin/orders/" + id)).data;
  });
}
async function resendOrder(id) {
  await task(async () => {
    message.value = (
      await api.post("/admin/orders/" + id + "/resend-delivery")
    ).data.message;
  });
}
async function toggleEntitlement(i) {
  await task(async () => {
    await api.put("/admin/entitlements/" + i.entitlement_id, {
      status: i.entitlement_status === "ACTIVE" ? "REVOKED" : "ACTIVE",
    });
    selected.value = (await api.get("/admin/orders/" + selected.value.id)).data;
  });
}
async function retry(id) {
  await task(async () => {
    message.value = (
      await api.post("/admin/email-jobs/" + id + "/retry")
    ).data.message;
    await load();
  });
}
async function attempts(id) {
  await task(async () => {
    selected.value = {
      attempts: (await api.get("/admin/email-jobs/" + id + "/attempts")).data,
    };
  });
}
watch(section, () => {
  page.value = 0;
  editing.value = false;
  selected.value = null;
  message.value = "";
  Object.assign(taxonomy, { id: null, name: "", slug: "" });
  load();
});
onMounted(async () => {
  await task(metadata);
  await load();
});
</script>
<template>
  <main class="admin-shell">
    <aside class="admin-sidebar">
      <span class="eyebrow accent">WORKSPACE / ADMIN</span>
      <h2>Studio control.</h2>
      <RouterLink
        v-for="n in nav"
        :key="n[0]"
        :to="'/admin/' + n[0]"
        :class="{ active: section === n[0] }"
        ><span>{{ n[2] }}</span
        >{{ n[1] }}</RouterLink
      >
      <div class="admin-foot">
        FRAMEVAULT<br /><small>Content & commerce workspace</small>
      </div>
    </aside>
    <section class="admin-main">
      <div class="section-heading">
        <div>
          <span class="eyebrow muted"
            >FRAMEVAULT / {{ section.toUpperCase() }}</span
          >
          <h1>{{ titles[section] || "Quản trị" }}</h1>
        </div>
        <button
          v-if="section === 'products' && !editing"
          class="button small"
          @click="newProduct"
        >
          + Thêm sản phẩm</button
        ><button v-else class="ghost small" @click="load">Làm mới ↻</button>
      </div>
      <div class="alert" v-if="error" role="alert">{{ error }}</div>
      <div class="success" v-if="message" role="status">{{ message }}</div>
      <div v-if="loading" class="loading-line">Đang tải dữ liệu…</div>
      <template v-if="section === 'dashboard' && dashboard"
        ><div class="stat-grid">
          <article>
            <span>Doanh thu đã xác nhận</span>
            <h2>{{ money(dashboard.revenue) }}</h2>
            <small>Chưa tích hợp thanh toán</small>
          </article>
          <article>
            <span>Đơn hàng</span>
            <h2>{{ dashboard.orders }}</h2>
            <small>Tất cả trạng thái</small>
          </article>
          <article>
            <span>Sản phẩm</span>
            <h2>{{ dashboard.products }}</h2>
            <small>Bao gồm bản nháp</small>
          </article>
          <article>
            <span>Khách hàng</span>
            <h2>{{ dashboard.customers }}</h2>
            <small>Email lỗi: {{ dashboard.failedEmails }}</small>
          </article>
        </div>
        <section class="panel">
          <h2>Đơn hàng gần đây</h2>
          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Đơn</th>
                  <th>Khách hàng</th>
                  <th>Tổng</th>
                  <th>Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="o in dashboard.recentOrders">
                  <td class="mono">{{ o.id.slice(0, 8) }}</td>
                  <td>{{ o.email }}</td>
                  <td>{{ money(o.total) }}</td>
                  <td>
                    <span class="badge">{{ o.status }}</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <p v-if="!dashboard.recentOrders.length" class="muted">
            Chưa có đơn hàng.
          </p>
        </section>
        <div class="info">
          Bắt đầu bằng việc mở Sản phẩm → chỉnh sửa bản nháp → upload tài nguyên
          → xuất bản. Chỉ sản phẩm đã xuất bản mới xuất hiện trong thư viện công
          khai.
        </div></template
      >
      <template v-else-if="section === 'products'">
        <form v-if="editing" class="panel editor" @submit.prevent="saveProduct">
          <div class="section-heading">
            <h2>{{ form.id ? "Chỉnh sửa sản phẩm" : "Sản phẩm mới" }}</h2>
            <button type="button" class="ghost small" @click="editing = false">
              Đóng
            </button>
          </div>
          <div class="form-grid">
            <label
              >Tên sản phẩm<input
                v-model="form.name"
                required
                maxlength="160" /></label
            ><label
              >Slug<input
                v-model="form.slug"
                required
                pattern="[a-z0-9]+(?:-[a-z0-9]+)*"
                placeholder="cinematic-smoke" /></label
            ><label class="span-two"
              >Mô tả<textarea
                v-model="form.description"
                required
                maxlength="20000"
                rows="4"
              ></textarea></label
            ><label
              >Giá (VND)<input
                v-model="form.price"
                type="number"
                min="1"
                step="1"
                required /></label
            ><label
              >Danh mục<select v-model="form.categoryId" required>
                <option v-for="c in categories" :value="c.id">
                  {{ c.name }}
                </option>
              </select></label
            ><label
              >Chiều rộng<input
                v-model.number="form.width"
                type="number"
                min="1"
                max="16384"
                required /></label
            ><label
              >Chiều cao<input
                v-model.number="form.height"
                type="number"
                min="1"
                max="16384"
                required /></label
            ><label
              >FPS<input
                v-model="form.fps"
                type="number"
                min="1"
                max="999"
                step="0.001"
                required /></label
            ><label
              >Thời lượng (giây)<input
                v-model="form.durationSeconds"
                type="number"
                min="0.01"
                step="0.01"
                required /></label
            ><label
              >Định dạng<select v-model="form.format">
                <option v-for="v in ['MOV', 'MP4', 'WEBM', 'ZIP']">
                  {{ v }}
                </option>
              </select></label
            ><label
              >Trạng thái<select v-model="form.status">
                <option value="DRAFT">Bản nháp</option>
                <option value="PUBLISHED">Xuất bản</option>
                <option value="ARCHIVED">Ngừng bán</option>
              </select></label
            ><label class="checkbox"
              ><input v-model="form.alphaChannel" type="checkbox" />Có alpha
              channel</label
            ><label class="checkbox"
              ><input v-model="form.featured" type="checkbox" />Sản phẩm nổi
              bật</label
            ><label
              >Tags<select v-model="form.tagIds" multiple>
                <option v-for="t in tags" :value="t.id">{{ t.name }}</option>
              </select></label
            ><label
              >Bộ sưu tập<select v-model="form.collectionIds" multiple>
                <option v-for="c in collections" :value="c.id">
                  {{ c.name }}
                </option>
              </select></label
            >
          </div>
          <h3>Tài nguyên</h3>
          <div class="upload-grid">
            <label
              >01 / Thumbnail<small>JPG, PNG, WebP · tối đa 10MB</small
              ><input
                :disabled="busy"
                type="file"
                accept="image/jpeg,image/png,image/webp"
                @change="upload($event, 'THUMBNAIL', 'thumbnailId')"
              /><span :class="form.thumbnailId ? 'success-text' : 'muted'">{{
                form.thumbnailId ? "✓ Đã có ảnh" : "Chưa upload"
              }}</span></label
            ><label
              >02 / Video demo<small
                >MP4, WebM · tối đa 200MB · có watermark</small
              ><input
                :disabled="busy"
                type="file"
                accept="video/mp4,video/webm"
                @change="upload($event, 'PREVIEW', 'previewId')"
              /><span :class="form.previewId ? 'success-text' : 'muted'">{{
                form.previewId ? "✓ Đã có demo" : "Chưa upload"
              }}</span></label
            ><label
              >03 / File gốc<small
                >MOV, MP4, WebM, ZIP · tối đa 2GB · riêng tư</small
              ><input
                :disabled="busy"
                type="file"
                accept=".mov,.mp4,.webm,.zip"
                @change="upload($event, 'ORIGINAL', 'originalAssetId')"
              /><span
                :class="form.originalAssetId ? 'success-text' : 'muted'"
                >{{
                  form.originalAssetId ? "✓ Đã có file gốc" : "Chưa upload"
                }}</span
              ></label
            >
          </div>
          <p class="muted small-text">
            Upload xong cần nhấn Lưu để gắn file vào sản phẩm. Thay file gốc tạo
            phiên bản mới, giữ nguyên phiên bản đã bán.
          </p>
          <button class="button" :disabled="busy">
            {{ busy ? "Đang xử lý…" : "Lưu sản phẩm ↗" }}
          </button>
        </form>
        <template v-else
          ><form
            class="search-bar"
            @submit.prevent="
              page = 0;
              load();
            "
          >
            <input v-model="query" placeholder="Tìm sản phẩm" /><button
              class="ghost small"
            >
              Tìm kiếm
            </button>
          </form>
          <section class="panel">
            <div class="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>Sản phẩm</th>
                    <th>Danh mục</th>
                    <th>Giá</th>
                    <th>Trạng thái</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="p in rows">
                    <td>
                      <b>{{ p.name }}</b
                      ><small class="block muted"
                        >{{ p.width }} × {{ p.height }} · {{ p.format }}</small
                      >
                    </td>
                    <td>{{ p.category_name }}</td>
                    <td>{{ money(p.price) }}</td>
                    <td>
                      <span
                        class="badge"
                        :class="{ green: p.status === 'PUBLISHED' }"
                        >{{ p.status }}</span
                      >
                    </td>
                    <td>
                      <button class="ghost small" @click="edit(p)">
                        Chỉnh sửa ↗
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <p v-if="!rows.length" class="muted">Chưa có sản phẩm.</p>
          </section></template
        >
      </template>
      <template
        v-else-if="['categories', 'tags', 'collections'].includes(section)"
        ><form class="panel taxonomy-form" @submit.prevent="saveTaxonomy">
          <label
            >Tên<input
              v-model="taxonomy.name"
              required
              maxlength="100" /></label
          ><label v-if="section !== 'tags'"
            >Slug<input
              v-model="taxonomy.slug"
              required
              pattern="[a-z0-9]+(?:-[a-z0-9]+)*" /></label
          ><button class="button small" :disabled="busy">
            {{ taxonomy.id ? "Cập nhật" : "Thêm mới" }}</button
          ><button
            v-if="taxonomy.id"
            class="ghost small"
            type="button"
            @click="Object.assign(taxonomy, { id: null, name: '', slug: '' })"
          >
            Hủy
          </button>
        </form>
        <section class="panel">
          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Tên</th>
                  <th v-if="section !== 'tags'">Slug</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="r in rows">
                  <td>{{ r.name }}</td>
                  <td v-if="section !== 'tags'">{{ r.slug }}</td>
                  <td>
                    <button
                      class="ghost small"
                      @click="Object.assign(taxonomy, r)"
                    >
                      Sửa
                    </button>
                    <button
                      class="text-button danger"
                      @click="deleteTaxonomy(r)"
                    >
                      Xóa
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section></template
      >
      <template v-else-if="section === 'orders'"
        ><div class="info">
          API thanh toán chưa triển khai. Không thể đánh dấu đơn đã thanh toán
          hoặc cấp file từ màn hình này.
        </div>
        <section class="panel">
          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Mã đơn</th>
                  <th>Email</th>
                  <th>Tổng</th>
                  <th>Trạng thái</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="o in rows">
                  <td class="mono">{{ o.id.slice(0, 8) }}</td>
                  <td>{{ o.email }}</td>
                  <td>{{ money(o.total) }}</td>
                  <td>
                    <span class="badge">{{ o.status }}</span>
                  </td>
                  <td>
                    <button class="ghost small" @click="showOrder(o.id)">
                      Chi tiết
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <p v-if="!rows.length" class="muted">Chưa có đơn hàng.</p>
        </section>
        <section class="panel" v-if="selected">
          <h3>Đơn {{ selected.id }}</h3>
          <p>{{ selected.email }} · {{ selected.status }}</p>
          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Tài nguyên</th>
                  <th>Giá</th>
                  <th>Quyền tải</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="i in selected.items">
                  <td>{{ i.name }}</td>
                  <td>{{ money(i.price) }}</td>
                  <td>{{ i.entitlement_status || "Chưa được cấp" }}</td>
                  <td>
                    <button
                      v-if="i.entitlement_id"
                      class="ghost small"
                      @click="toggleEntitlement(i)"
                    >
                      {{
                        i.entitlement_status === "ACTIVE"
                          ? "Thu hồi"
                          : "Khôi phục"
                      }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <button
            class="ghost"
            :disabled="busy || selected.status !== 'PAID'"
            @click="resendOrder(selected.id)"
          >
            Gửi lại email giao file
          </button>
        </section></template
      >
      <template v-else-if="section === 'users'"
        ><section class="panel">
          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Khách hàng</th>
                  <th>Email</th>
                  <th>Vai trò</th>
                  <th>Xác thực</th>
                  <th>Trạng thái</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="u in rows">
                  <td>{{ u.name }}</td>
                  <td>{{ u.email }}</td>
                  <td>{{ u.role }}</td>
                  <td>{{ u.verified ? "Đã xác thực" : "Chưa" }}</td>
                  <td>{{ u.enabled ? "Hoạt động" : "Đã khóa" }}</td>
                  <td>
                    <button
                      v-if="u.role === 'CUSTOMER'"
                      class="ghost small"
                      :disabled="busy"
                      @click="toggleUser(u)"
                    >
                      {{ u.enabled ? "Khóa" : "Mở khóa" }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section></template
      >
      <template v-else-if="section === 'email-jobs'"
        ><section class="panel">
          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Email</th>
                  <th>Chủ đề</th>
                  <th>Trạng thái</th>
                  <th>Số lần thử</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="e in rows">
                  <td>{{ e.recipient }}</td>
                  <td>
                    {{ e.subject
                    }}<small class="block danger">{{ e.last_error }}</small>
                  </td>
                  <td>
                    <span class="badge">{{ e.status }}</span>
                  </td>
                  <td>{{ e.attempts }}</td>
                  <td>
                    <button class="ghost small" @click="attempts(e.id)">
                      Lịch sử
                    </button>
                    <button
                      v-if="['DEAD', 'RETRY_WAIT'].includes(e.status)"
                      class="ghost small"
                      :disabled="busy"
                      @click="retry(e.id)"
                    >
                      Thử lại
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <p v-if="!rows.length" class="muted">Chưa có email.</p>
        </section>
        <section class="panel" v-if="selected?.attempts">
          <h3>Lịch sử gửi</h3>
          <p v-for="a in selected.attempts">
            {{ new Date(a.created_at).toLocaleString("vi-VN") }} —
            {{ a.success ? "Đã gửi" : a.error_code }}
          </p>
        </section></template
      >
      <template v-else-if="section === 'audit-logs'"
        ><section class="panel">
          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Thời gian</th>
                  <th>Người thao tác</th>
                  <th>Hành động</th>
                  <th>Đối tượng</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="r in rows">
                  <td>{{ new Date(r.created_at).toLocaleString("vi-VN") }}</td>
                  <td>{{ r.actor }}</td>
                  <td>{{ r.action }}</td>
                  <td class="mono">{{ r.target }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <p v-if="!rows.length" class="muted">Chưa có nhật ký.</p>
        </section></template
      >
      <div
        class="pagination"
        v-if="
          !editing &&
          !['dashboard', 'categories', 'tags', 'collections'].includes(section)
        "
      >
        <button
          class="ghost small"
          :disabled="page === 0"
          @click="
            page--;
            load();
          "
        >
          ← Trước</button
        ><span>Trang {{ page + 1 }}</span
        ><button
          class="ghost small"
          :disabled="
            section === 'products' ? (page + 1) * 12 >= total : rows.length < 50
          "
          @click="
            page++;
            load();
          "
        >
          Sau →
        </button>
      </div>
    </section>
  </main>
</template>
