package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
