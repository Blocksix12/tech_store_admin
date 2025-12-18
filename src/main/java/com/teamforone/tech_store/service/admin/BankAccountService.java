package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.model.BankAccount;

import java.util.List;

public interface BankAccountService {
    List<BankAccount> list();
    BankAccount save(BankAccount b);
    void delete(String id);
}
