package com.example.ResourcesManagement.viewController;


import com.example.ResourcesManagement.DTO.request.ChapterRequestDTO;
import com.example.ResourcesManagement.DTO.response.ChapterResponseDTO;
import com.example.ResourcesManagement.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


@Controller
public class ViewChapterController {

    @Autowired
    ChapterService chapterService;
    // lấy danh sách chapter
    @GetMapping("/viewDepartments")
    public String viewChapters(Model model) {
        List<ChapterResponseDTO> chapters = chapterService.getChapters();

        model.addAttribute("chapters" , chapters);
        return "admin-chapters";
    }

    // thêm chapter
    @GetMapping("/chapter/add")
    public String showAddChapterForm(Model model) {
        model.addAttribute("chapterRequest", new ChapterRequestDTO());
        return "admin-chapter-create";
    }
    @PostMapping("/chapter/add")
    public String saveChapter(@ModelAttribute("chapterRequest") ChapterRequestDTO chapterRequest) {
        chapterService.addChapter(chapterRequest);
        return "redirect:/viewDepartments";
    }

    //sửa chapter
    @GetMapping("/chapters/edit/{id}")
    public String showEditChapterForm(@PathVariable Long id, Model model) {
        ChapterResponseDTO chapterResponse = chapterService.getChapterById(id);
        
        // Convert to ChapterRequestDTO for form
        ChapterRequestDTO chapterRequest = ChapterRequestDTO.builder()
                .name(chapterResponse.getName())
                .description(chapterResponse.getDescription())
                .build();
        
        model.addAttribute("chapterRequest", chapterRequest);
        model.addAttribute("chapterId", id); // Đánh dấu là đang sửa ID này
        return "admin-chapter-create";
    }
    // 5. Xử lý Cập nhật
    @PostMapping("/chapters/edit/{id}")
    public String updateChapter(@PathVariable Long id, @ModelAttribute("chapterRequest") ChapterRequestDTO chapterRequest) {
        // Lưu ý: Hàm updateChapter trong Service của bạn đang nhận ChapterResponseDTO
        // Nếu bạn muốn chuẩn hơn thì nên sửa Service nhận ChapterRequestDTO
        chapterService.updateChapter(id, chapterRequest);
        return "redirect:/viewDepartments";
    }

    // xóa chapter
    @GetMapping("/chapter/delete/{id}")
    public String deleteChapter(@PathVariable Long id) {
        chapterService.deleteChapter(id);
        return "redirect:/viewDepartments";
    }
}
