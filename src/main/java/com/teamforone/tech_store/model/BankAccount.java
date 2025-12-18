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
@Table(name = "settings_bank_accounts")
public class BankAccount {
    @Id
    @UuidGenerator
    private String id;

    private String bankName;
    private String accountNumber;
    private String ownerName;
    private String logoUrl;
}
