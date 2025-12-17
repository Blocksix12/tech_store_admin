package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.UserRequest;
import com.teamforone.tech_store.model.User;
import com.teamforone.tech_store.repository.admin.UserRepository;
import com.teamforone.tech_store.service.admin.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
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
    }
}