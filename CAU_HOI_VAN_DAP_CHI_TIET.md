# Câu hỏi vấn đáp chi tiết - Hàm và Database

## PHẦN 1: CÂU HỎI VỀ CÁC HÀM

---

### Q1: "Hàm `createUser()` trong `UserService` làm gì?"

**Trả lời:**
"Hàm `createUser()` dùng để tạo user mới:
1. Kiểm tra role, nếu chưa có thì mặc định là 'USER'
2. Mã hóa mật khẩu bằng BCryptPasswordEncoder
3. Lưu user vào database qua `userRepository.save()`

**Cập nhật bảng:** `users`"

---

### Q2: "Hàm `deleteUser()` làm gì? Cập nhật những bảng nào?"

**Trả lời:**
"Hàm `deleteUser()` xóa user và xử lý tất cả dữ liệu liên quan:
1. Xóa tất cả notifications của user → Bảng `notifications`
2. Gỡ user khỏi devices (set `assignedUser = null`) → Bảng `devices`
3. Xóa tất cả requests của user → Bảng `requests`
4. Set `approvingUser = null` cho requests mà user đã duyệt → Bảng `requests`
5. Xóa device history liên quan → Bảng `device_history`
6. Set `handler = null` cho history mà user là người xử lý → Bảng `device_history`
7. Cuối cùng mới xóa user → Bảng `users`

**Cập nhật bảng:** `notifications`, `devices`, `requests`, `device_history`, `users`"

---

### Q3: "Hàm `loadUserByUsername()` làm gì?"

**Trả lời:**
"Hàm này implement từ `UserDetailsService` của Spring Security:
1. Tìm user trong database theo username
2. Tạo đối tượng `UserDetails` từ `UserEntity`
3. Chuyển role thành `GrantedAuthority` (ví dụ: 'ADMIN' → 'ROLE_ADMIN')
4. Spring Security dùng thông tin này để xác thực và phân quyền

**Cập nhật bảng:** Không cập nhật, chỉ đọc từ bảng `users`"

---

### Q4: "Hàm `addDevice()` trong `DeviceService` làm gì?"

**Trả lời:**
"Hàm `addDevice()` thêm thiết bị mới:
1. Xử lý status: Mặc định là 'Sẵn sàng' khi thêm mới
2. Tạo `DevicesEntity` từ DTO
3. Set `isChecked = false`
4. Lưu vào database

**Cập nhật bảng:** `devices`"

---

### Q5: "Hàm `deleteDevice()` cập nhật những bảng nào?"

**Trả lời:**
"Hàm `deleteDevice()` xóa thiết bị và xử lý dữ liệu liên quan:
1. Xóa tất cả device history → Bảng `device_history`
2. Set `device = null` cho các requests liên quan → Bảng `requests`
3. Gỡ người dùng đang mượn (set `assignedUser = null`) → Bảng `devices`
4. Xóa thiết bị → Bảng `devices`

**Cập nhật bảng:** `device_history`, `requests`, `devices`"

---

### Q6: "Hàm `approveAndAssignDevice()` làm gì?"

**Trả lời:**
"Hàm này duyệt yêu cầu mượn thiết bị và gán thiết bị cho user:
1. Tìm request và device
2. Xử lý checklist (chuyển List → String)
3. Tạo device history với action 'Mượn' → Bảng `device_history`
4. Cập nhật device: status = 'Đang sử dụng', assignedUser = user → Bảng `devices`
5. Cập nhật request: status = 'Đang mượn', lưu device → Bảng `requests`
6. Gửi thông báo cho user → Bảng `notifications`

**Cập nhật bảng:** `device_history`, `devices`, `requests`, `notifications`"

---

### Q7: "Hàm `returnDevice()` làm gì? Logic xử lý trạng thái như thế nào?"

**Trả lời:**
"Hàm `returnDevice()` xử lý trả thiết bị:
1. Tìm request và device
2. Xử lý checklist
3. Cập nhật request: status = 'Đã trả' → Bảng `requests`
4. **Logic cập nhật device status dựa trên condition:**
   - Nếu 'Tốt' → status = 'Sẵn sàng'
   - Nếu 'Hư hỏng' → status = 'Bảo trì'
   - Nếu 'Mất mát' → status = 'Mất mát'
5. Set `assignedUser = null` → Bảng `devices`
6. Tạo device history với action 'Trả' → Bảng `device_history`
7. Gửi thông báo → Bảng `notifications`

