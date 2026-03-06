package com.example.usermgmt.controller;

import com.example.usermgmt.dto.AuthDtos;
import com.example.usermgmt.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthDtos.RegisterResponse register(@Valid @RequestBody AuthDtos.RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public AuthDtos.TokenResponse login(@Valid @RequestBody AuthDtos.LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/refresh")
    public AuthDtos.TokenResponse refresh(@Valid @RequestBody AuthDtos.RefreshRequest req) {
        return authService.refresh(req);
    }

    @PostMapping("/logout")
    public AuthDtos.StatusResponse logout(@Valid @RequestBody AuthDtos.LogoutRequest req) {
        return authService.logout(req);
    }

    @PostMapping("/change-password")
    public AuthDtos.StatusResponse changePassword(@Valid @RequestBody AuthDtos.ChangePasswordRequest req) {
        return authService.changePassword(req);
    }

    @PostMapping("/reset-request")
    public AuthDtos.ResetRequestResponse resetRequest(@Valid @RequestBody AuthDtos.ResetRequest req) {
        return authService.resetRequest(req);
    }

    @PostMapping("/reset-confirm")
    public AuthDtos.StatusResponse resetConfirm(@Valid @RequestBody AuthDtos.ResetConfirmRequest req) {
        return authService.resetConfirm(req);
    }

    @PostMapping("/recovery/encrypt")
    public AuthDtos.RecoveryEncryptResponse encrypt(@Valid @RequestBody AuthDtos.RecoveryEncryptRequest req) {
        return authService.encryptRecovery(req);
    }
}
