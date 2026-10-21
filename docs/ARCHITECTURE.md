# Controller → Input interface → Interactor → Repository interface → SQL Adapter

Backend dùng Spring MVC REST với Vue 3 + Axios. Cấu trúc mới đúng theo luồng:

`AuthController → AuthUseCase → AuthInteractor → UserRepository → UserSqlAdapter → MySQL`

`AuthInteractor implements AuthUseCase`; `UserSqlAdapter implements UserRepository`.

## Các tầng

| Package | Vai trò |
|---|---|
| `controller` | Bind HTTP/JSON/query, lấy principal, gọi input interface, trả response DTO và HTTP headers |
| `usecase` | Các input interface nghiệp vụ, tương ứng “interface1” |
| `interactor` | Implement input interface, validate bằng ValidatorWrapper, xử lý nghiệp vụ, transaction và gọi Repository interface |
| `validation` | ValidatorWrapper, lỗi validation có danh sách field |
| `repository` | Output interface; không có SQL, JPA, JDBC hoặc Spring annotation |
| `repository.query` | Điều kiện tìm kiếm độc lập với request DTO |
| `repository.page` | Phân trang/sắp xếp bằng Java thuần |
| `adapter.sql` | Implement Repository, chứa SQL tham số hóa, mapping row/model, khóa ghi |
| `domain.model` | Model Java thuần, không có annotation JPA |
| `dto.request`, `dto.response` | Dữ liệu API; không trả model trực tiếp |
| `mapper` | Chuyển model sang response DTO |
| `security`, `scheduler`, `storage`, `config` | Hạ tầng phiên, lịch chạy, file và cấu hình |

## Ví dụ trong source

- `controller/AuthController.java`: inject `AuthUseCase`.
- `usecase/AuthUseCase.java`: khai báo đăng ký/đăng nhập/xác thực.
- `interactor/AuthInteractor.java`: gọi `validator.validate(request)` trước xử lý và truy cập repository.
- `repository/UserRepository.java`: khai báo `findByEmail`, `lockById`, `save` qua port.
- `adapter/sql/UserSqlAdapter.java`: SQL SELECT/INSERT/UPDATE/DELETE và thực thi bằng NamedParameterJdbcTemplate.

Tên class dùng **Interactor** theo thuật ngữ thông dụng. Không còn lớp `ServiceImpl` hoặc Repository tự triển khai bằng Spring Data JPA.

## Validation

Controller không dùng `@Valid` để làm ranh giới validation nghiệp vụ. Interactor inject `ValidatorWrapper` và tự validate DTO, vì input interface có thể được gọi từ HTTP, scheduler hoặc một caller khác.

ValidatorWrapper bọc javax.validation.Validator, đọc các constraint NotBlank/Email/Size/Pattern/Positive…, và ném InputValidationException. ApiError chuyển lỗi thành HTTP 400 với `message` và `fields`.

Kiểm tra trạng thái nghiệp vụ như sản phẩm đang bán, tài khoản sở hữu, đơn đã thanh toán, giá thay đổi và loại file vẫn nằm trong Interactor. Chuyển đổi kiểu dữ liệu HTTP/JSON không hợp lệ có thể bị Spring MVC từ chối trước khi gọi Interactor; đây là lỗi transport.

## SQL và transaction

Toàn bộ SQL truy cập dữ liệu nghiệp vụ nằm trong `adapter/sql`. Mỗi adapter có các hằng INSERT_SQL, UPDATE_SQL, SELECT_SQL…, cùng SQL của các truy vấn đặc thù. AbstractSqlAdapter chia sẻ cách bind tham số, đọc row, lấy generated key và phân trang; không đặt logic nghiệp vụ trong adapter.

Giá trị từ request được bind vào named parameter. Cột sort được kiểm tra với danh sách property cố định của adapter, không đưa chuỗi SQL từ client vào query.

Interactor sở hữu `@Transactional`. Spring JDBC dùng cùng datasource/transaction nên đơn hàng, snapshot item, token và audit rollback cùng nhau khi có lỗi. Không còn Hibernate dirty checking: các thay đổi tài khoản, quyền tải và email job gọi `repository.save` rõ ràng.

Các port khóa ghi được adapter thực hiện bằng SELECT … FOR UPDATE. Email worker claim dưới khóa, gửi SMTP ngoài transaction rồi ghi kết quả với kiểm tra lease. Link tải vẫn kiểm tra chủ sở hữu và quyền ACTIVE cho đơn PAID.

File system không tham gia transaction MySQL: upload đăng ký cleanup khi rollback. StorageService/LocalStorage được giữ đầy đủ trong source, ngoài web root.

## Tương thích

- Giữ nguyên toàn bộ source Vue/Axios và JSON contract.
- Giữ nguyên hai migration Flyway V1/V2; không cần xóa database để đổi source.
- Dependency backend chuyển từ starter-data-jpa sang starter-jdbc.
- Spring Session JDBC tiếp tục quản lý hai bảng session của framework.
- Thanh toán vẫn chỉ có UI, không có payment API/webhook/mock-payment.

## Kiểm thử kiến trúc

ArchitectureTest xác nhận Controller chỉ phụ thuộc input interface; Interactor implements input interface và có ValidatorWrapper; 20 SQL adapter implements Repository; Repository là port thuần, SQL/JDBC chỉ xuất hiện trong adapter. ValidationBoundaryTest gọi Interactor trực tiếp, bỏ qua Controller, và kiểm tra input lỗi không chạm Repository.
