# Kết quả kiểm tra kiến trúc Interactor / SQL Adapter

Ngày cập nhật: 2026-10-21.

| Kiểm tra | Kết quả |
|---|---|
| Backend API integration, H2 MySQL mode | 9/9 PASS |
| Ranh giới kiến trúc | 3/3 PASS |
| Gọi trực tiếp Interactor và ValidatorWrapper | 4/4 PASS |
| Tổng backend | 16/16 PASS, không bỏ qua test |
| Frontend Vitest | 3/3 PASS |
| Frontend production build | PASS |
| Flyway V1/V2 | Không thay đổi so với bản trước |
| Biên dịch | Java release 11; JVM chạy test là Java 17 |
| MySQL 8, Docker, JVM 11 thực tế | Chưa chạy trong môi trường này |
| SMTP thật, file lớn, browser tương tác | Chưa kiểm tra |
| Thanh toán | Chỉ giao diện; chưa có API thanh toán |

Backend kiểm tra session, CSRF, quyền admin, upload, xuất bản, bộ lọc, giá đơn hàng, idempotency, chủ sở hữu đơn, token email, thu hồi quyền tải và cơ chế claim/lease email job. Email thất bại không đổi trạng thái thanh toán.

Kiểm thử kiến trúc xác nhận Controller gọi input interface, Interactor implement interface và dùng ValidatorWrapper, repository là interface thuần, SQL/JDBC nằm trong adapter/sql. Kiểm thử gọi trực tiếp Interactor xác nhận input sai bị từ chối trước khi gọi repository.

Gói ZIP được giải nén sang thư mục mới và chạy lại toàn bộ 16 kiểm thử backend; bao gồm hai file nguồn storage từng bị bỏ sót bởi quy tắc đóng gói cũ.

Chạy lại:

```sh
cd backend
mvn test
```

```sh
cd frontend
npm ci
npm run test
npm run build
```

Trong môi trường kiểm tra, Maven dùng dependency cache/mirror cục bộ. Các helper kiểm tra không được đóng gói vào source.
