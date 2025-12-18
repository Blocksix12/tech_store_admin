package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.dto.request.UserRequest;
import com.teamforone.tech_store.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    User findUserById(String id);

    User addUser(UserRequest request);

    User updateUser(String id, UserRequest request);

    void lockUser(String id);

    void unlockUser(String id);

    long countActiveUsers();

    long countLockedUsers();
}
