package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.DTO.request.CreateDeviceRequestDTO;
import com.example.ResourcesManagement.DTO.response.DeviceResponseDTO;
import com.example.ResourcesManagement.repository.DeviceHistoryRepository;
import com.example.ResourcesManagement.service.DeviceService;
import com.example.ResourcesManagement.service.QRCodeService;
import com.example.ResourcesManagement.service.RequestDeviceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Controller
public class ViewDeviceController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private RequestDeviceService requestDeviceService;
    
    @Autowired
    private QRCodeService qrCodeService;
    
    @Autowired
    private DeviceHistoryRepository deviceHistoryRepository;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${app.base-url:}")
    private String baseUrl;
    
    /**
     * Lấy địa chỉ server để tạo URL cho QR Code
     * - Nếu có cấu hình base-url (production): dùng domain thực
     * - Nếu không, tự động lấy từ request (domain thực khi deploy, IP khi local)
     */
    private String getServerAddress(HttpServletRequest request) {
        // Nếu có cấu hình base-url (production), dùng nó
        if (baseUrl != null && !baseUrl.isEmpty()) {
            // Đảm bảo có https://
            if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
                return "https://" + baseUrl;
            }
            return baseUrl;
        }
        
        // Tự động lấy từ request (tự động detect domain khi deploy)
        String scheme = request.getScheme(); // http hoặc https
        String serverName = request.getServerName(); // domain hoặc IP
        int serverPort = request.getServerPort();
        
        // Nếu là domain thực (không phải localhost/IP), dùng HTTPS và không cần port
        if (!serverName.equals("localhost") && !serverName.startsWith("192.168.") 
            && !serverName.startsWith("10.") && !serverName.startsWith("172.") 
            && !serverName.equals("127.0.0.1")) {
            // Production: dùng HTTPS, không cần port
            return "https://" + serverName;
        }
        
        // Local: dùng IP hoặc localhost
        if (serverName.equals("localhost") || serverName.equals("127.0.0.1")) {
            // Thử lấy IP thực của máy tính
            try {
                InetAddress localhost = InetAddress.getLocalHost();
                String ip = localhost.getHostAddress();
                
                if (ip.equals("127.0.0.1") || ip.equals("0.0.0.0")) {
                    InetAddress[] allAddresses = InetAddress.getAllByName(localhost.getHostName());
                    for (InetAddress addr : allAddresses) {
                        if (!addr.isLoopbackAddress() && !addr.isLinkLocalAddress()) {
                            ip = addr.getHostAddress();
                            break;
                        }
                    }
                }
                return "http://" + ip;
            } catch (UnknownHostException e) {
                return "http://localhost";
            }
        }
        
        // Trường hợp khác: dùng serverName từ request
        if (serverPort == 80 || serverPort == 443) {
            return scheme + "://" + serverName;
        }
        return scheme + "://" + serverName + ":" + serverPort;
    }

    // --- 1. HIỂN THỊ DANH SÁCH & TÌM KIẾM ---
    @GetMapping("/viewDevices")
    public String viewDevices(Model model, @RequestParam(required = false) String keyword) {
        List<DeviceResponseDTO> devices;

        // Nếu có từ khóa tìm kiếm thì gọi hàm search, ngược lại gọi hàm getAll
        if (keyword != null && !keyword.isEmpty()) {
            devices = deviceService.searchDevices(keyword);
        } else {
            devices = deviceService.getAllDevices();
        }

        long requestDeviceCount = requestDeviceService.countRequestDevices();

        model.addAttribute("requestDeviceCount", requestDeviceCount);
        model.addAttribute("devices", devices);
        model.addAttribute("keyword", keyword);

        return "admin-devices"; // Trả về file HTML danh sách
    }

    // --- 2. CHỨC NĂNG THÊM MỚI ---

    // Hiển thị Form thêm mới (GET)
    @GetMapping("/devices/add")
    public String showAddDeviceForm(Model model) {
        model.addAttribute("deviceRequest", new CreateDeviceRequestDTO());
        return "admin-device-create"; // Trả về file form chung
    }

    // Xử lý dữ liệu thêm mới (POST)
    @PostMapping("/devices/add")
    public String addDevice(@ModelAttribute("deviceRequest") CreateDeviceRequestDTO deviceRequest,
                            RedirectAttributes redirectAttributes) {
        try {
            deviceService.addDevice(deviceRequest);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Thêm thiết bị thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Lỗi: " + e.getMessage());
        }
        return "redirect:/viewDevices";
    }

    // --- 3. CHỨC NĂNG CẬP NHẬT (SỬA) ---

    // Hiển thị Form sửa (GET)
    @GetMapping("/devices/edit/{id}")
    public String showEditDeviceForm(@PathVariable Long id, Model model) {
        // Lấy thông tin cũ từ database
        DeviceResponseDTO device = deviceService.getDeviceById(id);

        // Map dữ liệu cũ vào DTO để hiển thị lên form
        CreateDeviceRequestDTO deviceRequest = new CreateDeviceRequestDTO();
        deviceRequest.setDeviceName(device.getName());
        deviceRequest.setDeviceType(device.getType());
        deviceRequest.setNote(device.getNote());
        deviceRequest.setStatus(device.getStatus());
        // Nếu có trường isChecked/checklistId thì set thêm ở đây nếu cần

        model.addAttribute("deviceRequest", deviceRequest);
        model.addAttribute("deviceId", id); // Quan trọng: Để Thymeleaf biết là đang ở chế độ Edit

        return "admin-device-create";
    }

    // Xử lý dữ liệu cập nhật (POST) - 👇 ĐOẠN NÀY ĐÃ ĐƯỢC MỞ LẠI 👇
    @PostMapping("/devices/edit/{id}")
    public String updateDevice(@PathVariable Long id,
                               @ModelAttribute("deviceRequest") CreateDeviceRequestDTO deviceRequest,
                               RedirectAttributes redirectAttributes) {
        try {
            deviceService.updateDevice(id, deviceRequest);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Cập nhật thiết bị thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Lỗi cập nhật: " + e.getMessage());
        }
        return "redirect:/viewDevices";
    }

    // --- 4. CHỨC NĂNG XÓA ---
    @GetMapping("/devices/delete/{id}")
    public String deleteDevice(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            deviceService.deleteDevice(id); // Hàm này trong Service đã xử lý Soft Delete
            redirectAttributes.addFlashAttribute("successMessage", "✅ Đã xóa thiết bị thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Lỗi: Không thể xóa thiết bị này. " + e.getMessage());
        }

        return "redirect:/viewDevices";
    }
    
    // --- 5. CHỨC NĂNG QR CODE ---
    
    // Hiển thị QR Code cho admin (có thể in)
    @GetMapping("/devices/{id}/qr")
    public String showQRCode(@PathVariable Long id, Model model, HttpServletRequest request) {
        DeviceResponseDTO device = deviceService.getDeviceById(id);
        
        // Lấy địa chỉ server (domain thực nếu production, IP nếu local)
        String serverAddress = getServerAddress(request);
        
        // Tạo URL để quét QR Code
        String qrUrl;
        if (serverAddress.startsWith("https://")) {
            // Production: dùng domain thực, không cần port
            qrUrl = serverAddress + "/device/qr/" + id;
        } else {
            // Local: dùng IP + port
            qrUrl = serverAddress + ":" + serverPort + "/device/qr/" + id;
        }
        
        // Tạo QR Code dưới dạng Base64
        String qrCodeBase64 = qrCodeService.generateQRCodeBase64(qrUrl, 400, 400);
        
        model.addAttribute("device", device);
        model.addAttribute("qrCodeBase64", qrCodeBase64);
        model.addAttribute("qrUrl", qrUrl);
        model.addAttribute("serverAddress", serverAddress.replace("http://", "").replace("https://", "")); // Hiển thị để user biết
        
        return "admin-device-qr";
    }
    
    // Trang public xem thông tin thiết bị khi quét QR Code (không cần đăng nhập)
    @GetMapping("/device/qr/{id}")
    public String viewDeviceByQR(@PathVariable Long id, Model model) {
        DeviceResponseDTO device = deviceService.getDeviceById(id);
        
        // Lấy lịch sử mượn/trả của thiết bị này
        List<com.example.ResourcesManagement.entity.DeviceHistoryEntity> history = 
            deviceHistoryRepository.findByDeviceDeviceIdOrderByActionDateDesc(id);
        
        model.addAttribute("device", device);
        model.addAttribute("history", history);
        
        return "public-device-info";
    }
}