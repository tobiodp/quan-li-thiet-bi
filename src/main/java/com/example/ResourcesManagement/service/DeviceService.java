package com.example.ResourcesManagement.service;

import com.example.ResourcesManagement.DTO.request.CreateDeviceRequestDTO;
import com.example.ResourcesManagement.DTO.response.DeviceResponseDTO;
import com.example.ResourcesManagement.entity.DevicesEntity;
import com.example.ResourcesManagement.repository.DeviceHistoryRepository;
import com.example.ResourcesManagement.repository.DeviceRepository;
import com.example.ResourcesManagement.repository.RequetsRepository;
import com.example.ResourcesManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    
    @Autowired
    private DeviceHistoryRepository deviceHistoryRepository;
    
    @Autowired
    private RequetsRepository requetsRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    // Lấy tất cả thiết bị
    // Trong DeviceService.java

    public List<DeviceResponseDTO> getAllDevices() {
        List<DevicesEntity> entities = deviceRepository.findAll();
        
        // Lọc bỏ thiết bị đã xóa (status = "Đã xóa")
        return entities.stream()
                .filter(device -> device.getStatus() == null || 
                        (!device.getStatus().equals("Đã xóa") && 
                        !device.getStatus().equalsIgnoreCase("DELETED")))
                .map(device -> DeviceResponseDTO.builder()
                        .deviceId(device.getDeviceId())
                        .name(device.getDeviceName())
                        .type(device.getDeviceType()) // <-- Thêm dòng này
                        .status(device.getStatus())
                        .note(device.getNote())       // <-- Thêm dòng này
                        .assignedUser(device.getAssignedUser() != null ? device.getAssignedUser().getUsername() : "Unassigned")
                        .build()).collect(Collectors.toList());
    }


    // Thêm thiết bị mới
    // SỬA HÀM THÊM MỚI
    public void addDevice(CreateDeviceRequestDTO dto) {

        // 1. Xử lý Status: Luôn đảm bảo có giá trị "Sẵn sàng" khi thêm mới
        String status = "Sẵn sàng"; // Mặc định khi thêm mới
        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty() && 
            !dto.getStatus().trim().equals("-- Chọn trạng thái (Mặc định: Sẵn sàng) --")) {
            // Chỉ dùng giá trị từ form nếu có và hợp lệ
            String formStatus = dto.getStatus().trim();
            if (formStatus.equals("Sẵn sàng") || formStatus.equals("Đang sử dụng") || formStatus.equals("Bảo trì")) {
                status = formStatus;
            }
        }

        DevicesEntity device = DevicesEntity.builder()
                .deviceName(dto.getDeviceName())
                .deviceType(dto.getDeviceType())
                .note(dto.getNote())
                .status(status) // <-- Luôn có giá trị "Sẵn sàng" hoặc giá trị hợp lệ
                .isChecked(false)
                .build();

        deviceRepository.save(device);
    }

    // SỬA HÀM CẬP NHẬT
    public void updateDevice(Long id, CreateDeviceRequestDTO dto) {
        DevicesEntity device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        device.setDeviceName(dto.getDeviceName());
        device.setDeviceType(dto.getDeviceType());
        device.setNote(dto.getNote());

        // Xử lý status khi update
        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            device.setStatus(dto.getStatus().trim()); // Giữ nguyên tiếng Việt
        }

        // Nếu có trường isChecked
        if (dto.getIsChecked() != null) {
            device.setIsChecked(dto.getIsChecked());
        }

        deviceRepository.save(device);
    }




    @Transactional
    public void deleteDevice(Long id) {
        DevicesEntity device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị ID: " + id));

        // 1. Xóa tất cả device history liên quan đến thiết bị này
        deviceHistoryRepository.findByDeviceDeviceIdOrderByActionDateDesc(id).forEach(history -> {
            deviceHistoryRepository.delete(history);
        });

        // 2. Xóa hoặc set null các requests liên quan đến thiết bị này
        requetsRepository.findAll().stream()
                .filter(request -> request.getDevice() != null && request.getDevice().getDeviceId().equals(id))
                .forEach(request -> {
                    request.setDevice(null);
                    requetsRepository.save(request);
                });

        // 3. Gỡ người dùng đang mượn (nếu có)
        device.setAssignedUser(null);

        // 4. Xóa thiết bị thật sự
        deviceRepository.delete(device);
    }


    // Trong file DeviceService.java

    public List<DeviceResponseDTO> searchDevices(String keyword) {
        // Gọi repository tìm kiếm
        List<DevicesEntity> entities = deviceRepository.findByDeviceNameContainingIgnoreCaseOrDeviceTypeContainingIgnoreCase(keyword, keyword);

        // Lọc bỏ thiết bị đã xóa và tái sử dụng logic map sang DTO
        return entities.stream()
                .filter(device -> device.getStatus() == null || 
                        (!device.getStatus().equals("Đã xóa") && 
                        !device.getStatus().equalsIgnoreCase("DELETED")))
                .map(device -> DeviceResponseDTO.builder()
                        .deviceId(device.getDeviceId())
                        .name(device.getDeviceName())
                        .type(device.getDeviceType())
                        .status(device.getStatus())
                        .note(device.getNote())
                        .assignedUser(device.getAssignedUser() != null ? device.getAssignedUser().getUsername() : "Unassigned")
                        .build()).collect(Collectors.toList());
    }

    public long countDevices() {
        return deviceRepository.count();
    }

    public DeviceResponseDTO getDeviceById(Long id) {
        DevicesEntity device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        return DeviceResponseDTO.builder()
                .deviceId(id)
                .name(device.getDeviceName())
                .type(device.getDeviceType())
                .status(device.getStatus())
                .note(device.getNote())
                .assignedUser(device.getAssignedUser() != null ? device.getAssignedUser().getUsername() : "Unassigned")
                .build();
    }

    // ============================================
    // THỐNG KÊ - DỮ LIỆU CHO BIỂU ĐỒ
    // ============================================

    /**
     * Đếm số lượng thiết bị theo từng trạng thái
     * Trả về Map: {"Sẵn sàng": 10, "Đang sử dụng": 5, "Bảo trì": 2}
     * Dùng cho Pie Chart
     */
    public java.util.Map<String, Long> getDeviceCountByStatus() {
        List<DevicesEntity> allDevices = deviceRepository.findAll();
        
        // Đếm theo trạng thái, bỏ qua thiết bị "Đã xóa"
        return allDevices.stream()
                .filter(device -> device.getStatus() != null && !device.getStatus().equals("Đã xóa"))
                .collect(Collectors.groupingBy(
                    device -> device.getStatus() != null ? device.getStatus() : "Chưa xác định",
                    Collectors.counting()
                ));
    }
}