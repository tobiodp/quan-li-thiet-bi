# Hướng dẫn Deploy lên Render bằng Docker

## Cách 1: Build Docker Image và Push lên Docker Hub (Khuyến nghị)

### Bước 1: Tạo Dockerfile (đã tạo sẵn)

### Bước 2: Build Docker Image

1. **Mở Terminal** trong thư mục dự án

2. **Build image:**
   ```bash
   docker build -t resources-management:latest .
   ```

3. **Tag image cho Docker Hub:**
   ```bash
   docker tag resources-management:latest your-dockerhub-username/resources-management:latest
   ```

4. **Login vào Docker Hub:**
   ```bash
   docker login
   ```
   (Nhập username và password Docker Hub)

5. **Push image lên Docker Hub:**
   ```bash
   docker push your-dockerhub-username/resources-management:latest
   ```

### Bước 3: Deploy trên Render

1. **Vào Render Dashboard** → "New Web Service"

2. **Chọn tab "Existing Image"**

3. **Nhập Image URL:**
   ```
   docker.io/your-dockerhub-username/resources-management:latest
   ```

4. **Cấu hình:**
   - **Name:** `resources-management`
   - **Start Command:** (để trống, đã có trong Dockerfile)

5. **Thêm Environment Variables:**
   ```
   SPRING_DATASOURCE_URL=jdbc:mysql://your-database-host:port/ResourcesManagement?ssl-mode=REQUIRED
   SPRING_DATASOURCE_USERNAME=your_username
   SPRING_DATASOURCE_PASSWORD=your_password
   SPRING_JPA_HIBERNATE_DDL_AUTO=update
   SERVER_PORT=10000
   ```

6. **Deploy**

---

## Cách 2: Build Image trực tiếp trên Render (Nếu có Git)

Nếu bạn có thể push code lên GitHub (dù chỉ 1 lần):

1. **Push code lên GitHub** (chỉ cần 1 lần)

2. **Trên Render:**
   - Chọn "Git Provider" → Connect GitHub
   - Chọn repository
   - Render sẽ tự động build Docker image từ Dockerfile

3. **Cấu hình Environment Variables** như trên

---

## Cách 3: Dùng Render CLI (Nếu có)

```bash
render deploy
```

---

## Lưu ý

- **Docker Hub:** Cần tạo tài khoản miễn phí tại https://hub.docker.com
- **Port:** Render tự động map port, nhưng set `SERVER_PORT=10000` để chắc chắn
- **Database:** Có thể tiếp tục dùng Aiven MySQL hiện tại

