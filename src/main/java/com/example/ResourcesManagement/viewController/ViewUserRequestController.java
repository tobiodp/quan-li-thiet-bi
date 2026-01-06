package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.DTO.response.NotificationResponseDTO;
import com.example.ResourcesManagement.DTO.response.RequestResponseDTO;
import com.example.ResourcesManagement.entity.RequestEntity;
import com.example.ResourcesManagement.entity.UserEntity;
import com.example.ResourcesManagement.repository.UserRepository;
import com.example.ResourcesManagement.service.NotificationService;
import com.example.ResourcesManagement.service.RequestDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ViewUserRequestController {

    @Autowired
    RequestDeviceService requestDeviceService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationService notificationService;

    // --- 1. HIỂN THỊ FORM (Tạo Model trước theo ý bạn) ---
    @GetMapping("/request-device")
    public String showRequestForm(Model model) {
        // Tạo đối tượng DTO rỗng để bind dữ liệu form
        model.addAttribute("requestDTO", new RequestEntity());
        return "user-request-create"; // Trả về file user-request-create.html
    }

    // --- 2. XỬ LÝ (Hứng Model từ HTML về) ---
    @PostMapping("/request-device/create")
    public String createRequest(@ModelAttribute("requestDTO") RequestEntity requestDTO,
                                Model model) {
        try {
            // 1. Lấy User đang đăng nhập (Vì Form không gửi ID người dùng để bảo mật)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserEntity currentUser = userRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 2. Gán User vào request vừa nhận được từ form
            requestDTO.setRequestingUser(currentUser);

            // 3. Gọi Service
            requestDeviceService.addRequest(requestDTO);

            return "redirect:/user-requests?success";

        } catch (Exception e) {
            // Nếu lỗi, gửi lại chính object đó để không mất dữ liệu người dùng đã nhập
            model.addAttribute("requestDTO", requestDTO);
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "user-request-create";
        }
    }


    // 3. Hiển thị danh sách yêu cầu của User
    @GetMapping("/user-requests")
    public String viewMyRequests(Model model) {
        // Lấy User hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Gọi Service lấy danh sách của RIÊNG user này
        List<RequestResponseDTO> myRequests = requestDeviceService.getMyRequests(currentUser.getId());

        // Đẩy danh sách ra View
        model.addAttribute("myRequests", myRequests);

        return "user-requests"; // Trả về user-requests.html
    }


    // hiển thị thông báo của user
    @GetMapping("/user-notifications")
    public String viewNotifications(Model model) {
        // 1. Lấy User đang đăng nhập
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Gọi Service lấy dữ liệu THẬT từ DB
        List<NotificationResponseDTO> notifications = notificationService.getMyNotifications(currentUser.getId());
        
        // 3. Đếm số thông báo chưa đọc
        long unreadCount = notificationService.countUnread(currentUser.getId());

        // 4. Đẩy ra view
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);

        return "user-notifications";
    }
    
    // Đánh dấu thông báo đã đọc
    @GetMapping("/user-notifications/{id}/read")
    public String markNotificationAsRead(@PathVariable("id") Long notificationId) {
        // 1. Lấy User đang đăng nhập
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 2. Đánh dấu đã đọc
        notificationService.markAsRead(notificationId, currentUser.getId());
        
        // 3. Quay lại trang thông báo
        return "redirect:/user-notifications";
    }
    
    // Model attribute để truyền số thông báo chưa đọc cho tất cả các trang user
    @org.springframework.web.bind.annotation.ModelAttribute("unreadNotificationCount")
    public long getUnreadNotificationCount() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                UserEntity currentUser = userRepository.findByUsername(auth.getName()).orElse(null);
                if (currentUser != null) {
                    return notificationService.countUnread(currentUser.getId());
                }
            }
        } catch (Exception e) {
            // Ignore errors
        }
        return 0;
    }
}