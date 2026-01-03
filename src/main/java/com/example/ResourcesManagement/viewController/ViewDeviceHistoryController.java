package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.entity.DeviceHistoryEntity;
import com.example.ResourcesManagement.entity.UserEntity;
import com.example.ResourcesManagement.repository.DeviceHistoryRepository;
import com.example.ResourcesManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ViewDeviceHistoryController {

    @Autowired
    private DeviceHistoryRepository deviceHistoryRepository;

    @Autowired
    private UserRepository userRepository;

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
