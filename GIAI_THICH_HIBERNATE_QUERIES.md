# Giải thích các Hibernate Queries

## Các query đang chạy:

### 1. **Đếm requests theo status:**
```sql
select count(re1_0.request_id) from requests re1_0 where re1_0.status=?
```
**Mục đích:** Đếm số yêu cầu đang chờ duyệt (cho badge trên sidebar)

### 2. **Thống kê thiết bị được mượn nhiều nhất:**
```sql
select dhe1_0.device_id, d1_0.device_name, count(dhe1_0.id) 
from device_history dhe1_0 
join devices d1_0 on d1_0.device_id=dhe1_0.device_id 
where dhe1_0.action='Mượn' 
group by dhe1_0.device_id, d1_0.device_name 
order by count(dhe1_0.id) desc
```
**Mục đích:** Lấy top thiết bị được mượn nhiều nhất (cho biểu đồ dashboard)

### 3. **Lấy tất cả devices:**
```sql
select de1_0.device_id, de1_0.device_name, de1_0.device_type, 
       de1_0.status, de1_0.note, de1_0.is_checked 
from devices de1_0
```
**Mục đích:** Hiển thị danh sách thiết bị

### 4. **Lấy device chi tiết với relationships:**
```sql
select de1_0.device_id, au1_0.id, c1_0.id, ...
from devices de1_0 
left join users au1_0 on au1_0.id=de1_0.user_id 
left join chapters c1_0 on c1_0.id=au1_0.chapter_id 
left join checklists c2_0 on c2_0.checklist_id=de1_0.checklist_id 
where de1_0.device_id=?
```
**Mục đích:** Lấy thông tin device kèm user đang mượn, chapter, checklist (cho trang chi tiết)

### 5. **Lấy lịch sử thiết bị:**
```sql
select dhe1_0.id, dhe1_0.action, dhe1_0.action_date, ...
from device_history dhe1_0 
left join devices d1_0 on d1_0.device_id=dhe1_0.device_id 
where d1_0.device_id=? 
order by dhe1_0.action_date desc
```
**Mục đích:** Lấy lịch sử mượn/trả của thiết bị

### 6. **Lấy user với chapter:**
```sql
select ue1_0.id, c1_0.id, c1_0.name, ue1_0.username, ...
from users ue1_0 
left join chapters c1_0 on c1_0.id=ue1_0.chapter_id 
where ue1_0.id=?
```
**Mục đích:** Lấy thông tin user kèm phòng ban

---

## Cách trả lời khi bị hỏi:

**"Các query này là do Hibernate tự động sinh ra từ JPA Repository. Em dùng `spring.jpa.show-sql=true` trong application.properties để xem các câu SQL để debug và kiểm tra performance."**

---

## Tối ưu (nếu cần):

### Nếu query chậm:
1. **Thêm index vào database:**
   - Index trên `status` trong bảng `requests`
   - Index trên `device_id`, `action` trong bảng `device_history`

2. **Dùng @Query với native query:**
   - Tối ưu query phức tạp hơn

3. **Dùng pagination:**
   - Không load tất cả records một lúc

---

## Lưu ý:

✅ **Bình thường:** Các query này là bình thường, không có vấn đề

⚠️ **Nếu chậm:** Có thể do:
- Database lớn
- Thiếu index
- Query N+1 problem (load quá nhiều relationships)


