package com.teamforone.tech_store.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @UuidGenerator
    private String id;

    private String username;
    private String action;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime time;
    private String ipAddress;
    private String userAgent;

    @PrePersist
    public void prePersist() {
        if (time == null) time = LocalDateTime.now();
    }
}
