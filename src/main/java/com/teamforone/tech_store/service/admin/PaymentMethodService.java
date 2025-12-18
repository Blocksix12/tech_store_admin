package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.model.PaymentMethod;

import java.util.List;

public interface PaymentMethodService {
    List<PaymentMethod> list();
    PaymentMethod save(PaymentMethod paymentMethod);
    void delete(String id);
}
