package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.model.PaymentMethod;
import com.teamforone.tech_store.repository.admin.PaymentMethodRepository;
import com.teamforone.tech_store.service.admin.PaymentMethodService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {
    private final PaymentMethodRepository repo;

    public PaymentMethodServiceImpl(PaymentMethodRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<PaymentMethod> list() {
        return repo.findAll();
    }

    @Override
    public PaymentMethod save(PaymentMethod paymentMethod) {
        return repo.save(paymentMethod);
    }

    @Override
    public void delete(String id) {
        repo.deleteById(id);
    }
}
