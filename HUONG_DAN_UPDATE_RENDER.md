# Hướng dẫn Update Code trên Render

## Cách 1: Push code lên GitHub (Tự động - Khuyến nghị)

### Bước 1: Commit code
```powershell
git add .
git commit -m "Update: Sửa thông báo, giao diện QR, bỏ @Transactional rollbackFor"
```

### Bước 2: Push lên GitHub
```powershell
git push
```

### Bước 3: Render tự động build
- Render sẽ tự động phát hiện commit mới
- Tự động build lại
- Deploy version mới

**Thời gian:** 5-10 phút

---

## Cách 2: Manual Deploy trên Render Dashboard

### Nếu Auto-Deploy bị tắt:

1. **Vào Render Dashboard:** https://dashboard.render.com
2. **Chọn service** của bạn
3. **Vào tab "Manual Deploy"**
4. **Chọn "Deploy latest commit"**
5. **Click "Deploy"**
6. **Đợi build xong**

---

## Kiểm tra Update có thành công:

1. **Xem Logs trên Render:**
   - Vào tab "Logs"
   - Xem quá trình build
   - Kiểm tra có lỗi không

2. **Test ứng dụng:**
   - Truy cập URL: `https://your-app.onrender.com`
   - Test các chức năng đã sửa
   - Kiểm tra thông báo mới

---

## Lưu ý:

⚠️ **Nếu build bị lỗi:**
- Xem logs để biết lỗi gì
- Sửa code
- Commit và push lại

✅ **Nếu build thành công:**
- Ứng dụng sẽ tự động restart
- Version mới sẽ được deploy
- Có thể mất 30-60 giây để ứng dụng sẵn sàng

---

## Tóm tắt nhanh:

```powershell
git add .
git commit -m "Mô tả thay đổi"
git push
```

→ Render tự động build và deploy!

