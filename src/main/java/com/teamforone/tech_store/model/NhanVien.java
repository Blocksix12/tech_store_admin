package com.teamforone.tech_store.model;

import com.teamforone.tech_store.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "nhanvien")
public class NhanVien implements UserDetails {
    @Id
    @UuidGenerator
    @Column(name = "nhanvienID", columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "website")
    private String website;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('ACTIVE','LOCKED') DEFAULT 'ACTIVE'")
    private Status status = Status.ACTIVE;

    @Column(name = "access_token", length = 1000)
    private String accessToken;


    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "nhanvien_roles",
            joinColumns = @JoinColumn(name = "nhanvien_id", referencedColumnName = "nhanvienID"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    )
    private Set<Roles> roles;
    
    @Column(name = "avatar")
    private String avatarUrl;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Thêm roles
        roles.forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName().name()));

            // Thêm permissions của từng role
            role.getPermissions().forEach(permission -> {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            });
        });

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public enum Status {
        ACTIVE,
        LOCKED;

        private static Status toEnum(String status) {
            for (Status item : values()) {
                if (item.toString().equalsIgnoreCase(status)) return item;
            }
            return null;
        }
    }

    public String getRolesDisplay() {
        if (roles == null || roles.isEmpty()) {
            return "Nhân viên";
        }
        return roles.stream()
                .map(role -> {
                    if (role.getRoleName() == null) return "Chưa xác định";
                    switch (role.getRoleName()) {
                        case ADMIN: return "Quản trị viên";
                        case MANAGER: return "Quản lý";
                        case STAFF: return "Nhân viên";
                        default: return role.getRoleName().name();
                    }
                })
                .collect(Collectors.joining(", "));
    }

    /**
     * Lấy danh sách tên roles gốc (ADMIN, STAFF, MANAGER)
     */
    public String getRolesName() {
        if (roles == null || roles.isEmpty()) {
            return "STAFF";
        }
        return roles.stream()
                .map(role -> role.getRoleName() != null ? role.getRoleName().name() : "UNKNOWN")
                .collect(Collectors.joining(", "));
    }

    /**
     * Kiểm tra có phải Admin không
     */
    public boolean isAdmin() {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.stream()
                .anyMatch(role -> role.getRoleName() == Roles.RoleName.ADMIN);
    }

    /**
     * Kiểm tra có phải Manager không
     */
    public boolean isManager() {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.stream()
                .anyMatch(role -> role.getRoleName() == Roles.RoleName.MANAGER);
    }

    /**
     * Kiểm tra có phải Staff không
     */
    public boolean isStaff() {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.stream()
                .anyMatch(role -> role.getRoleName() == Roles.RoleName.STAFF);
    }
}
