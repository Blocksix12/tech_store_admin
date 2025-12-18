package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color, String> {

    // Lấy tất cả màu sắc, sắp xếp theo tên
    List<Color> findAllByOrderByColorNameAsc();

    // Tìm màu theo tên (an toàn)
    Optional<Color> findByColorName(String colorName);

    // Kiểm tra màu có tồn tại không
    boolean existsByColorName(String colorName);
}
