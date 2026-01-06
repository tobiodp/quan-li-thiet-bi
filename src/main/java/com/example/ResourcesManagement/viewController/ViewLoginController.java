package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.entity.UserEntity;
import com.example.ResourcesManagement.repository.DeviceRepository;
import com.example.ResourcesManagement.service.NotificationService;
import com.example.ResourcesManagement.service.RequestDeviceService;
import com.example.ResourcesManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.ResourcesManagement.repository.UserRepository;

@Controller
public class ViewLoginController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private RequestDeviceService requestDeviceService;
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserRepository userRepository;

    // Model attribute để truyền số thông báo chưa đọc cho tất cả các trang user
    @ModelAttribute("unreadNotificationCount")
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

    @GetMapping("/viewLogin")
    public String showLoginPage(Model model) {
        model.addAttribute("user", new UserEntity());
        return "user-login";
    }

    @GetMapping("/login-success")
    public String loginSuccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        UserEntity currentUser = userService.getUserByUsername(username);
        
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            System.out.println("Đăng nhập với vai trò ADMIN");
            return "redirect:/dashboardController";
        } else {
            System.out.println("Đăng nhập với vai trò USER");
            return "redirect:/user-dashboard";
        }
    }

    @GetMapping("/user-dashboard")
    public String userDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        UserEntity currentUser = userService.getUserByUsername(username);
        
        // Đếm số thiết bị đang giữ
        long holdingCount = deviceRepository.findByAssignedUserId(currentUser.getId()).size();
        
        // Đếm số yêu cầu chờ duyệt
        long pendingCount = requestDeviceService.getMyRequests(currentUser.getId()).stream()
                .filter(req -> "Chờ duyệt".equals(req.getStatus()))
                .count();
        
        // Đếm tổng số yêu cầu (lịch sử)
        long totalHistoryCount = requestDeviceService.getMyRequests(currentUser.getId()).size();
        
        // Đếm số thông báo chưa đọc
        long unreadNotificationCount = notificationService.countUnread(currentUser.getId());
        
        model.addAttribute("user", currentUser);
        model.addAttribute("holdingCount", holdingCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("totalHistoryCount", totalHistoryCount);
        model.addAttribute("unreadNotificationCount", unreadNotificationCount);
        
        return "user-dashboard";
    }

    @GetMapping("/user-home")
    public String userHome(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        UserEntity currentUser = userService.getUserByUsername(username);
        
        // Đếm số thiết bị đang giữ
        long holdingCount = deviceRepository.findByAssignedUserId(currentUser.getId()).size();
        
        // Đếm số yêu cầu chờ duyệt
        long pendingCount = requestDeviceService.getMyRequests(currentUser.getId()).stream()
                .filter(req -> "Chờ duyệt".equals(req.getStatus()))
                .count();
        
        // Đếm tổng số yêu cầu (lịch sử)
        long totalHistoryCount = requestDeviceService.getMyRequests(currentUser.getId()).size();
        
        // Đếm số thông báo chưa đọc
        long unreadNotificationCount = notificationService.countUnread(currentUser.getId());
        
        model.addAttribute("user", currentUser);
        model.addAttribute("holdingCount", holdingCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("totalHistoryCount", totalHistoryCount);
        model.addAttribute("unreadNotificationCount", unreadNotificationCount);
        
        return "user-dashboard";
    }
}