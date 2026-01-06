package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.entity.DeviceHistoryEntity;
import com.example.ResourcesManagement.entity.UserEntity;
import com.example.ResourcesManagement.repository.DeviceHistoryRepository;
import com.example.ResourcesManagement.repository.UserRepository;
import com.example.ResourcesManagement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class ViewDeviceHistoryController {

    @Autowired
    private DeviceHistoryRepository deviceHistoryRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
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

    @GetMapping("/my-device-history")
    public String viewMyDeviceHistory(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<DeviceHistoryEntity> history = deviceHistoryRepository
                .findByUserIdWithDetailsOrderByActionDateDesc(currentUser.getId());

        model.addAttribute("history", history);
        return "user-device-history";
    }

    @GetMapping("/admin/device-history")
    public String viewAdminDeviceHistory(Model model) {
        List<DeviceHistoryEntity> history = deviceHistoryRepository.findAllWithDetailsOrderByActionDateDesc();
        model.addAttribute("history", history);
        return "admin-device-history";
    }
}
