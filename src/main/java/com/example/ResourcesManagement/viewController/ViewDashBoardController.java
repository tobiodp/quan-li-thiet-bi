package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.DTO.response.DeviceResponseDTO;
import com.example.ResourcesManagement.service.ChapterService;
import com.example.ResourcesManagement.service.DeviceService;
import com.example.ResourcesManagement.service.RequestDeviceService;
import com.example.ResourcesManagement.service.StatisticsService;
import com.example.ResourcesManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class ViewDashBoardController {
    @Autowired
    UserService userService;

    @Autowired
    DeviceService deviceService;

    @Autowired
    ChapterService chapterService;

    @Autowired
    RequestDeviceService requestDeviceService;

    @Autowired
    StatisticsService statisticsService;

    // thống kê số lượng user, device, chapter, requestDevice
    @GetMapping("/dashboardController")
    public String viewDashboard(Model model) {
        long userCount = userService.countUsers();
        long deviceCount = deviceService.countDevices();
        long chapterCount = chapterService.countChapters();
        long requestDeviceCount = requestDeviceService.countRequestDevices();

        model.addAttribute("userCount", userCount);
        model.addAttribute("deviceCount", deviceCount);
        model.addAttribute("chapterCount", chapterCount);
        model.addAttribute("requestDeviceCount", requestDeviceCount);

        return "admin-dashboard";
    }

    /**
     * API trả về dữ liệu cho Pie Chart - Thiết bị theo trạng thái
     * URL: /api/stats/device-status
     * Trả về JSON: {"labels": [...], "data": [...]}
     */
    @GetMapping("/api/stats/device-status")
    public ResponseEntity<Map<String, Object>> getDeviceStatusStats() {
        Map<String, Object> data = statisticsService.getDeviceStatusData();
        return ResponseEntity.ok(data);
    }

    /**
     * API trả về dữ liệu cho Bar Chart - Top 10 thiết bị được mượn nhiều nhất
     * URL: /api/stats/top-borrowed?limit=10
     * Trả về JSON: {"labels": [...], "data": [...]}
     */
    @GetMapping("/api/stats/top-borrowed")
    public ResponseEntity<Map<String, Object>> getTopBorrowedDevices(
            @RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> data = statisticsService.getTopBorrowedDevices(limit);
        return ResponseEntity.ok(data);
    }
}
