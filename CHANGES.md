# Bản cập nhật input port / interactor / output port / adapter — 2026-10-21

- Controller chỉ inject UseCase interface (interface1).
- Interactor implement UseCase và validate DTO bằng ValidatorWrapper trước nghiệp vụ/truy cập repository.
- Repository là interface thuần; 20 SQL adapter implement các interface đó.
- SQL tham số hóa được viết trong adapter/sql; bỏ JPA/JPQL/Specification và Hibernate ORM.
- Dùng starter-jdbc; transaction vẫn ở Interactor. Persist rõ ràng thay cho dirty checking.
- Domain model không có annotation persistence; DTO response giữ JSON cũ qua JsonProperty.
- Giữ Vue/Axios, API, schema và checksum Flyway. Thanh toán vẫn chỉ có UI.
- Khôi phục đầy đủ StorageService/LocalStorage bị bỏ sót bởi quy tắc đóng gói cũ; gói cuối được giải nén và chạy kiểm thử lại.

Xem docs/ARCHITECTURE.md để theo dõi một luồng cụ thể qua từng tầng.
