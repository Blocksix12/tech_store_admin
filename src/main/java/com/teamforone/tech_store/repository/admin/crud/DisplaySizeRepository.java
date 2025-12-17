package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.DisplaySize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DisplaySizeRepository extends JpaRepository<DisplaySize, String> {
    // Lấy tất cả display size, sắp xếp theo size
    List<DisplaySize> findAllByOrderBySizeInchAsc();

    // Tìm theo size inch
    DisplaySize findBySizeInch(BigDecimal sizeInch);

    // Kiểm tra size có tồn tại không
    boolean existsBySizeInch(BigDecimal sizeInch);
}
