-- ============================================================
-- THÊM CHECKLIST CHO TABLET (Tự động lấy ID)
-- ============================================================

-- 1. Tạo checklist cho Tablet và lưu ID vào biến (nếu database hỗ trợ)
-- Hoặc chạy riêng và lấy ID từ kết quả

INSERT INTO checklists (title, device_type, description, created_by) VALUES 
('Quy trình kiểm tra Tablet', 'Tablet', 'Áp dụng cho tất cả các dòng Tablet iPad, Android Tablet', 1);

-- ============================================================
-- BƯỚC 2: TẠO CÁC MỤC KIỂM TRA CHO TABLET
-- Sử dụng subquery để tự động lấy checklist_id của Tablet vừa tạo
-- ============================================================

-- Cách 1: Sử dụng subquery (nếu database hỗ trợ)
INSERT INTO checklist_items (checklist_id, item_description) 
SELECT id, 'Củ sạc / Adapter'
FROM checklists 
WHERE device_type = 'Tablet' AND title = 'Quy trình kiểm tra Tablet'
ORDER BY id DESC LIMIT 1;

INSERT INTO checklist_items (checklist_id, item_description) 
SELECT id, 'Cáp sạc'
FROM checklists 
WHERE device_type = 'Tablet' AND title = 'Quy trình kiểm tra Tablet'
ORDER BY id DESC LIMIT 1;

INSERT INTO checklist_items (checklist_id, item_description) 
SELECT id, 'Đã thoát tài khoản iCloud / Google / Samsung'
FROM checklists 
WHERE device_type = 'Tablet' AND title = 'Quy trình kiểm tra Tablet'
ORDER BY id DESC LIMIT 1;

INSERT INTO checklist_items (checklist_id, item_description) 
SELECT id, 'Bàn phím rời (nếu có)'
FROM checklists 
WHERE device_type = 'Tablet' AND title = 'Quy trình kiểm tra Tablet'
ORDER BY id DESC LIMIT 1;

INSERT INTO checklist_items (checklist_id, item_description) 
SELECT id, 'Bút cảm ứng / Stylus (nếu có)'
FROM checklists 
WHERE device_type = 'Tablet' AND title = 'Quy trình kiểm tra Tablet'
ORDER BY id DESC LIMIT 1;

INSERT INTO checklist_items (checklist_id, item_description) 
SELECT id, 'Ốp lưng / Vỏ bảo vệ (nếu có)'
FROM checklists 
WHERE device_type = 'Tablet' AND title = 'Quy trình kiểm tra Tablet'
ORDER BY id DESC LIMIT 1;

INSERT INTO checklist_items (checklist_id, item_description) 
SELECT id, 'Kiểm tra ngoại quan (Trầy xước/Móp méo)'
FROM checklists 
WHERE device_type = 'Tablet' AND title = 'Quy trình kiểm tra Tablet'
ORDER BY id DESC LIMIT 1;

INSERT INTO checklist_items (checklist_id, item_description) 
SELECT id, 'Kiểm tra màn hình (Điểm chết/Sọc/Ám màu)'
FROM checklists 
WHERE device_type = 'Tablet' AND title = 'Quy trình kiểm tra Tablet'
ORDER BY id DESC LIMIT 1;

INSERT INTO checklist_items (checklist_id, item_description) 
SELECT id, 'Kiểm tra cảm ứng màn hình'
FROM checklists 
WHERE device_type = 'Tablet' AND title = 'Quy trình kiểm tra Tablet'
ORDER BY id DESC LIMIT 1;

INSERT INTO checklist_items (checklist_id, item_description) 
SELECT id, 'Camera trước / sau'
FROM checklists 
WHERE device_type = 'Tablet' AND title = 'Quy trình kiểm tra Tablet'
ORDER BY id DESC LIMIT 1;

INSERT INTO checklist_items (checklist_id, item_description) 
SELECT id, 'Loa & Microphone'
FROM checklists 
WHERE device_type = 'Tablet' AND title = 'Quy trình kiểm tra Tablet'
ORDER BY id DESC LIMIT 1;

INSERT INTO checklist_items (checklist_id, item_description) 
SELECT id, 'Khay sim & Thẻ nhớ (nếu có)'
FROM checklists 
WHERE device_type = 'Tablet' AND title = 'Quy trình kiểm tra Tablet'
ORDER BY id DESC LIMIT 1;

INSERT INTO checklist_items (checklist_id, item_description) 
SELECT id, 'Tem bảo hành / Mã tài sản còn nguyên'
FROM checklists 
WHERE device_type = 'Tablet' AND title = 'Quy trình kiểm tra Tablet'
ORDER BY id DESC LIMIT 1;

-- ============================================================
-- CÁCH 2: Nếu database không hỗ trợ subquery trong INSERT
-- Hãy chạy query này trước để lấy ID:
-- SELECT id FROM checklists WHERE device_type = 'Tablet' ORDER BY id DESC LIMIT 1;
-- Sau đó thay thế số 5 trong file ADD_TABLET_CHECKLIST.sql bằng ID thực tế
-- ============================================================

