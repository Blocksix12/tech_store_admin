package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.UserRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.User;
import com.teamforone.tech_store.repository.admin.crud.UserRepository;
import com.teamforone.tech_store.service.admin.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public Response createUser(UserRequest request) {
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setFullname(request.getFullname());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());

            // Set status, default là ACTIVE nếu không có
            if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                user.setStatus(User.Status.valueOf(request.getStatus().toUpperCase()));
            } else {
                user.setStatus(User.Status.ACTIVE);
            }

            // TODO: Nên mã hóa password
            // user.setPassword(passwordEncoder.encode(defaultPassword));
            user.setPassword("123456"); // Password mặc định, nên đổi

            userRepository.save(user);

            return Response.builder()
                    .status(HttpStatus.CREATED.value())
                    .message("User created successfully")
                    .build();
        } catch (Exception e) {
            return Response.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Failed to create user: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response updateUser(String id, UserRequest request) {
        try {
            Optional<User> existingUserOpt = userRepository.findById(id);

            if (existingUserOpt.isEmpty()) {
                return Response.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message("User not found")
                        .build();
            }

            User existingUser = existingUserOpt.get();

            // Chỉ update những field không null và không rỗng


            if (request.getUsername() != null && !request.getUsername().isEmpty()) {
                existingUser.setUsername(request.getUsername());
            }

            if (request.getFullname() != null && !request.getFullname().isEmpty()) {
                existingUser.setFullname(request.getFullname());
            }

            if (request.getEmail() != null && !request.getEmail().isEmpty()) {
                existingUser.setEmail(request.getEmail());
            }

            if (request.getPhone() != null && !request.getPhone().isEmpty()) {
                existingUser.setPhone(request.getPhone());
            }

            // Update status nếu có
            if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                existingUser.setStatus(User.Status.valueOf(request.getStatus().toUpperCase()));
            }


            userRepository.save(existingUser);

            return Response.builder()
                    .status(HttpStatus.OK.value())
                    .message("User updated successfully")
                    .build();
        } catch (Exception e) {
            return Response.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Failed to update user: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response deleteUser(String id) {
        try {
            if (!userRepository.existsById(id)) {
                return Response.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message("User not found")
                        .build();
            }

            userRepository.deleteById(id);

            return Response.builder()
                    .status(HttpStatus.OK.value())
                    .message("User deleted successfully")
                    .build();
        } catch (Exception e) {
            return Response.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Failed to delete user: " + e.getMessage())
                    .build();
        }
    }
}