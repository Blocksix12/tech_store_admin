package com.teamforone.tech_store.repository.admin;

import com.teamforone.tech_store.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
}
