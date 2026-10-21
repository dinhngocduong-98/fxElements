import { createRouter, createWebHistory } from "vue-router";
import { useSession } from "./stores";
import Catalog from "./pages/Catalog.vue";
import Product from "./pages/Product.vue";
import Auth from "./pages/Auth.vue";
import Cart from "./pages/Cart.vue";
import Checkout from "./pages/Checkout.vue";
import Account from "./pages/Account.vue";
import Admin from "./pages/Admin.vue";
import Download from "./pages/Download.vue";
const routes = [
  { path: "/", component: Catalog },
  { path: "/library", component: Catalog },
  { path: "/products/:slug", component: Product },
  ...[
    "login",
    "register",
    "forgot-password",
    "reset-password",
    "verify-email",
  ].map((x) => ({ path: "/" + x, component: Auth })),
  { path: "/cart", component: Cart },
  { path: "/checkout", component: Checkout, meta: { auth: true } },
  { path: "/account/:section?", component: Account, meta: { auth: true } },
  { path: "/download", component: Download, meta: { auth: true } },
  {
    path: "/admin/:section?",
    component: Admin,
    meta: { auth: true, admin: true },
  },
  {
    path: "/:pathMatch(.*)*",
    component: {
      template:
        '<main class="container empty"><h1>Không tìm thấy trang</h1><a href="/">Về trang chủ</a></main>',
    },
  },
];
const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
});
router.beforeEach(async (to) => {
  const s = useSession();
  if (!s.ready) {
    try {
      await s.init();
    } catch {
      /* Pages report API connection errors. */
    }
  }
  if (to.meta.auth && !s.user)
    return { path: "/login", query: { next: to.fullPath } };
  if (to.meta.admin && !s.admin) return "/";
});
export default router;
