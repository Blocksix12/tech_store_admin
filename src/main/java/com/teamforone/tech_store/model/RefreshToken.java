package com.teamforone.tech_store.model;

import com.teamforone.tech_store.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;
import org.apache.xmlbeans.XmlCursor;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private String userId;
    private String nhanVienId;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType; // USER, STAFF, MANAGER, ADMIN

    private Instant expiryDate;

    private boolean revoked;
}
