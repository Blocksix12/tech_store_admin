package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.UserRequest;
import com.teamforone.tech_store.model.User;
import com.teamforone.tech_store.repository.admin.crud.UserRepository;
import com.teamforone.tech_store.service.admin.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    // ✅ BỎ @Autowired trên field
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ CHỈ CẦN @Autowired Ở ĐÂY (hoặc bỏ cũng được vì Spring 4.3+)
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));
    }

    @Override
    public User addUser(UserRequest request) {
        // Validate username unique
        Optional<User> existingByUsername = userRepository.findByUsername(request.getUsername());
        if (existingByUsername.isPresent()) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }

        // Validate email unique
        Optional<User> existingByEmail = userRepository.findByEmail(request.getEmail());
        if (existingByEmail.isPresent()) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        // Validate phone unique
        Optional<User> existingByPhone = userRepository.findByPhone(request.getPhone());
        if (existingByPhone.isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng");
        }

        // Validate password
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername().trim());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setFullname(request.getFullname().trim());
        newUser.setEmail(request.getEmail().trim());
        newUser.setPhone(request.getPhone().trim());
        newUser.setStatus(request.getStatus() != null ?
                User.Status.valueOf(request.getStatus()) : User.Status.ACTIVE);

        return userRepository.save(newUser);
    }

    @Override
    public User updateUser(String id, UserRequest request) {
        User existingUser = findUserById(id);

        // Check username unique (except current user)
        Optional<User> userByUsername = userRepository.findByUsername(request.getUsername());
        if (userByUsername.isPresent() && !userByUsername.get().getId().equals(id)) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }

        // Check email unique (except current user)
        Optional<User> userByEmail = userRepository.findByEmail(request.getEmail());
        if (userByEmail.isPresent() && !userByEmail.get().getId().equals(id)) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        // Check phone unique (except current user)
        Optional<User> userByPhone = userRepository.findByPhone(request.getPhone());
        if (userByPhone.isPresent() && !userByPhone.get().getId().equals(id)) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng");
        }

        existingUser.setUsername(request.getUsername().trim());
        existingUser.setFullname(request.getFullname().trim());
        existingUser.setEmail(request.getEmail().trim());
        existingUser.setPhone(request.getPhone().trim());

        // Update password only if provided
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Update status
        if (request.getStatus() != null) {
            existingUser.setStatus(User.Status.valueOf(request.getStatus()));
        }

        return userRepository.save(existingUser);
    }

    @Override
    public void lockUser(String id) {
        User user = findUserById(id);
        user.setStatus(User.Status.LOCKED);
        userRepository.save(user);
    }

    @Override
    public void unlockUser(String id) {
        User user = findUserById(id);
        user.setStatus(User.Status.ACTIVE);
        userRepository.save(user);
    }

    @Override
    public long countActiveUsers() {
        return userRepository.countByStatus(User.Status.ACTIVE);
    }

    @Override
    public long countLockedUsers() {
        return userRepository.countByStatus(User.Status.LOCKED);
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