-- ============================================================
-- THÊM CHECKLIST CHO TABLET
-- ============================================================

-- BƯỚC 1: Tạo checklist cho Tablet
INSERT INTO checklists (title, device_type, description, created_by) VALUES 
('Quy trình kiểm tra Tablet', 'Tablet', 'Áp dụng cho tất cả các dòng Tablet iPad, Android Tablet', 1);

-- BƯỚC 2: Lấy ID của checklist vừa tạo
-- Chạy query này để lấy ID: SELECT MAX(id) FROM checklists WHERE device_type = 'Tablet';
-- Giả sử ID là 5 (hoặc ID tiếp theo sau 4 checklist hiện có)

-- BƯỚC 3: Tạo các mục kiểm tra cho TABLET
-- Thay số 5 bằng ID thực tế từ BƯỚC 2
INSERT INTO checklist_items (checklist_id, item_description) VALUES 
(5, 'Củ sạc / Adapter'),
(5, 'Cáp sạc'),
(5, 'Đã thoát tài khoản iCloud / Google / Samsung'),
(5, 'Bàn phím rời (nếu có)'),
(5, 'Bút cảm ứng / Stylus (nếu có)'),
(5, 'Ốp lưng / Vỏ bảo vệ (nếu có)'),
(5, 'Kiểm tra ngoại quan (Trầy xước/Móp méo)'),
(5, 'Kiểm tra màn hình (Điểm chết/Sọc/Ám màu)'),
(5, 'Kiểm tra cảm ứng màn hình'),
(5, 'Camera trước / sau'),
(5, 'Loa & Microphone'),
(5, 'Khay sim & Thẻ nhớ (nếu có)'),
(5, 'Tem bảo hành / Mã tài sản còn nguyên');

-- ============================================================
-- HƯỚNG DẪN SỬ DỤNG:
-- 1. Chạy BƯỚC 1 để tạo checklist
-- 2. Chạy query: SELECT MAX(id) FROM checklists WHERE device_type = 'Tablet';
-- 3. Thay số 5 trong BƯỚC 3 bằng ID thực tế vừa lấy được
-- 4. Chạy BƯỚC 3 để thêm các mục kiểm tra
-- ============================================================

