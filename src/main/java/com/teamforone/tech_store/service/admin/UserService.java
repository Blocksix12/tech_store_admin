package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.dto.request.UserRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(String id);
    Response createUser(UserRequest request);
    Response updateUser(String id, UserRequest request);
    Response deleteUser(String id);
}
