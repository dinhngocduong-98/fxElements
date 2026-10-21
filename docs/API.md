# REST API — /api/v1

Dùng JSON, cùng origin, session cookie. POST/PUT/DELETE cần header CSRF. Lấy token và headerName từ GET `/auth/csrf`; lấy lại sau login/logout. Lỗi có `message`; validation có `fields`.

## Công khai

| Method | Endpoint | Body/query |
|---|---|---|
| GET | /auth/csrf | token và headerName |
| POST | /auth/register | email, name, password (10–64 ký tự) |
| POST | /auth/login | email, password |
| POST | /auth/logout | không body |
| POST | /auth/forgot-password | email |
| POST | /auth/resend-verification | email |
| POST | /auth/verify-email | token |
| POST | /auth/reset-password | token, password |
| GET | /products | q, category_id, collection_id, height, fps, format, alpha_channel, minPrice, maxPrice, featured, sort, page, size |
| GET | /products/{slug} | chỉ PUBLISHED |
| GET | /categories, /tags, /collections | danh sách metadata |
| GET | /media/public/{id} | chỉ thumbnail/demo đã xuất bản |

Sort: newest, priceAsc, priceDesc; page bắt đầu 0, size tối đa 60. API không chấp nhận tên cột sort trực tiếp.

## Đăng nhập

| Method | Endpoint | Quy tắc |
|---|---|---|
| GET | /auth/me | hồ sơ đang đăng nhập |
| GET | /cart | giỏ của tài khoản |
| POST | /cart/items | {productId}; không mua lại quyền ACTIVE |
| DELETE | /cart/items/{productId} | xóa sản phẩm |
| POST | /orders | header Idempotency-Key 16–100 ký tự; body {expectedPrices:{"productId":"249000.00"}} |
| GET | /orders | lịch sử của tài khoản |
| GET | /orders/{id} | kiểm tra chủ sở hữu |
| GET | /me/purchases | quyền tải của đơn PAID |
| POST | /purchases/{entitlementId}/download-grants | trả link frontend, hạn 24 giờ |
| GET | /downloads/{token} | kiểm tra session + chủ sở hữu + token + quyền ACTIVE + đơn PAID |

Tạo đơn: yêu cầu xác thực email. Backend đọc lại giá và khóa dữ liệu, kiểm tra expectedPrices, snapshot license/file rồi xóa giỏ. Đơn PENDING_PAYMENT hết hạn sau 30 phút. Không có API chuyển PAID.

## ADMIN

| Method | Endpoint | Nội dung |
|---|---|---|
| GET | /admin/dashboard | thống kê và đơn gần đây |
| GET/POST | /admin/products | phân trang / thêm mới |
| GET | /admin/products/{slug} | chi tiết quản trị |
| PUT | /admin/products/{id} | cập nhật, phiên bản file, tag, collection |
| POST | /admin/media/uploads | multipart: kind=THUMBNAIL/PREVIEW/ORIGINAL và file |
| GET/POST | /admin/taxonomy/{kind} | kind=categories/tags/collections |
| PUT/DELETE | /admin/taxonomy/{kind}/{id} | sửa/xóa, từ chối xóa khi đang tham chiếu |
| GET | /admin/users | page; 50 dòng/trang |
| PUT | /admin/users/{id}/enabled | {enabled}; chỉ CUSTOMER, thu hồi session |
| GET | /admin/orders | page; 50 dòng/trang |
| GET | /admin/orders/{id} | chi tiết và quyền tải |
| POST | /admin/orders/{id}/resend-delivery | chỉ PAID có quyền tải; không cấp mới |
| PUT | /admin/entitlements/{id} | {status:ACTIVE/REVOKED}; ghi audit |
| GET | /admin/email-jobs | metadata, không trả body chứa token |
| GET | /admin/email-jobs/{id}/attempts | lịch sử gửi |
| POST | /admin/email-jobs/{id}/retry | chỉ job DEAD/RETRY_WAIT |
| GET | /admin/audit-logs | page; 50 dòng/trang |

Product body: name, slug, description, price, status, categoryId, width, height, fps, format, alphaChannel, durationSeconds, featured, thumbnailId, previewId, originalAssetId, tagIds[], collectionIds[]. Chỉ PUBLISHED khi có đủ file đúng loại. Gửi originalAssetId mới sẽ thêm phiên bản file gốc.

## Điểm tích hợp thanh toán tương lai — CHƯA TRIỂN KHAI

Không có endpoint `/payments`, webhook hay mock-payment trong code hiện tại. Phương thức trên UI chỉ là lựa chọn giao diện, không gửi thông tin thẻ hoặc tạo giao dịch.

Khi tích hợp: thêm payment_attempts/payment_events bằng Flyway mới, xác minh chữ ký trên raw body và đối soát số tiền/tiền tệ/tài khoản nhận. Trong một transaction: ghi nhận sự kiện duy nhất, khóa đơn, đánh dấu PAID, tạo entitlement duy nhất cho từng order_item, xếp email. Không nhận trạng thái PAID từ frontend hoặc redirect. Không viết update SQL thủ công để vận hành luồng mua thật.

Quyền tải và gửi email hiện được chuẩn bị để nhận kết quả tin cậy từ bước tích hợp đó. Callback trùng và sai thứ tự phải có kiểm thử riêng khi thêm payment module.
