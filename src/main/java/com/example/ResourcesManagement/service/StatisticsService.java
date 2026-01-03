package com.example.ResourcesManagement.service;

import com.example.ResourcesManagement.repository.DeviceHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service chuyên xử lý thống kê cho Dashboard
 * Code đơn giản, dễ hiểu, dễ trình bày
 */
@Service
public class StatisticsService {

    @Autowired
    private DeviceHistoryRepository deviceHistoryRepository;

    @Autowired
    private DeviceService deviceService;

    /**
     * Lấy dữ liệu thiết bị theo trạng thái cho Pie Chart
     * Trả về: {"labels": ["Sẵn sàng", "Đang sử dụng", ...], "data": [10, 5, ...]}
     */
    public Map<String, Object> getDeviceStatusData() {
        // Lấy Map từ DeviceService
        Map<String, Long> statusCount = deviceService.getDeviceCountByStatus();
        
        // Tách thành 2 mảng: labels và data
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        
        // Sắp xếp theo thứ tự ưu tiên
        String[] order = {"Sẵn sàng", "Đang sử dụng", "Bảo trì", "Đã xóa"};
        Set<String> processed = new HashSet<>();
        
        // Thêm theo thứ tự ưu tiên
        for (String status : order) {
            if (statusCount.containsKey(status)) {
                labels.add(status);
                data.add(statusCount.get(status));
                processed.add(status);
            }
        }
        
        // Thêm các trạng thái khác (nếu có)
        for (Map.Entry<String, Long> entry : statusCount.entrySet()) {
            if (!processed.contains(entry.getKey())) {
                labels.add(entry.getKey());
                data.add(entry.getValue());
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("data", data);
        return result;
    }

    /**
     * Lấy Top 10 thiết bị được mượn nhiều nhất cho Bar Chart
     * Trả về: {"labels": ["Macbook Pro", "Dell XPS", ...], "data": [15, 12, ...]}
     */
    public Map<String, Object> getTopBorrowedDevices(int limit) {
        // Lấy dữ liệu từ repository
        List<Object[]> results = deviceHistoryRepository.countBorrowsByDevice();
        
        // Giới hạn số lượng
        List<Object[]> topResults = results.stream()
                .limit(limit)
                .collect(Collectors.toList());
        
        // Tách thành labels và data
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        
        for (Object[] row : topResults) {
            Long deviceId = (Long) row[0];
            String deviceName = (String) row[1];
            Long count = (Long) row[2];
            
            labels.add(deviceName);
            data.add(count);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("data", data);
        return result;
    }
}

