<script setup>
import { ref, reactive, watch, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { api, errorMessage } from "../api";
import ProductCard from "../components/ProductCard.vue";
const route = useRoute(),
  router = useRouter();
const categories = ref([]),
  collections = ref([]),
  items = ref([]),
  total = ref(0),
  loading = ref(false),
  error = ref(""),
  page = ref(0);
const f = reactive({
  q: "",
  category_id: "",
  collection_id: "",
  height: "",
  fps: "",
  format: "",
  alpha_channel: "",
  minPrice: "",
  maxPrice: "",
  sort: "newest",
  featured: "",
});
let sequence = 0;
async function load() {
  const request = ++sequence;
  loading.value = true;
  error.value = "";
  try {
    const params = { ...f, page: page.value, size: 12 };
    Object.keys(params).forEach((k) => params[k] === "" && delete params[k]);
    const { data } = await api.get("/products", { params });
    if (request === sequence) {
      items.value = data.items;
      total.value = data.total;
    }
  } catch (e) {
    if (request === sequence) error.value = errorMessage(e);
  } finally {
    if (request === sequence) loading.value = false;
  }
}
function search() {
  page.value = 0;
  load();
}
function reset() {
  Object.assign(f, {
    q: "",
    category_id: "",
    collection_id: "",
    height: "",
    fps: "",
    format: "",
    alpha_channel: "",
    minPrice: "",
    maxPrice: "",
    sort: "newest",
    featured: "",
  });
  search();
}
function category(id) {
  f.category_id = id;
  search();
}
watch(
  () => route.fullPath,
  () => {
    f.featured = route.query.featured || "";
    search();
  },
);
onMounted(async () => {
  f.featured = route.query.featured || "";
  try {
    const [c, s] = await Promise.all([
      api.get("/categories"),
      api.get("/collections"),
    ]);
    categories.value = c.data;
    collections.value = s.data;
  } catch (e) {
    error.value = errorMessage(e);
  }
  await load();
});
</script>
<template>
  <main>
    <section v-if="route.path === '/'" class="hero container">
      <div class="hero-copy">
        <span class="eyebrow"
          ><i class="tiny-dot"></i> BUILT FOR YOUR NEXT BIG IDEA</span
        >
        <h1>Mọi khung hình.<br />Một <em>dấu ấn.</em></h1>
        <p>
          Thư viện hiệu ứng video dành cho nhà sáng tạo.<br />Khám phá, xem thử
          và tìm chất liệu cho câu chuyện của bạn.
        </p>
        <RouterLink class="button" to="/library"
          >Khám phá thư viện <span>↗</span></RouterLink
        >
        <div class="hero-notes">
          <span>◈ Chất lượng gốc</span><span>▶ Xem demo trước khi mua</span
          ><span>↗ Giao file qua email</span>
        </div>
      </div>
      <div class="hero-art" aria-hidden="true">
        <div class="art-ring ring-one"></div>
        <div class="art-ring ring-two"></div>
        <div class="art-glow"></div>
        <div class="art-word">CREATE<br />BEYOND.</div>
        <span class="art-caption">FRAMEVAULT / CREATIVE RESOURCES</span
        ><span class="art-corner">01 — ∞</span>
      </div>
    </section>
    <section class="library container">
      <div class="section-heading">
        <div>
          <span class="eyebrow accent">THE ASSET LIBRARY</span>
          <h1 v-if="route.path !== '/'">Thư viện hiệu ứng</h1>
          <h2 v-else>Chất liệu cho ý tưởng tiếp theo.</h2>
        </div>
        <span class="muted">{{ total }} tài nguyên</span>
      </div>
      <form class="search-bar" @submit.prevent="search">
        <span aria-hidden="true">⌕</span
        ><input
          v-model="f.q"
          placeholder="Tìm lửa, khói, ánh sáng, chuyển cảnh..."
          aria-label="Tìm tài nguyên"
        /><button class="button small">Tìm kiếm ↗</button>
      </form>
      <div class="category-tabs">
        <button :class="{ active: !f.category_id }" @click="category('')">
          Tất cả hiệu ứng</button
        ><button
          v-for="c in categories"
          :key="c.id"
          :class="{ active: f.category_id === c.id }"
          @click="category(c.id)"
        >
          {{ c.name }}
        </button>
      </div>
      <div class="library-layout">
        <aside class="filters">
          <div class="filter-title">
            BỘ LỌC <button class="text-button" @click="reset">Xóa</button>
          </div>
          <form @submit.prevent="search">
            <label
              >Bộ sưu tập<select v-model="f.collection_id">
                <option value="">Tất cả</option>
                <option v-for="c in collections" :value="c.id">
                  {{ c.name }}
                </option>
              </select></label
            >
            <label
              >Độ phân giải<select v-model="f.height">
                <option value="">Tất cả</option>
                <option value="2160">4K · 2160p</option>
                <option value="1080">Full HD · 1080p</option>
                <option value="720">HD · 720p</option>
              </select></label
            >
            <label
              >Tốc độ khung hình<select v-model="f.fps">
                <option value="">Tất cả</option>
                <option v-for="n in [24, 25, 30, 50, 60, 120]" :value="n">
                  {{ n }} FPS
                </option>
              </select></label
            >
            <label
              >Định dạng<select v-model="f.format">
                <option value="">Tất cả</option>
                <option v-for="v in ['MOV', 'MP4', 'WEBM', 'ZIP']">
                  {{ v }}
                </option>
              </select></label
            >
            <label
              >Alpha channel<select v-model="f.alpha_channel">
                <option value="">Tất cả</option>
                <option value="true">Có nền trong suốt</option>
                <option value="false">Không có</option>
              </select></label
            >
            <label
              >Giá từ (₫)<input
                v-model="f.minPrice"
                type="number"
                min="0"
                step="1000"
                placeholder="0" /></label
            ><label
              >Giá đến (₫)<input
                v-model="f.maxPrice"
                type="number"
                min="0"
                step="1000"
                placeholder="Không giới hạn" /></label
            ><button class="button full small">Áp dụng bộ lọc</button>
          </form>
          <div class="filter-note">
            PREVIEW ≠ ORIGINAL
            <p>
              Video xem thử có watermark hoặc chất lượng thấp. File gốc được bảo
              vệ riêng.
            </p>
          </div>
        </aside>
        <div class="catalog-results">
          <div class="results-toolbar">
            <span>{{
              f.featured ? "Sản phẩm nổi bật" : "Khám phá tài nguyên"
            }}</span
            ><label class="inline-label"
              >Sắp xếp<select v-model="f.sort" @change="search">
                <option value="newest">Mới nhất</option>
                <option value="priceAsc">Giá tăng dần</option>
                <option value="priceDesc">Giá giảm dần</option>
              </select></label
            >
          </div>
          <div v-if="error" class="alert" role="alert">
            {{ error }} <button class="ghost" @click="load">Thử lại</button>
          </div>
          <div v-if="loading" class="empty">Đang tải thư viện…</div>
          <div v-else-if="!items.length && !error" class="empty">
            <div class="empty-icon">◈</div>
            <h3>Chưa có tài nguyên phù hợp</h3>
            <p>Sản phẩm sẽ xuất hiện sau khi quản trị viên xuất bản.</p>
            <button class="ghost" @click="reset">Xóa bộ lọc</button>
          </div>
          <div v-else class="product-grid">
            <ProductCard v-for="p in items" :key="p.id" :product="p" />
          </div>
          <div class="pagination" v-if="total > 12">
            <button
              class="ghost"
              :disabled="page === 0"
              @click="
                page--;
                load();
              "
            >
              ← Trước</button
            ><span>{{ page + 1 }} / {{ Math.ceil(total / 12) }}</span
            ><button
              class="ghost"
              :disabled="(page + 1) * 12 >= total"
              @click="
                page++;
                load();
              "
            >
              Sau →
            </button>
          </div>
        </div>
      </div>
    </section>
    <section class="container promise">
      <span class="eyebrow accent">MAKE SOMETHING UNEXPECTED</span>
      <h2>Ý tưởng của bạn.<br />Không giới hạn khung hình.</h2>
      <p>
        Xem trước tài nguyên, chọn hiệu ứng phù hợp và lưu lại cho dự án tiếp
        theo.
      </p>
    </section>
  </main>
</template>
