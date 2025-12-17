package com.teamforone.tech_store.service.admin.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Tạo tên file unique
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // Tạo thư mục nếu chưa tồn tại
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/images/" + fileName; // Trả về đường dẫn tương đối
    }
    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            // Lấy tên file từ URL (bỏ phần /images/)
            String fileName = fileUrl.replace("/images/", "");

            // Tạo đường dẫn đầy đủ
            Path filePath = Paths.get(uploadDir).resolve(fileName);

            // Xóa file nếu tồn tại
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("Đã xóa file: " + fileName);
            } else {
                System.out.println("File không tồn tại: " + fileName);
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi xóa file: " + e.getMessage());
            throw e;
        }
    }

    // ✅ THÊM METHOD KIỂM TRA FILE TỒN TẠI
    public boolean fileExists(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        try {
            String fileName = fileUrl.replace("/images/", "");
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            return Files.exists(filePath);
        } catch (Exception e) {
            return false;
        }
    }

}
