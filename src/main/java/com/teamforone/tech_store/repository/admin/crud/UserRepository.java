package com.teamforone.tech_store.repository.admin.crud;

import com.teamforone.tech_store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD:src/main/java/com/teamforone/tech_store/repository/admin/crud/UserRepository.java
=======
import java.util.Optional;
import java.util.UUID;

>>>>>>> a4a014af87307103265b91d7afcd2e53131a4ebb:src/main/java/com/teamforone/tech_store/repository/admin/UserRepository.java
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
}
