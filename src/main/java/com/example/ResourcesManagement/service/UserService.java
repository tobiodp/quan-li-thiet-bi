package com.example.ResourcesManagement.service;

import com.example.ResourcesManagement.entity.ChapterEntity;
import com.example.ResourcesManagement.entity.UserEntity;
import com.example.ResourcesManagement.repository.ChapterRepository;
import com.example.ResourcesManagement.repository.DeviceHistoryRepository;
import com.example.ResourcesManagement.repository.DeviceRepository;
import com.example.ResourcesManagement.repository.NotificationRepository;
import com.example.ResourcesManagement.repository.RequetsRepository;
import com.example.ResourcesManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserService.java
 */

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private RequetsRepository requetsRepository;
    
    @Autowired
    private DeviceHistoryRepository deviceHistoryRepository;

    // Hàm lấy thông tin user đầy đủ (để check role)
    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // THAY ĐỔI 2: Thêm phương thức bắt buộc của UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm kiếm user trong CSDL bằng username
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với username: " + username));

        // Tạo một đối tượng UserDetails từ UserEntity
        // Spring Security sẽ sử dụng thông tin này để xác thực và phân quyền
        return new User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                // Chuyển đổi role (String) của bạn thành GrantedAuthority
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole()))
        );
    }


    public UserEntity findUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Create user method
    public void createUser(UserEntity user) {
        // 👇 1. SỬA LỖI: Kiểm tra nếu chưa có Role thì gán mặc định là "EMPLOYEE" (hoặc "USER")
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        // 2. Mã hóa mật khẩu (Code cũ của bạn)
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setChapter(user.getChapter());
        // 3. Lưu vào DB
        userRepository.save(user);
    }


    public List<UserEntity> getListUser() {
        return userRepository.findAll();
    }

    // Update user
    public void updateUser(Long id, UserEntity user) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setRole(user.getRole());
        existingUser.setChapter(user.getChapter());
        
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        userRepository.save(existingUser);
    }

    // Delete user
    @Transactional
    public void deleteUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 1. Xóa tất cả notifications của user
        List<com.example.ResourcesManagement.entity.NotificationEntity> notifications = 
                notificationRepository.findByUserIdOrderByCreatedAtDesc(id);
        if (notifications != null && !notifications.isEmpty()) {
            notificationRepository.deleteAll(notifications);
        }
        
        // 2. Gỡ user khỏi các devices đang được gán (set assignedUser = null)
        deviceRepository.findByAssignedUserId(id).forEach(device -> {
            device.setAssignedUser(null);
            deviceRepository.save(device);
        });
        
        // 3. Xóa tất cả requests của user (yêu cầu mượn thiết bị)
        requetsRepository.findByRequestingUserId(id).forEach(request -> {
            requetsRepository.delete(request);
        });
        
        // 3b. Xử lý requests mà user là người duyệt (approvingUser) - set null
        requetsRepository.findAll().stream()
                .filter(request -> request.getApprovingUser() != null && request.getApprovingUser().getId().equals(id))
                .forEach(request -> {
                    request.setApprovingUser(null);
                    requetsRepository.save(request);
                });
        
        // 4. Xóa tất cả device history liên quan đến user
        deviceHistoryRepository.findByUser_IdOrderByActionDateDesc(id).forEach(history -> {
            deviceHistoryRepository.delete(history);
        });
        
        // 5. Xóa tất cả device history mà user là handler (người xử lý)
        deviceHistoryRepository.findAll().stream()
                .filter(history -> history.getHandler() != null && history.getHandler().getId().equals(id))
                .forEach(history -> {
                    history.setHandler(null);
                    deviceHistoryRepository.save(history);
                });
        
        // 6. Cuối cùng mới xóa user
        userRepository.deleteById(id);
    }

    // Count users
    public long countUsers() {
        return userRepository.count();
    }
}