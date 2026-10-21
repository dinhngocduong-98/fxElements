# FrameVault — Video Asset Store

Website thư viện và bán tài nguyên video, gồm Vue 3 + Axios và backend Java 11 / Spring Boot 2.7.18 / Spring Security / MySQL 8.

Bản cập nhật 2026-10-21: Controller → UseCase interface → Interactor → Repository interface → SQL Adapter. Interactor dùng ValidatorWrapper; adapter chứa SQL tham số hóa bằng Spring JDBC. Xem `docs/ARCHITECTURE.md`, `CHANGES.md` và `VERIFICATION.md`. Giữ nguyên schema/API; không cần xóa database để nâng cấp.

## Phạm vi phiên bản này

- Thư viện: tìm kiếm, lọc, phân trang, chi tiết, preview công khai.
- Tài khoản: đăng ký, đăng nhập, đăng xuất, xác thực email, reset mật khẩu.
- Session trong MySQL; cookie HttpOnly; CSRF; BCrypt; thu hồi phiên khi reset/khóa tài khoản.
- Giỏ khách trên trình duyệt, giỏ tài khoản trên server; hợp nhất sau đăng nhập.
- Tạo đơn chờ thanh toán, tính giá server, snapshot giấy phép và phiên bản file, idempotency.
- **Thanh toán chỉ có giao diện. Không có API khởi tạo giao dịch, webhook, mock-payment hoặc API đánh dấu đơn đã thanh toán.**
- Chuẩn bị sẵn quyền tải, link tải 24 giờ, kiểm tra chủ sở hữu và thu hồi; chỉ hoạt động với đơn PAID và quyền tải đã được cấp bởi một tích hợp tin cậy trong tương lai.
- Email xác thực/reset có hàng đợi MySQL, SMTP, retry và lịch sử. Gửi lại email giao hàng chỉ cho đơn PAID có quyền tải; không tự cấp quyền.
- Admin: dashboard, CRUD danh mục/tag/bộ sưu tập, sản phẩm, upload, khách hàng, xem đơn, email, audit.
- MySQL migrations bằng Flyway; Docker Compose; Mailpit để xem email phát triển.

Không có chức năng thu tiền hoặc cách mở khóa file gốc cho đơn chưa thanh toán. Không có mã thanh toán giả bị giấu dưới nút UI.

## Chạy nhanh với Docker Desktop

Yêu cầu Docker Desktop đang chạy và Docker Compose v2. Chạy tại thư mục chứa `compose.yml`:

```sh
cp .env.example .env
```

Mở `.env`, thay `MYSQL_ROOT_PASSWORD`, `DB_PASSWORD`, `ADMIN_EMAIL`, `ADMIN_PASSWORD` bằng giá trị của bạn. Mật khẩu admin tối thiểu 12 ký tự. Không commit `.env`.

```sh
docker compose up --build -d
docker compose logs -f backend
```

Lần đầu Maven/npm cần tải dependency. Chờ log backend thông báo `Started Application`.

- Website: http://localhost:8088
- Admin: http://localhost:8088/admin — đăng nhập bằng biến môi trường đã đặt.
- Email phát triển: http://localhost:8025 — Mailpit chỉ được bind vào localhost.

MySQL không mở cổng ra ngoài Docker. Database `framevault` được image MySQL tạo; Flyway tự tạo bảng và dữ liệu danh mục/bản nháp. Không cần import SQL thủ công.

Admin bootstrap chỉ tạo khi email chưa tồn tại. Đổi biến ADMIN_PASSWORD không đổi mật khẩu tài khoản đã tồn tại; dùng luồng reset. Muốn đổi email admin phải quản lý có chủ đích, không dùng bootstrap để ghi đè tài khoản khách đã tồn tại.

```sh
docker compose down
```

Lệnh trên giữ dữ liệu trong volumes. Không thêm `-v` nếu muốn giữ database và file.

## Đưa sản phẩm đầu tiên lên thư viện

1. Đăng nhập admin → Sản phẩm → mở một bản nháp hoặc thêm mới.
2. Nhập giá VND, thông số kỹ thuật, mô tả, slug duy nhất.
3. Upload thumbnail JPG/PNG/WebP (10MB), demo MP4/WebM (200MB), file gốc MOV/MP4/WebM/ZIP (2GB).
4. Demo phải được bạn đóng watermark hoặc giảm chất lượng trước khi upload. Ứng dụng chưa tự chuyển mã.
5. Chọn Xuất bản → Lưu sản phẩm. Backend từ chối xuất bản nếu thiếu file.
6. Mở thư viện công khai. Có thể xem demo và thêm vào giỏ.

Dữ liệu mẫu không kèm video có bản quyền và không mở bán giả. Thư viện công khai ban đầu trống là hành vi có chủ đích.

Thumbnail và preview chỉ được phục vụ nếu gắn với một sản phẩm PUBLISHED. File ORIGINAL không bao giờ được phục vụ ở API media công khai. Các MOV cần container có header `ftyp`; với định dạng MOV khác, đóng gói ZIP hoặc chuyển container trước khi upload.

