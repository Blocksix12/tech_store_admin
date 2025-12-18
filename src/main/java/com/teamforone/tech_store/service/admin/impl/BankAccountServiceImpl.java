package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.model.BankAccount;
import com.teamforone.tech_store.repository.admin.BankAccountRepository;
import com.teamforone.tech_store.service.admin.BankAccountService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountRepository repo;

    public BankAccountServiceImpl(BankAccountRepository repo) {
        this.repo = repo;
    }


    @Override
    public List<BankAccount> list() {
        return repo.findAll();
    }

    @Override
    public BankAccount save(BankAccount b) {
        return repo.save(b);
    }

    @Override
    public void delete(String id) {
        repo.deleteById(id);
    }
}
