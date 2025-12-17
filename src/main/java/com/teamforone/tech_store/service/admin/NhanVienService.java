package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.dto.request.NhanVienUpdateRequest;
import com.teamforone.tech_store.dto.request.RegisterRequest;
import com.teamforone.tech_store.dto.response.NhanVienResponse;
import com.teamforone.tech_store.dto.response.Response;

public interface NhanVienService {
    Response createNhanVien(RegisterRequest request);
    Response updateNhanVien(String id, NhanVienUpdateRequest request);

}
