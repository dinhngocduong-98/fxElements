import { defineStore } from "pinia";
import { api, refreshCsrf } from "./api";
function readGuest() {
  try {
    return JSON.parse(localStorage.getItem("framevault-cart") || "[]").filter(
      (x) => Number.isInteger(x.id),
    );
  } catch {
    return [];
  }
}
export const useSession = defineStore("session", {
  state: () => ({ user: null, ready: false }),
  getters: { admin: (s) => s.user?.role === "ADMIN" },
  actions: {
    async init() {
      try {
        this.user = (await api.get("/auth/me")).data;
      } catch (e) {
        if (e.response?.status !== 401) throw e;
      } finally {
        this.ready = true;
      }
    },
    async login(form) {
      this.user = (await api.post("/auth/login", form)).data;
      await refreshCsrf();
    },
    async logout() {
      await api.post("/auth/logout");
      this.user = null;
      await refreshCsrf();
    },
  },
});
export const useCart = defineStore("cart", {
  state: () => ({ items: readGuest() }),
  getters: { total: (s) => s.items.reduce((n, p) => n + Number(p.price), 0) },
  actions: {
    async load() {
      const session = useSession();
      if (session.user) this.items = (await api.get("/cart")).data;
      else this.items = readGuest();
    },
    async add(p) {
      if (useSession().user) {
        this.items = (await api.post("/cart/items", { productId: p.id })).data;
      } else {
        if (!this.items.some((x) => x.id === p.id)) this.items.push(p);
        localStorage.setItem("framevault-cart", JSON.stringify(this.items));
      }
    },
    async remove(id) {
      if (useSession().user)
        this.items = (await api.delete("/cart/items/" + id)).data;
      else {
        this.items = this.items.filter((x) => x.id !== id);
        localStorage.setItem("framevault-cart", JSON.stringify(this.items));
      }
    },
    async merge() {
      let failed = [];
      for (const p of readGuest()) {
        try {
          await api.post("/cart/items", { productId: p.id });
        } catch {
          failed.push(p.name);
        }
      }
      localStorage.removeItem("framevault-cart");
      await this.load();
      return failed;
    },
  },
});
