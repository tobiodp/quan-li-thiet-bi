# Hướng dẫn Rollback trên Render

## Câu hỏi: "Deploy lên Render rồi, có thể rollback về code cũ (giống local) được không?"

**Trả lời: CÓ, có nhiều cách!**

---

## Cách 1: Rollback về commit cũ trên Render (Dễ nhất)

### Bước 1: Xem danh sách commits
1. Vào **Render Dashboard** → Chọn service của bạn
2. Vào tab **"Manual Deploy"**
3. Xem danh sách các commits đã deploy

### Bước 2: Chọn commit cũ để deploy lại
1. Click vào commit cũ mà bạn muốn rollback về
2. Click **"Deploy this commit"**
3. Render sẽ build và deploy lại commit đó

**Ví dụ:**
- Commit mới nhất: `37e3661` (có Dockerfile mới)
- Commit cũ: `49cc740` (code local của bạn)
- → Chọn `49cc740` và deploy lại

---

## Cách 2: Tạo branch riêng cho Production

### Trên GitHub:
1. Tạo branch mới từ commit ổn định:
   ```bash
   git checkout -b production
   git push origin production
   ```

2. Trên Render:
   - Vào Settings → Change branch
   - Chọn branch `production` thay vì `main`
   - Render sẽ chỉ deploy từ branch `production`

**Lợi ích:**
- Branch `main`: Code mới, test
- Branch `production`: Code ổn định, đã test kỹ

---

## Cách 3: Dùng Git Tags

### Tạo tag cho version ổn định:
```bash
git tag -a v1.0.0 -m "Version ổn định"
git push origin v1.0.0
```

### Trên Render:
- Vào Settings → Change branch
- Chọn tag `v1.0.0` thay vì branch

---

## Cách 4: Revert commit trên GitHub

### Nếu muốn xóa commit mới:
```bash
# Xem lịch sử
git log --oneline

# Revert commit mới nhất
git revert HEAD

# Hoặc revert commit cụ thể
git revert <commit-hash>

# Push lên GitHub
git push
```

Render sẽ tự động detect và deploy lại.

---

## Cách 5: Tắt Auto-Deploy tạm thời

1. Vào Render Dashboard → Settings
2. Tắt **"Auto-Deploy"**
3. Chỉ deploy thủ công khi bạn muốn

---

## So sánh các cách:

| Cách | Độ khó | Tốc độ | Khuyến nghị |
|------|--------|--------|-------------|
| Manual Deploy commit cũ | ⭐ Dễ | ⚡ Nhanh | ✅ Tốt nhất |
| Tạo branch production | ⭐⭐ Trung bình | ⚡⚡ Trung bình | ✅✅ Tốt cho dự án lớn |
| Dùng Git Tags | ⭐⭐ Trung bình | ⚡⚡ Trung bình | ✅ Tốt cho versioning |
| Revert commit | ⭐⭐⭐ Khó hơn | ⚡⚡⚡ Chậm hơn | ⚠️ Chỉ khi cần xóa code |

---

## Ví dụ cụ thể:

### Tình huống:
- Code local: Chạy tốt, commit `49cc740`
- Deploy lên Render: Commit `37e3661` (có Dockerfile), bị lỗi
- Muốn rollback về `49cc740`

### Cách làm:
1. Vào Render Dashboard
2. Tab **"Manual Deploy"**
3. Tìm commit `49cc740`
4. Click **"Deploy this commit"**
5. Đợi build xong → Xong!

---

## Lưu ý:

⚠️ **Rollback sẽ:**
- Xóa code mới
- Quay về code cũ
- Có thể mất dữ liệu nếu có migration database

✅ **Nên làm:**
- Test kỹ trên local trước khi deploy
- Tạo branch `production` riêng
- Backup database trước khi rollback

---

## Tóm tắt:

**"Có thể rollback trên Render bằng cách vào Manual Deploy, chọn commit cũ và deploy lại. Hoặc tạo branch production riêng để quản lý code ổn định."**

