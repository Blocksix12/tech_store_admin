package com.teamforone.tech_store.controller.admin;

import com.teamforone.tech_store.model.ShippingMethod;
import com.teamforone.tech_store.service.admin.ShippingMethodService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {
    private final ShippingMethodService shippingService;
    public ShippingController(ShippingMethodService shippingService){ this.shippingService = shippingService; }

    @GetMapping
    public List<ShippingMethod> list(){ return shippingService.list(); }

    @PostMapping
    public ShippingMethod create(@RequestBody ShippingMethod m){ return shippingService.save(m); }
}
