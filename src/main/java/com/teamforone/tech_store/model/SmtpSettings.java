package com.teamforone.tech_store.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "settings_smtp")
public class SmtpSettings {
    @Id
    @UuidGenerator
    private String id;

    private String host;
    private Integer port;
    private String senderEmail;
    private String senderName;
    private String username;
    private String password;
    private String secureType;
}
