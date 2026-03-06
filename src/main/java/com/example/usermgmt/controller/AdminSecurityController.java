package com.example.usermgmt.controller;

import com.example.usermgmt.dto.AdminDtos;
import com.example.usermgmt.service.AdminSecurityService;
import com.example.usermgmt.service.PasswordPolicyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/security")
public class AdminSecurityController {
    private final PasswordPolicyService passwordPolicyService;
    private final AdminSecurityService adminSecurityService;

    public AdminSecurityController(PasswordPolicyService passwordPolicyService, AdminSecurityService adminSecurityService) {
        this.passwordPolicyService = passwordPolicyService;
        this.adminSecurityService = adminSecurityService;
    }

    @PutMapping("/password-policy")
    public AdminDtos.PasswordPolicyResponse updatePolicy(@Valid @RequestBody AdminDtos.PasswordPolicyRequest req) {
        return passwordPolicyService.update(req);
    }

    @GetMapping("/password-policy")
    public AdminDtos.PasswordPolicyResponse getPolicy() {
        return passwordPolicyService.getPolicyResponse();
    }

    @PostMapping("/hash/preview")
    public AdminDtos.HashPreviewResponse hashPreview(@Valid @RequestBody AdminDtos.HashPreviewRequest req) {
        return adminSecurityService.hashPreview(req);
    }
}
