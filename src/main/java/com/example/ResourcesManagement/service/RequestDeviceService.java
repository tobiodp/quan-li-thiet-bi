package com.example.ResourcesManagement.service;

import com.example.ResourcesManagement.DTO.response.RequestResponseDTO;
import com.example.ResourcesManagement.DTO.response.UserResponseDTO;
import com.example.ResourcesManagement.entity.*;
import com.example.ResourcesManagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestDeviceService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequetsRepository requetsRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private DeviceHistoryRepository deviceHistoryRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    // --- 1. USER GỬI YÊU CẦU ---
    public String addRequest(RequestEntity request) {
        request.setStatus("Chờ duyệt");
        requetsRepository.save(request);
        return "Request added successfully";
    }

    // --- 2. CÁC HÀM LẤY DANH SÁCH ---

    // Lấy danh sách chờ duyệt (Chờ duyệt)
    public List<RequestResponseDTO> getAllRequest() {
        return convertToDTOList(requetsRepository.findByStatus("Chờ duyệt"));
    }

    // [QUAN TRỌNG] Lấy cả Chờ duyệt và Đã duyệt (Để Admin vừa duyệt vừa trả máy)
    public List<RequestResponseDTO> getAllManageableRequests() {
        // Cần thêm hàm findByStatusIn trong Repository nếu chưa có
        // Hoặc dùng tạm logic này nếu repo chưa hỗ trợ IN
        List<RequestEntity> pending = requetsRepository.findByStatus("Chờ duyệt");
        List<RequestEntity> approved = requetsRepository.findByStatus("Đã duyệt");
        pending.addAll(approved);
        return convertToDTOList(pending);
    }

    // Lấy danh sách theo trạng thái cụ thể (Dùng cho trang "Đang mượn")
    public List<RequestResponseDTO> getRequestsByStatus(String status) {
        return convertToDTOList(requetsRepository.findByStatus(status));
    }

    // --- 3. LOGIC DUYỆT & BÀN GIAO ---

    public void approveAndAssignDevice(Long requestId, Long selectedDeviceId,
                                       List<String> checkedItems, String adminNote) {

        // 1. Tìm Request & Device
        RequestEntity request = requetsRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));
        DevicesEntity device = deviceRepository.findById(selectedDeviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));

        // 2. Xử lý Checklist (List -> String)
        String checklistResult = (checkedItems == null || checkedItems.isEmpty())
                ? "Không có mục nào được chọn."
                : "Đã kiểm tra: " + String.join(", ", checkedItems);

        // 3. Lưu Lịch sử (History) - ACTION: Mượn
        DeviceHistoryEntity history = DeviceHistoryEntity.builder()
                .action("Mượn")
                .actionDate(LocalDateTime.now())
                .device(device)
                .user(request.getRequestingUser())
                .checklistResult(checklistResult)
                .note(adminNote)
                .build();
        deviceHistoryRepository.save(history);

        // 4. Cập nhật Device
        device.setStatus("Đang sử dụng");
        device.setAssignedUser(request.getRequestingUser());
        deviceRepository.save(device);

        // 5. Cập nhật Request
        request.setStatus("Đang mượn");
        request.setNameDevice(device.getDeviceName());
        request.setDevice(device); // [QUAN TRỌNG] Lưu liên kết để sau này trả máy biết máy nào
        requetsRepository.save(request);

        // 6. Gửi Thông báo
        createNotification(request.getRequestingUser(), "Yêu cầu được duyệt ✅",
                "Bạn đã được cấp thiết bị: " + device.getDeviceName());
    }

    // --- 4. LOGIC TRẢ THIẾT BỊ (Thu hồi) ---

    public void returnDevice(Long requestId, List<String> checkedItems, String condition, String note) {
        // 1. Tìm lại yêu cầu mượn gốc
        RequestEntity request = requetsRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu id: " + requestId));

        // Kiểm tra trạng thái hợp lệ
        if (!"Đang mượn".equals(request.getStatus()) && !"Đã duyệt".equals(request.getStatus())) {
            throw new RuntimeException("Yêu cầu này không hợp lệ để trả máy (Status: " + request.getStatus() + ")");
        }

        // 2. Lấy thông tin thiết bị từ yêu cầu
        DevicesEntity device = request.getDevice();
        if (device == null) {
            throw new RuntimeException("Lỗi dữ liệu: Yêu cầu này chưa được liên kết với thiết bị nào!");
        }

        // 3. Xử lý Checklist (List -> String)
        String checklistResult = (checkedItems == null || checkedItems.isEmpty())
                ? "Không có mục nào được chọn."
                : "Đã kiểm tra: " + String.join(", ", checkedItems);

        // 4. Cập nhật trạng thái Yêu cầu -> Đã trả
        request.setStatus("Đã trả");
        requetsRepository.save(request);

        // 5. Cập nhật trạng thái Thiết bị dựa trên tình trạng
        if ("Tốt".equals(condition)) {
            // Nếu thiết bị tốt -> Trả về kho (Sẵn sàng)
            device.setStatus("Sẵn sàng");
        } else if ("Hư hỏng".equals(condition)) {
            // Nếu thiết bị bị hư hỏng -> Chuyển sang trạng thái Bảo trì
            device.setStatus("Bảo trì");
        } else if ("Mất mát".equals(condition)) {
            // Nếu thiết bị bị mất -> Đánh dấu là "Mất mát"
            device.setStatus("Mất mát");
        } else {
            // Mặc định: Trả về kho nếu không xác định được condition
            device.setStatus("Sẵn sàng");
        }
        
        device.setAssignedUser(null); // Gỡ người dùng ra
        deviceRepository.save(device);

        // 6. Ghi Lịch sử (History) - ACTION: Trả
        DeviceHistoryEntity history = DeviceHistoryEntity.builder()
                .device(device)
                .user(request.getRequestingUser())
                .action("Trả")
                .actionDate(LocalDateTime.now())
                .checklistResult(checklistResult) // Kết quả checklist
                .note(note != null ? note : "") // Ghi chú phạt/hỏng
                .build();
        deviceHistoryRepository.save(history);

        // 7. Gửi thông báo dựa trên tình trạng thiết bị
        String notificationTitle;
        String notificationMessage;
        
        if ("Mất mát".equals(condition)) {
            notificationTitle = "Thiết bị đã được đánh dấu mất mát ⚠️";
            notificationMessage = "Thiết bị: " + device.getDeviceName() + " đã được đánh dấu là mất mát. Vui lòng liên hệ ty  để xử lý.";
        } else if ("Hư hỏng".equals(condition)) {
            notificationTitle = "Trả thiết bị thành công ⚠️";
            notificationMessage = "Bạn đã trả thiết bị: " + device.getDeviceName() + ". Thiết bị đã được chuyển sang trạng thái bảo trì do hư hỏng.";
        } else {
            // Tốt hoặc mặc định
            notificationTitle = "Trả thiết bị thành công ✅";
            notificationMessage = "Bạn đã hoàn tất trả thiết bị: " + device.getDeviceName();
        }
        
        createNotification(request.getRequestingUser(), notificationTitle, notificationMessage);
    }


    // Hàm tạo thông báo chung
    private void createNotification(UserEntity user, String title, String message) {
        NotificationEntity notification = NotificationEntity.builder()
                .title(title)
                .message(message)
                .user(user)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

    // Từ chối yêu cầu (Thủ công)
    public void rejectRequestManual(Long requestId) {
        RequestEntity request = requetsRepository.findById(requestId).orElseThrow();

        request.setStatus("Đã từ chối");
        request.setDescription(request.getDescription() + " | Admin đã từ chối.");
        requetsRepository.save(request);

        createNotification(request.getRequestingUser(), "Yêu cầu bị từ chối ❌",
                "Admin đã từ chối yêu cầu của bạn.");
    }



    public long countRequestDevices() {
        return requetsRepository.countByStatus("Chờ duyệt");
    }

    public List<RequestResponseDTO> getMyRequests(Long id) {
        return convertToDTOList(requetsRepository.findByRequestingUserId(id));
    }

    public RequestResponseDTO getRequestById(Long requestId) {
        RequestEntity requestEntity = requetsRepository.findById(requestId).orElseThrow();
        return convertSingleDTO(requestEntity);
    }

    // --- Helper: Convert Entity to DTO ---
    private List<RequestResponseDTO> convertToDTOList(List<RequestEntity> entities) {
        return entities.stream().map(this::convertSingleDTO).toList();
    }

    private RequestResponseDTO convertSingleDTO(RequestEntity entity) {
        UserEntity user = entity.getRequestingUser();
        UserResponseDTO userDTO = UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .chapterName(user.getChapter() != null ? user.getChapter().getName() : null)
                .build();

        return RequestResponseDTO.builder()
                .id(entity.getRequestId())
                .user(userDTO)
                .deviceType(entity.getDeviceType())
                .description(entity.getDescription())
                .status(entity.getStatus())
                // [ĐÃ SỬA] Thêm dòng này để fix lỗi Thymeleaf "nameDevice cannot be found"
                .nameDevice(entity.getNameDevice())
                .build();
    }
}