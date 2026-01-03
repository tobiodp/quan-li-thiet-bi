# Hướng dẫn Deploy trực tiếp lên Render (không cần GitHub)

## Bước 1: Build JAR file

### Trên máy tính của bạn:

1. **Mở Terminal/Command Prompt** trong thư mục dự án

2. **Build JAR file:**
   ```bash
   mvn clean package -DskipTests
   ```

3. **Tìm file JAR:**
   - File sẽ nằm ở: `target/ResourcesManagement-0.0.1-SNAPSHOT.jar`
   - Ghi nhớ đường dẫn này

## Bước 2: Tạo Web Service trên Render

1. **Vào Render Dashboard:** https://dashboard.render.com

2. **Click "New +" → "Web Service"**

3. **Chọn "Deploy an existing image or binary"** (hoặc tương tự)

4. **Cấu hình:**
   - **Name:** `resources-management` (hoặc tên bạn muốn)
   - **Environment:** `Java`
   - **Start Command:** `java -jar ResourcesManagement-0.0.1-SNAPSHOT.jar`

## Bước 3: Upload JAR file

### Cách 1: Upload qua Render Dashboard
1. Trong phần "Build & Deploy"
2. Click "Upload" hoặc "Browse"
3. Chọn file `ResourcesManagement-0.0.1-SNAPSHOT.jar`
4. Đợi upload xong

### Cách 2: Dùng Render CLI (nếu có)
```bash
render deploy ResourcesManagement-0.0.1-SNAPSHOT.jar
```

## Bước 4: Cấu hình Environment Variables

Trong Render Dashboard, thêm các biến môi trường:

```
SPRING_DATASOURCE_URL=jdbc:mysql://your-database-host:port/ResourcesManagement?ssl-mode=REQUIRED
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SERVER_PORT=10000
APP_BASE_URL=https://your-app-name.onrender.com
```

**Lưu ý:** Thay `your-app-name.onrender.com` bằng URL thực tế của bạn sau khi deploy.

## Bước 5: Deploy

1. **Click "Deploy"** hoặc "Save"
2. **Đợi deploy xong** (thường 5-10 phút)
3. **Lấy URL:** Render sẽ cung cấp URL dạng `https://your-app-name.onrender.com`

## Bước 6: Cập nhật APP_BASE_URL

Sau khi có URL thực tế:
1. Vào Environment Variables
2. Cập nhật `APP_BASE_URL` = URL thực tế của bạn
3. Restart service

## Lưu ý quan trọng

### 1. Mỗi lần sửa code:
- Phải build lại JAR: `mvn clean package -DskipTests`
- Upload JAR mới lên Render
- Restart service

### 2. Database:
- Bạn đang dùng Aiven MySQL - có thể tiếp tục dùng
- Hoặc tạo PostgreSQL mới trên Render

### 3. Free Tier:
- Có thể sleep sau 15 phút không hoạt động
- Lần đầu truy cập sau khi sleep sẽ mất 30-60 giây

### 4. QR Code:
- Sau khi deploy, cập nhật `APP_BASE_URL` = URL thực tế
- QR Code sẽ dùng domain thực: `https://your-app.onrender.com/device/qr/1`

## Troubleshooting

### Lỗi: Cannot find JAR file
- Kiểm tra tên file JAR đúng chưa
- Kiểm tra Start Command có đúng tên file không

### Lỗi: Port already in use
- Render tự động set port, không cần lo
- Dùng `SERVER_PORT=10000` hoặc để trống

### Lỗi: Database connection failed
- Kiểm tra Database URL đúng chưa
- Kiểm tra username/password
- Kiểm tra firewall/whitelist IP của Aiven

