package com.teamforone.tech_store.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
public class Roles {
    @Id
    @UuidGenerator
    @Column(name = "role_id", columnDefinition = "CHAR(36)")
    private String roleID;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", nullable = false)
    private RoleName roleName;

    @Column(name = "description")
    private String description;
//    @ManyToMany(mappedBy = "roles")
//    private Set<NhanVien> nhanViens;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id")
    )
    private Set<Permission> permissions;

    public enum RoleName {
        ADMIN,
        STAFF,
        MANAGER;

        public static RoleName toEnum(String value) {
            for (RoleName item : values()) {
                if (item.toString().equalsIgnoreCase(value)) return item;
            }
            return null;
        }
    }
}
