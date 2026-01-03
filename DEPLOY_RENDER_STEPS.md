# Hướng dẫn Deploy trên Render - Từng bước

## Bước 1: Tạo Project (Tùy chọn)

1. Click **"+ Create a project"** (nếu muốn tạo project mới)
   - Hoặc bỏ qua và deploy trực tiếp

## Bước 2: Chọn loại Service

1. Click **"New +"** → **"Web Service"** (không phải Docker nếu bạn muốn Render tự build)

## Bước 3: Kết nối Repository

1. Chọn tab **"Git Provider"**
2. Chọn repository: **quan-li-thiet-bi**
3. Click **"Connect"**

## Bước 4: Cấu hình Build

Render sẽ tự động detect:
- **Build Command:** `mvn clean install -DskipTests`
- **Start Command:** `java -jar target/ResourcesManagement-0.0.1-SNAPSHOT.jar`

**Kiểm tra:**
- **Name:** `quan-li-thiet-bi` (hoặc tên bạn muốn)
- **Region:** Singapore (Southeast Asia) - OK
- **Branch:** `main` - OK
- **Root Directory:** (để trống)

## Bước 5: Cấu hình Environment Variables

Scroll xuống phần **"Environment Variables"**, click **"Add Environment Variable"** và thêm từng biến:

1. **SPRING_DATASOURCE_URL**
   - Value: `jdbc:mysql://mysql-148f4d0d-thungo3011-4212.f.aivencloud.com:23095/ResourcesManagement?ssl-mode=REQUIRED`

2. **SPRING_DATASOURCE_USERNAME**
   - Value: `avnadmin`

3. **SPRING_DATASOURCE_PASSWORD**
   - Value: `AVNS_mWVafZw-Dwx7aPpvAml`

4. **SPRING_JPA_HIBERNATE_DDL_AUTO**
   - Value: `update`

5. **SERVER_PORT**
   - Value: `10000`

## Bước 6: Chọn Plan

- Chọn **Free** (đủ dùng cho demo)
- Hoặc **Starter** nếu muốn không bị sleep

## Bước 7: Deploy

1. Click **"Create Web Service"**
2. Đợi build xong (5-10 phút)
   - Xem logs để theo dõi quá trình build
3. Lấy URL: `https://quan-li-thiet-bi.onrender.com` (hoặc tên bạn đặt)

## Bước 8: Test

1. Truy cập URL: `https://your-app-name.onrender.com/viewLogin`
2. Đăng nhập
3. Test các chức năng
4. Test QR Code → Sẽ tự động dùng domain thực

## Lưu ý

- **Free tier:** Có thể sleep sau 15 phút, lần đầu wake up mất 30-60 giây
- **QR Code:** Tự động dùng domain thực, không cần cấu hình thêm
- **Database:** Đang dùng Aiven MySQL, có thể tiếp tục dùng

## Nếu gặp lỗi

### Lỗi: Build failed
- Kiểm tra Java version (Render hỗ trợ Java 17, 21)
- Xem build logs trên Render
- Kiểm tra `pom.xml` có đúng không

### Lỗi: Database connection failed
- Kiểm tra Database URL đúng chưa
- Kiểm tra username/password
- Kiểm tra firewall của Aiven (có thể cần whitelist IP của Render)

### Lỗi: Port already in use
- Render tự động set port, không cần lo
- Dùng `SERVER_PORT=10000` hoặc để trống