**Cập nhật bảng:** `requests`, `devices`, `device_history`, `notifications`"

---

### Q8: "Hàm `getDeviceCountByStatus()` làm gì?"

**Trả lời:**
"Hàm này đếm số lượng thiết bị theo từng trạng thái:
1. Lấy tất cả devices từ database
2. Lọc bỏ thiết bị 'Đã xóa'
3. Nhóm theo status và đếm
4. Trả về Map: {'Sẵn sàng': 10, 'Đang sử dụng': 5, 'Bảo trì': 2}

**Dùng cho:** Pie chart trên dashboard

**Cập nhật bảng:** Không, chỉ đọc từ bảng `devices`"

---

### Q9: "Hàm `sendNotificationToAllUsers()` làm gì?"

**Trả lời:**
"Hàm này gửi thông báo cho tất cả users:
1. Lấy danh sách tất cả users
2. Với mỗi user, tạo một notification
3. Lưu vào database

**Cập nhật bảng:** `notifications` (tạo nhiều records, mỗi user một record)"

---

### Q10: "Hàm `markAsRead()` làm gì?"

**Trả lời:**
"Hàm này đánh dấu thông báo là đã đọc:
1. Tìm notification theo ID và user ID
2. Set `isRead = true`
3. Lưu lại

**Cập nhật bảng:** `notifications` (cập nhật field `is_read`)"

---

## PHẦN 2: CÂU HỎI VỀ DATABASE SCHEMA

---

### Q11: "Muốn thêm trường 'address' cho User thì cập nhật bảng nào?"

**Trả lời:**
"Em cần:
1. **Cập nhật Entity:** Thêm field `address` vào `UserEntity.java`
2. **Cập nhật bảng:** `users` (JPA sẽ tự động thêm cột khi `ddl-auto=update`)
3. **Cập nhật DTO:** Thêm `address` vào `UserResponseDTO` (nếu có)
4. **Cập nhật Service:** Thêm logic xử lý `address` trong `createUser()`, `updateUser()`
5. **Cập nhật Form:** Thêm input field trong HTML template"

---

### Q12: "Muốn thêm trường 'purchaseDate' (ngày mua) cho Device thì làm sao?"

**Trả lời:**
"Em cần:
1. **Cập nhật Entity:** Thêm `@Column private LocalDate purchaseDate;` vào `DevicesEntity.java`
2. **Cập nhật bảng:** `devices` (JPA tự động thêm cột)
3. **Cập nhật DTO:** Thêm `purchaseDate` vào `CreateDeviceRequestDTO` và `DeviceResponseDTO`
4. **Cập nhật Service:** Xử lý `purchaseDate` trong `addDevice()`, `updateDevice()`
5. **Cập nhật Form:** Thêm date picker trong HTML"

---

### Q13: "Muốn thêm trường 'reason' (lý do từ chối) cho Request thì cập nhật bảng nào?"

**Trả lời:**
"Em cần:
1. **Cập nhật Entity:** Thêm `private String reason;` vào `RequestEntity.java`
2. **Cập nhật bảng:** `requests` (JPA tự động thêm cột)
3. **Cập nhật Service:** Thêm logic lưu `reason` trong `rejectRequestManual()`
4. **Cập nhật Form:** Thêm textarea cho lý do từ chối"

---

### Q14: "Muốn thêm bảng mới 'Categories' (danh mục thiết bị) thì làm sao?"

**Trả lời:**
"Em cần:
1. **Tạo Entity:** `CategoryEntity.java` với các field: `id`, `name`, `description`
2. **Tạo Repository:** `CategoryRepository.java` extends `JpaRepository<CategoryEntity, Long>`
3. **Tạo Service:** `CategoryService.java` với các method CRUD
4. **Tạo Controller:** `CategoryController.java` để xử lý HTTP requests
5. **Cập nhật `DevicesEntity`:** Thêm `@ManyToOne` relationship với `CategoryEntity`
6. **Cập nhật bảng:** JPA sẽ tự động tạo bảng `categories` và cập nhật bảng `devices` (thêm cột `category_id`)"

---

## PHẦN 3: CÂU HỎI VỀ RELATIONSHIPS

---

### Q15: "Mối quan hệ giữa User và Device là gì?"

**Trả lời:**
"Là quan hệ **Many-to-One**:
- Nhiều devices có thể được gán cho một user
- Trong `DevicesEntity`: `@ManyToOne UserEntity assignedUser`
- Trong database: Bảng `devices` có cột `user_id` (foreign key)

