package com.example.ResourcesManagement.service;

import com.example.ResourcesManagement.DTO.response.NotificationResponseDTO;
import com.example.ResourcesManagement.entity.NotificationEntity;
import com.example.ResourcesManagement.entity.UserEntity;
import com.example.ResourcesManagement.repository.NotificationRepository;
import com.example.ResourcesManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;

    public List<NotificationResponseDTO> getMyNotifications(Long userId) {
        // 1. Lấy danh sách từ DB (đã sắp xếp mới nhất trước)
        List<NotificationEntity> entities = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // 2. Chuyển đổi sang DTO và trả về
        return entities.stream()
                .map(NotificationResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Hàm đếm số thông báo chưa đọc (nếu bạn muốn hiển thị chấm đỏ trên menu)
    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
    
    // Đánh dấu thông báo đã đọc
    public void markAsRead(Long notificationId, Long userId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        // Kiểm tra xem thông báo có thuộc về user này không
        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: This notification does not belong to you");
        }
        
        notification.setRead(true);
        notificationRepository.save(notification);
    }
    
    // Gửi thông báo đến tất cả user
    public void sendBroadcastNotification(String title, String message, String type) {
        List<UserEntity> allUsers = userRepository.findAll();
        
        for (UserEntity user : allUsers) {
            NotificationEntity notification = new NotificationEntity();
            notification.setUser(user);
            notification.setRead(false);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setType(type);
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }
}