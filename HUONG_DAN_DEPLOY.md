# Hướng dẫn Deploy lên Render - Từng bước

## Bước 1: Push code lên GitHub (Cách đơn giản nhất)

### 1.1. Tạo repository trên GitHub

1. Vào https://github.com
2. Đăng nhập/Đăng ký (miễn phí)
3. Click nút **"+"** (góc trên bên phải) → **"New repository"**
4. Đặt tên: `resources-management` (hoặc tên bạn muốn)
5. Chọn **Public** (hoặc Private nếu muốn)
6. **KHÔNG** tích "Initialize with README"
7. Click **"Create repository"**

### 1.2. Push code lên GitHub

**Cách A: Dùng IntelliJ (Dễ nhất)**

1. Trong IntelliJ, click chuột phải vào project → **Git** → **Add**
2. Click chuột phải vào project → **Git** → **Commit Directory**
   - Nhập commit message: "Initial commit"
   - Click **Commit**
3. Click chuột phải vào project → **Git** → **Repository** → **Remotes...**
   - Click **+** để thêm remote
   - Name: `origin`
   - URL: `https://github.com/your-username/resources-management.git` (thay your-username)
4. Click chuột phải → **Git** → **Repository** → **Push...**
   - Chọn remote: `origin`
   - Click **Push**

**Cách B: Dùng Command Line**

Mở Terminal trong IntelliJ và chạy:

```bash
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/your-username/resources-management.git
git push -u origin main
```

## Bước 2: Deploy trên Render

### 2.1. Tạo Web Service

1. Vào https://dashboard.render.com
2. Đăng nhập/Đăng ký (dùng GitHub account)
3. Click **"New +"** → **"Web Service"**

### 2.2. Kết nối Repository

1. Chọn tab **"Git Provider"**
2. Click **"Connect GitHub"** (nếu chưa connect)
3. Chọn repository: `resources-management`
4. Click **"Connect"**

### 2.3. Cấu hình Build

Render sẽ tự động detect:
- **Build Command:** `mvn clean install -DskipTests`
- **Start Command:** `java -jar target/ResourcesManagement-0.0.1-SNAPSHOT.jar`

**Hoặc nếu dùng Dockerfile:**
- Render sẽ tự động build từ Dockerfile
- Start Command: (để trống, đã có trong Dockerfile)

### 2.4. Cấu hình Environment Variables

Scroll xuống phần **"Environment Variables"**, thêm:

```
SPRING_DATASOURCE_URL=jdbc:mysql://mysql-148f4d0d-thungo3011-4212.f.aivencloud.com:23095/ResourcesManagement?ssl-mode=REQUIRED
SPRING_DATASOURCE_USERNAME=avnadmin
SPRING_DATASOURCE_PASSWORD=AVNS_mWVafZw-Dwx7aPpvAml
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SERVER_PORT=10000
```

### 2.5. Chọn Plan

- Chọn **Free** (đủ dùng cho demo)
- Hoặc **Starter** nếu muốn không bị sleep

### 2.6. Deploy

1. Click **"Create Web Service"**
2. Đợi build xong (5-10 phút)
3. Lấy URL: `https://your-app-name.onrender.com`

## Bước 3: Test

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

### Lỗi: Database connection failed
- Kiểm tra Database URL đúng chưa
- Kiểm tra username/password
- Kiểm tra firewall của Aiven

### Lỗi: Port already in use
- Render tự động set port, không cần lo
- Dùng `SERVER_PORT=10000` hoặc để trống


