# Giải thích @Transactional cho vấn đáp

## @Transactional là gì?

**@Transactional** đảm bảo tất cả các thao tác database trong method được thực hiện trong 1 transaction:
- Nếu tất cả thành công → Commit (lưu vào database)
- Nếu có lỗi → Rollback (hủy tất cả thay đổi)

## rollbackFor = Exception.class là gì?

- Mặc định: Spring chỉ rollback khi có **RuntimeException** hoặc **Error**
- `rollbackFor = Exception.class`: Rollback cho **MỌI exception** (kể cả checked exception)

## Có thể xóa được không?

**CÓ**, có 2 cách:

### Cách 1: Xóa `rollbackFor = Exception.class` (Khuyến nghị)
- Giữ lại `@Transactional` đơn giản
- Vẫn đảm bảo transaction
- Dễ giải thích hơn

### Cách 2: Xóa hẳn `@Transactional`
- Code vẫn chạy được
- Nhưng mất tính atomicity (mỗi save() là 1 transaction riêng)
- Nếu có lỗi giữa chừng, một số thay đổi đã được commit

## Cách trả lời khi bị hỏi:

**"@Transactional đảm bảo tất cả thao tác database trong method được thực hiện trong 1 transaction. Nếu có lỗi, tất cả thay đổi sẽ được rollback để đảm bảo tính nhất quán dữ liệu."**

Nếu hỏi về `rollbackFor`:
**"rollbackFor = Exception.class nghĩa là rollback cho mọi exception, không chỉ RuntimeException."**

