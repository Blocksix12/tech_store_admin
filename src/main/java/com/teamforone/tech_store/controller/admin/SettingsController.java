package com.teamforone.tech_store.controller.admin;

import com.teamforone.tech_store.model.*;
import com.teamforone.tech_store.service.admin.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/settings")
public class SettingsController {
    private final SettingsGeneralService generalService;
    private final ShippingMethodService shippingService;
    private final PaymentMethodService paymentService;
    private final BankAccountService bankService;
    private final SmtpSettingsService smtpService;
    private final AuditLogService auditService;

    public SettingsController(SettingsGeneralService generalService,
                              ShippingMethodService shippingService,
                              PaymentMethodService paymentService,
                              BankAccountService bankService,
                              SmtpSettingsService smtpService,
                              AuditLogService auditService) {
        this.generalService = generalService;
        this.shippingService = shippingService;
        this.paymentService = paymentService;
        this.bankService = bankService;
        this.smtpService = smtpService;
        this.auditService = auditService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("general", generalService.get());
        model.addAttribute("shippingList", shippingService.list());
        model.addAttribute("paymentList", paymentService.list());
        model.addAttribute("banks", bankService.list());
        model.addAttribute("smtp", smtpService.get());
        model.addAttribute("audits", auditService.list());
        return "admin/settings";
    }

    @PostMapping("/general")
    public String saveGeneral(@ModelAttribute SettingsGeneral general) {
        generalService.save(general);
        return "redirect:/admin/settings?tab=general&success";
    }

    @PostMapping("/shipping")
    public String saveShipping(@ModelAttribute ShippingMethod m) {
        shippingService.save(m);
        return "redirect:/admin/settings?tab=shipping&success";
    }

    @PostMapping("/payment")
    public String savePayment(@ModelAttribute PaymentMethod p) {
        paymentService.save(p);
        return "redirect:/admin/settings?tab=payment&success";
    }

    @PostMapping("/bank")
    public String saveBank(@ModelAttribute BankAccount b) {
        bankService.save(b);
        return "redirect:/admin/settings?tab=payment&success";
    }

    @PostMapping("/smtp")
    public String saveSmtp(@ModelAttribute SmtpSettings s) {
        smtpService.save(s);
        return "redirect:/admin/settings?tab=email&success";
    }

    @PostMapping("/audit/clear")
    public String clearAudit() {
        auditService.clearAll();
        return "redirect:/admin/settings?tab=audit-logs&cleared";
    }
}
