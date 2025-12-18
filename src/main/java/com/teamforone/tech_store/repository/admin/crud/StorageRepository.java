package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StorageRepository extends JpaRepository<Storage, String> {

    // Tìm storage theo RAM và ROM (an toàn)
    Optional<Storage> findByRamAndRom(String ram, String rom);

    // Kiểm tra storage có tồn tại không
    boolean existsByRamAndRom(String ram, String rom);

    // Lấy danh sách storage, sắp xếp theo RAM
    List<Storage> findAllByOrderByRamAsc();

    // Nếu bạn thực sự cần sắp xếp theo ROM
    List<Storage> findAllByOrderByRomAsc();
}
