package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
<<<<<<< HEAD
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StorageRepository extends JpaRepository<Storage, String> {
    Optional<Storage> findByRamAndRom(String ram, String rom);

    @Query("SELECT s FROM Storage s WHERE s.ram = :ram AND s.rom = :rom")
    Optional<Storage> findByRamRom(@Param("ram") String ram, @Param("rom") String rom);

    List<Storage> findAllByOrderByRamAsc();
=======
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
>>>>>>> a4a014af87307103265b91d7afcd2e53131a4ebb
}
