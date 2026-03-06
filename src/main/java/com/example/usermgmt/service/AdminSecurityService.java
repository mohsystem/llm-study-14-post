package com.example.usermgmt.service;

import com.example.usermgmt.dto.AdminDtos;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminSecurityService {
    private final PasswordEncoder passwordEncoder;

    public AdminSecurityService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public AdminDtos.HashPreviewResponse hashPreview(AdminDtos.HashPreviewRequest req) {
        return new AdminDtos.HashPreviewResponse(passwordEncoder.encode(req.password()), "bcrypt");
    }
}
