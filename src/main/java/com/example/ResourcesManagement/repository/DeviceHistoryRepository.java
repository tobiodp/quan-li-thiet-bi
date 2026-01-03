package com.example.ResourcesManagement.repository;

import com.example.ResourcesManagement.entity.DeviceHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceHistoryRepository extends JpaRepository<DeviceHistoryEntity, Long> {

    // --- DÒNG ĐÚNG (Đã sửa) ---
    // Spring sẽ tìm thuộc tính 'device' -> rồi tìm tiếp 'deviceId' bên trong nó
    List<DeviceHistoryEntity> findByDeviceDeviceIdOrderByActionDateDesc(Long deviceId);

    // --- CÁC HÀM KHÁC ---
    List<DeviceHistoryEntity> findByUser_IdOrderByActionDateDesc(Long userId);

    List<DeviceHistoryEntity> findByAction(String action);

    @Query("""
            select dh
            from DeviceHistoryEntity dh
            join fetch dh.device d
            left join fetch dh.user u
            left join fetch dh.handler h
            order by dh.actionDate desc
            """)
    List<DeviceHistoryEntity> findAllWithDetailsOrderByActionDateDesc();

    @Query("""
            select dh
            from DeviceHistoryEntity dh
            join fetch dh.device d
            left join fetch dh.user u
            left join fetch dh.handler h
            where u.id = :userId
            order by dh.actionDate desc
            """)
    List<DeviceHistoryEntity> findByUserIdWithDetailsOrderByActionDateDesc(@Param("userId") Long userId);

    /**
     * Đếm số lần mượn của mỗi thiết bị (chỉ đếm action = "Mượn")
     * Trả về danh sách Object[]: [deviceId, deviceName, count]
     * Dùng cho Top 10 thiết bị được mượn nhiều nhất
     */
    @Query("""
            select dh.device.deviceId, dh.device.deviceName, count(dh.id) as borrowCount
            from DeviceHistoryEntity dh
            where dh.action = 'Mượn'
            group by dh.device.deviceId, dh.device.deviceName
            order by count(dh.id) desc
            """)
    List<Object[]> countBorrowsByDevice();
}