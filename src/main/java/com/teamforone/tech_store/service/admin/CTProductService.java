package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.dto.request.CTProductRequest;

import java.util.List;

public interface CTProductService {
    List<CTProductRequest> getAllProduct();
}
