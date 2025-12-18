package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.model.ShippingMethod;
import com.teamforone.tech_store.repository.admin.ShippingMethodRepository;
import com.teamforone.tech_store.service.admin.ShippingMethodService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingMethodServiceImpl implements ShippingMethodService {
    private final ShippingMethodRepository repo;

    public ShippingMethodServiceImpl(ShippingMethodRepository repo) {
        this.repo = repo;
    }


    @Override
    public List<ShippingMethod> list() {
        return repo.findAll();
    }

    @Override
    public ShippingMethod save(ShippingMethod shippingMethod) {
        return repo.save(shippingMethod);
    }

    @Override
    public void delete(String id) {
        repo.deleteById(id);
    }
}
