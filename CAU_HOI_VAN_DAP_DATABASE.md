# Câu hỏi vấn đáp: Kết nối Database

## Câu hỏi: "Kết nối database ở chỗ nào?"

### Cách trả lời:

**"Dự án em sử dụng Spring Boot, kết nối database được cấu hình ở 2 nơi:"**

---

## 1. **File cấu hình: `application.properties`**

**Vị trí:** `src/main/resources/application.properties`

**Nội dung:**
```properties
spring.datasource.url=jdbc:mysql://host:port/database
spring.datasource.username=username
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

**Giải thích:**
- Spring Boot tự động đọc file `application.properties` khi khởi động
- Tạo DataSource bean từ các thông tin này
- JPA/Hibernate sử dụng DataSource này để kết nối database

---

## 2. **Environment Variables (cho Production trên Render)**

**Vị trí:** Render Dashboard → Environment Variables

**Các biến:**
```
SPRING_DATASOURCE_URL=jdbc:mysql://...
SPRING_DATASOURCE_USERNAME=...
SPRING_DATASOURCE_PASSWORD=...
```

**Giải thích:**
- Trên môi trường production (Render), em không lưu password trong code
- Dùng Environment Variables để bảo mật
- Spring Boot ưu tiên đọc Environment Variables hơn file properties

---

## 3. **Cách Spring Boot kết nối:**

**"Spring Boot tự động tạo DataSource bean từ cấu hình:"**

1. **Đọc cấu hình** từ `application.properties` hoặc Environment Variables
2. **Tạo DataSource** với thông tin đã cấu hình
3. **JPA/Hibernate** sử dụng DataSource này để:
   - Kết nối database
   - Tạo/tự động cập nhật bảng (`ddl-auto=update`)
   - Thực hiện các truy vấn

---

## 4. **Database đang dùng:**

**"Em đang dùng MySQL trên Aiven Cloud:"**
- Host: `mysql-148f4d0d-thungo3011-4212.f.aivencloud.com`
- Port: `23095`
- Database: `ResourcesManagement`
- SSL: Bật (`ssl-mode=REQUIRED`)

---

## 5. **Các câu hỏi phụ có thể gặp:**

### Q: "Tại sao không hardcode password trong code?"
**A:** "Để bảo mật, em dùng Environment Variables trên Render. Password không được commit lên GitHub."

### Q: "JPA/Hibernate làm gì?"
**A:** "JPA/Hibernate là ORM framework, giúp:
- Map Java entities với database tables
- Tự động tạo/cập nhật schema (`ddl-auto=update`)
- Thực hiện CRUD operations qua Repository pattern"

### Q: "Làm sao test kết nối database?"
**A:** "Em test bằng cách:
- Chạy ứng dụng, nếu kết nối thành công sẽ không có lỗi
- Kiểm tra logs: `spring.jpa.show-sql=true` để xem các câu SQL
- Truy cập ứng dụng, nếu load được dữ liệu thì kết nối OK"

---

## 6. **Tóm tắt ngắn gọn (nếu cần trả lời nhanh):**

**"Kết nối database được cấu hình trong file `application.properties` ở thư mục `src/main/resources/`. Spring Boot tự động đọc file này khi khởi động và tạo DataSource để kết nối MySQL. Trên môi trường production, em dùng Environment Variables trên Render để bảo mật thông tin đăng nhập."**