Thay file gốc khi chỉnh sửa tạo phiên bản mới. Đơn hàng cũ giữ phiên bản đã mua. Ngừng bán bằng ARCHIVED thay vì xóa sản phẩm hoặc file đã bán.

## Tài khoản và email

Mailpit nhận email trong môi trường Docker mặc định; không gửi tới hộp thư Internet. Mở Mailpit để lấy link xác thực/reset.

Production: cấu hình SMTP thật qua MAIL_HOST/PORT/USER/PASSWORD/AUTH/TLS/FROM và PUBLIC_URL. Backend dùng Spring Mail, không nhúng khóa dịch vụ vào frontend.

Session hết hạn sau 30 phút không hoạt động, tối đa 12 giờ. Cookie Secure phải bật khi triển khai HTTPS. Khi xác thực email xong, trang tài khoản sẽ cập nhật trạng thái; có thể tải lại trang nếu đang mở tab khác.

Email nhận hàng được snapshot từ tài khoản khi tạo đơn. Không lấy email giao hàng tùy ý từ trình duyệt.

## Chạy không dùng Docker

Cài JDK 11, Maven 3.8+ và MySQL 8; Node 22 cho frontend.

Tạo database trống theo `database/00_create_database.sql`, cấp một MySQL user quyền trên database đó. Cấu hình biến DB_URL, DB_USER, DB_PASSWORD, MAIL_HOST, MAIL_PORT, PUBLIC_URL, STORAGE_ROOT, ADMIN_EMAIL, ADMIN_PASSWORD.

```sh
cd backend
mvn test
mvn spring-boot:run
```

Mở terminal khác:

```sh
cd frontend
npm ci
npm run dev
```

Mở http://localhost:5173. Vite proxy `/api` sang port 8080 nên không cần mở CORS wildcard. PUBLIC_URL cần đặt `http://localhost:5173` trong chế độ này.

## Kiểm thử và build

```sh
cd backend
mvn test
mvn package
```

```sh
cd frontend
npm ci
npm run test
npm run build
```

Backend integration tests dùng H2 ở MySQL compatibility mode, chạy cùng migration Flyway. Test kiểm tra truy cập admin, CSRF, giá đơn, idempotency, phân quyền tải, token email, query DTO và vòng đời email job. ArchitectureTest kiểm tra phân tầng, còn ValidationBoundaryTest kiểm tra ValidatorWrapper khi gọi trực tiếp Interactor. Dữ liệu PAID chỉ được chèn trong test, không phải endpoint của ứng dụng.

H2 không thay thế kiểm thử với MySQL thật. Xem `VERIFICATION.md` để biết kiểm tra đã chạy trong lần bàn giao.

## Kiến trúc

Backend Spring MVC REST theo Controller → input interface → Interactor → Repository interface → SQL Adapter. Input interface nằm trong `usecase`; implementation trong `interactor`. SQL và JDBC chỉ nằm trong `adapter/sql`; domain model và port không có JPA. DTO/Mapper tiếp tục tách khỏi model. Interactor dùng ValidatorWrapper để validate cả khi được gọi trực tiếp, không phụ thuộc Controller.

Adapter `storage/LocalStorage` triển khai StorageService. Object storage S3 chỉ có điểm mở rộng interface; chưa tích hợp adapter, presigned URL hoặc CDN. Không mount thư mục storage vào nginx.

Spring Session JDBC và email outbox cùng dùng MySQL. Worker claim email trong transaction bằng SELECT FOR UPDATE và lease 10 phút; SMTP có timeout, gửi ngoài transaction. Gửi email có thể trùng khi dịch vụ nhận nhưng ứng dụng mất kết nối trước lúc lưu kết quả. Body chứa token được xóa sau khi SMTP chấp nhận; giới hạn truy cập DB và backup.

Giá VND cố định trong bản triển khai này; BigDecimal/DECIMAL lưu tiền, DTO catalog/đơn khách trả chuỗi. Muốn đổi tiền tệ cần cập nhật cả backend và frontend trước khi có đơn thực tế.

Rate limit hiện lưu trong bộ nhớ một backend; reset khi restart. Nếu scale nhiều instance cần shared limiter tại gateway/Redis. Không tin X-Forwarded-For từ client để vượt giới hạn IP.

## Trước khi vận hành thật

- Tích hợp thanh toán theo API.md; hiện không thể hoàn tất giao dịch mua.
- Thay giấy phép DRAFT-1 bằng nội dung chính thức trước khi xuất bản bán hàng.
- Cấu hình HTTPS, COOKIE_SECURE=true, SMTP thật và secret management.
- Chốt chiến lược bản vá cho stack Java 11 / Boot 2.7 theo yêu cầu dự án.
- Kiểm thử MySQL 8, SMTP thật, file lớn, tải tiếp và backup/restore ở hạ tầng đích.
- Giữ bản sao MySQL và volume media cùng thời điểm nhất quán; chưa có job backup tự động.
- Upload hiện kiểm tra signature/loại/kích thước, chưa có antivirus hoặc tự xác thực nội dung toàn bộ video. Chỉ cấp admin cho người quản trị tin cậy.
- Link hết hạn ngăn request mới; không thể thu hồi byte đã tải hoặc một response đang truyền.

Không có secret thật trong source hoặc dữ liệu mẫu.
