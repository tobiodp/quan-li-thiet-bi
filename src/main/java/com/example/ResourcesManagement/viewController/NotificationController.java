package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Hiển thị trang gửi thông báo
    @GetMapping("/admin/notifications/send")
    public String showSendNotificationPage() {
        return "admin-notification-create";
    }
 
    // Xử lý gửi thông báo đến tất cả user
    @PostMapping("/admin/notifications/send-broadcast")
    public String sendBroadcastNotification(
            @RequestParam("title") String title,
            @RequestParam("message") String message,
            @RequestParam("type") String type,
            RedirectAttributes redirectAttributes) {
        
        try {
            notificationService.sendBroadcastNotification(title, message, type);
            redirectAttributes.addFlashAttribute("success", "Thông báo đã được gửi đến tất cả người dùng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi gửi thông báo: " + e.getMessage());
        }
        
        return "redirect:/admin/notifications/send";
    }
}