**Ví dụ:** User A có thể mượn nhiều thiết bị (laptop, monitor, keyboard)"

---

### Q16: "Mối quan hệ giữa User và Request là gì?"

**Trả lời:**
"Có 2 mối quan hệ:
1. **RequestingUser (Many-to-One):** Nhiều requests được tạo bởi một user
   - Trong `RequestEntity`: `@ManyToOne UserEntity requestingUser`
   - Database: Cột `request_user_id` trong bảng `requests`

2. **ApprovingUser (Many-to-One):** Nhiều requests được duyệt bởi một user (admin)
   - Trong `RequestEntity`: `@ManyToOne UserEntity approvingUser`
   - Database: Cột `approve_user_id` trong bảng `requests`"

---

### Q17: "Mối quan hệ giữa Device và DeviceHistory là gì?"

**Trả lời:**
"Là quan hệ **One-to-Many**:
- Một device có nhiều lịch sử (history)
- Trong `DeviceHistoryEntity`: `@ManyToOne DevicesEntity device`
- Database: Bảng `device_history` có cột `device_id` (foreign key)

**Ví dụ:** Một laptop có thể có nhiều lịch sử: Mượn, Trả, Bảo trì, ..."

---

### Q18: "Mối quan hệ giữa User và Chapter là gì?"

**Trả lời:**
"Là quan hệ **Many-to-One**:
- Nhiều users thuộc về một chapter (phòng ban)
- Trong `UserEntity`: `@ManyToOne ChapterEntity chapter`
- Database: Bảng `users` có cột `chapter_id` (foreign key)

**Ví dụ:** Nhiều nhân viên thuộc phòng IT"

---

## PHẦN 4: CÂU HỎI VỀ TÍNH NĂNG MỚI

---

### Q19: "Muốn thêm tính năng 'Đánh giá thiết bị' (rating) sau khi trả thì làm sao?"

**Trả lời:**
"Em cần:
1. **Cập nhật Entity:** Thêm field `rating` (Integer 1-5) vào `DeviceHistoryEntity.java`
2. **Cập nhật bảng:** `device_history` (thêm cột `rating`)
3. **Cập nhật Service:** Thêm parameter `rating` vào `returnDevice()` method
4. **Cập nhật Form:** Thêm star rating component trong form trả thiết bị
5. **Cập nhật View:** Hiển thị rating trong lịch sử thiết bị"

---

### Q20: "Muốn thêm tính năng 'Gia hạn mượn thiết bị' thì cập nhật bảng nào?"

**Trả lời:**
"Em cần:
1. **Cập nhật Entity:** Thêm field `extendCount` (số lần gia hạn) và `extendDate` vào `RequestEntity.java`
2. **Cập nhật bảng:** `requests` (thêm 2 cột: `extend_count`, `extend_date`)
3. **Tạo Service method:** `extendRequest(Long requestId)` để xử lý logic gia hạn
4. **Cập nhật Controller:** Thêm endpoint `/requests/{id}/extend`
5. **Cập nhật View:** Thêm nút 'Gia hạn' trong danh sách requests"

---

## PHẦN 5: TÓM TẮT CÁC BẢNG VÀ MỐI QUAN HỆ

### Các bảng chính:
1. **users** - Thông tin người dùng
2. **devices** - Thông tin thiết bị
3. **requests** - Yêu cầu mượn thiết bị
4. **device_history** - Lịch sử thiết bị
5. **notifications** - Thông báo
6. **chapters** - Phòng ban
7. **checklists** - Danh sách kiểm tra
8. **checklist_items** - Mục kiểm tra

### Mối quan hệ:
- User ↔ Device: Many-to-One (assignedUser)
- User ↔ Request: Many-to-One (requestingUser, approvingUser)
- Device ↔ Request: Many-to-One
- Device ↔ DeviceHistory: One-to-Many
- User ↔ Chapter: Many-to-One
- User ↔ Notification: One-to-Many

---

## MẸO TRẢ LỜI:

1. **Luôn bắt đầu bằng:** "Hàm này làm..." hoặc "Em cần cập nhật..."
2. **Liệt kê các bước:** 1, 2, 3...
3. **Nhắc đến bảng:** "Cập nhật bảng `tên_bảng`"
4. **Nếu không chắc:** "Em cần kiểm tra lại code để xác nhận"

