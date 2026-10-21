-- Chỉ cần khi chạy MySQL bên ngoài Docker Compose.
-- Docker Compose tự tạo database qua MYSQL_DATABASE.
CREATE DATABASE IF NOT EXISTS framevault CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- Tạo user và cấp quyền qua công cụ quản trị bằng mật khẩu riêng.
-- Không import migration thủ công: backend Flyway áp dụng V1, V2 khi khởi động.
