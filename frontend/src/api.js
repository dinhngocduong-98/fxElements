import axios from "axios";
export const api = axios.create({
  baseURL: "/api/v1",
  withCredentials: true,
  timeout: 30000,
});
let csrf = null;
export async function refreshCsrf() {
  const { data } = await api.get("/auth/csrf");
  csrf = data;
}
api.interceptors.request.use(async (config) => {
  if (!["get", "head", "options"].includes(config.method)) {
    if (!csrf) await refreshCsrf();
    config.headers[csrf.headerName] = csrf.token;
  }
  return config;
});
export const errorMessage = (e) =>
  e.response?.data?.message || "Không kết nối được máy chủ. Vui lòng thử lại.";
