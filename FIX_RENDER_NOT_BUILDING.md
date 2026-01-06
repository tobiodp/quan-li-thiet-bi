# Fix: Render không tự động build sau khi push code

## Nguyên nhân có thể:

1. **Auto-Deploy bị tắt**
2. **Render chưa phát hiện commit mới**
3. **Branch không đúng**
4. **Webhook GitHub bị lỗi**

---

## Cách fix:

### Cách 1: Kiểm tra Auto-Deploy

1. **Vào Render Dashboard:** https://dashboard.render.com
2. **Chọn service** của bạn
3. **Vào tab "Settings"**
4. **Tìm phần "Auto-Deploy"**
5. **Đảm bảo đã BẬT (ON)**

### Cách 2: Manual Deploy

1. **Vào Render Dashboard**
2. **Chọn service** của bạn
3. **Vào tab "Manual Deploy"**
4. **Chọn "Deploy latest commit"**
5. **Click "Deploy"**
6. **Đợi build xong**

### Cách 3: Kiểm tra Branch

1. **Vào Settings**
2. **Kiểm tra "Branch"** phải là `main` (hoặc branch bạn đang push)
3. **Nếu sai, đổi lại branch đúng**

### Cách 4: Reconnect GitHub

1. **Vào Settings**
2. **Scroll xuống phần "Git Provider"**
3. **Click "Disconnect"** (nếu có)
4. **Click "Connect GitHub"** lại
5. **Chọn repository** của bạn
6. **Connect lại**

---

## Kiểm tra nhanh:

### Trên Render Dashboard:
- **Tab "Events":** Xem có event "Deploy" mới không
- **Tab "Logs":** Xem có log build không
- **Status:** Phải là "Live" hoặc "Deploying"

### Trên GitHub:
- **Kiểm tra commit đã push chưa:** Vào repository → Commits
- **Kiểm tra branch:** Phải là `main` (hoặc branch Render đang theo dõi)

---

## Nếu vẫn không được:

1. **Kiểm tra GitHub Webhook:**
   - Vào GitHub repository → Settings → Webhooks
   - Xem có webhook của Render không
   - Nếu không có, Render sẽ không nhận được thông báo

2. **Thử push lại:**
   ```powershell
   git commit --allow-empty -m "Trigger deploy"
   git push
   ```

3. **Liên hệ Render Support** (nếu cần)

---

## Tóm tắt nhanh:

**Cách nhanh nhất:** Vào Render Dashboard → Manual Deploy → Deploy latest commit


