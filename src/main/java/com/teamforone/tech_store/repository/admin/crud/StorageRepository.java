package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageRepository extends JpaRepository<Storage, String> {
    // Lấy tất cả storage, sắp xếp theo ROM
    @Query("SELECT s FROM Storage s ORDER BY s.rom ASC")
    List<Storage> findAllOrderByRom();

    // Tìm storage theo RAM và ROM
    Storage findByRamAndRom(String ram, String rom);

    // Kiểm tra storage có tồn tại không
    boolean existsByRamAndRom(String ram, String rom);
}
