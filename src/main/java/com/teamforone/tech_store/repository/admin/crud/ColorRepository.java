package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color, String> {
    Optional<Color> findByColorName(String colorName);
    boolean existsByColorName(String colorName);
}