package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color, String> {
    Optional<Color> findByColorName(String colorName);
    boolean existsByColorName(String colorName);
}
=======
import java.util.List;

@Repository
public interface ColorRepository extends JpaRepository<Color, String> {

    // Lấy tất cả màu sắc, sắp xếp theo tên
    List<Color> findAllByOrderByColorNameAsc();

    // Tìm màu theo tên
    Color findByColorName(String colorName);

    // Kiểm tra màu có tồn tại không
    boolean existsByColorName(String colorName);
}
>>>>>>> a4a014af87307103265b91d7afcd2e53131a4ebb
