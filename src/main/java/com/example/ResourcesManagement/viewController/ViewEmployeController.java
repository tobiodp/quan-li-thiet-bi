package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.entity.ChapterEntity;
import com.example.ResourcesManagement.entity.UserEntity;
import com.example.ResourcesManagement.service.ChapterService;
import com.example.ResourcesManagement.service.RequestDeviceService;
import com.example.ResourcesManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ViewEmployeController {

    // lấy danh sách nhân viên
    @Autowired
    UserService userService;

    @Autowired
    RequestDeviceService requestDeviceService;

    @Autowired
    ChapterService chapterService;

    @GetMapping("/viewEmployees") // cái api ni ch config trong security để ai cũng truy cập đc
    public String viewEmployees(Model model) {

        long requestDeviceCount = requestDeviceService.countRequestDevices();
        model.addAttribute("requestDeviceCount", requestDeviceCount);
        model.addAttribute("employees", userService.getListUser());
        return "admin-employees";
    }


    // --- 2. THÊM NHÂN VIÊN (ADD) ---

    // Hiển thị Form thêm mới (GET)
    @GetMapping("/employees/add")
    public String showAddEmployeeForm(Model model) {
        // Tạo đối tượng User rỗng
        model.addAttribute("userRequest", new UserEntity());
        // lấy danh sách chapter để chọn khi tạo nhân viên
        model.addAttribute("chapter" , chapterService.getChapters());

        return "admin-employee-create"; // Trả về file HTML admin-employee-create
    }

    // Xử lý dữ liệu thêm mới (POST)
    @PostMapping("/employees/add")
    public String addEmployee(
            @ModelAttribute("userRequest") UserEntity userRequest,
            // 👇 THÊM DÒNG NÀY: Để hứng cái name="chapterId" từ HTML
            @RequestParam(value = "chapterId", required = false) Long chapterId
    ) {

        // 1. Kiểm tra nếu có chọn phòng ban
        if (chapterId != null) {
            // 2. Tìm Chapter trong DB (Phải lấy ra Entity, không phải DTO)
            ChapterEntity chapter = chapterService.findChapterById(chapterId);

            // 3. Gán Chapter vào User
            userRequest.setChapter(chapter);
        } else {
            userRequest.setChapter(null);
        }

        // 4. Lưu User
        userService.createUser(userRequest);

        return "redirect:/viewEmployees";
    }

    // --- 3. SỬA NHÂN VIÊN (EDIT) ---

    // Hiển thị Form sửa (GET)
    @GetMapping("/employees/edit/{id}")
    public String showEditEmployeeForm(@PathVariable Long id, Model model) {
        // Lấy thông tin user cũ
        UserEntity user = userService.findUserEntityById(id);
        model.addAttribute("userRequest", user);
        model.addAttribute("userId", id); // Gửi ID sang để Form biết là đang Sửa
        return "admin-employee-create"; // Dùng chung file view với Thêm mới
    }

    // Xử lý dữ liệu sửa (POST)
    @PostMapping("/employees/edit/{id}")
    public String editEmployee(@PathVariable Long id, @ModelAttribute("userRequest") UserEntity userRequest) {
        userRequest.setId(id); // Set ID để update
        userService.createUser(userRequest); // Reuse create method (will update if ID exists)
        return "redirect:/viewEmployees";
    }

    // --- 4. XÓA NHÂN VIÊN ---
    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/viewEmployees";
    }




}
