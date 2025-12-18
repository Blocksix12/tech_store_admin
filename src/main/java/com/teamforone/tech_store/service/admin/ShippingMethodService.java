package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.model.ShippingMethod;

import java.util.List;

public interface ShippingMethodService {
    List<ShippingMethod> list();
    ShippingMethod save(ShippingMethod shippingMethod);
    void delete(String id);
}
