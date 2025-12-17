package com.teamforone.tech_store.controller.admin;

import com.teamforone.tech_store.dto.request.NhanVienUpdateRequest;
import com.teamforone.tech_store.dto.request.RegisterRequest;
import com.teamforone.tech_store.dto.response.NhanVienResponse;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.service.admin.NhanVienService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/auth/admin")
public class NhanVienController {
    private final NhanVienService nhanVienService;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody @Valid RegisterRequest request) {
        Response response = nhanVienService.createNhanVien(request);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateNhanVien(@PathVariable String id, @RequestBody @Valid NhanVienUpdateRequest request) {
        Response response = nhanVienService.updateNhanVien(id, request);
        return ResponseEntity.status(response.getStatus())
                .body(response);
    }
}
