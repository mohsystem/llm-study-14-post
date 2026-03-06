package com.example.usermgmt.dto;

import jakarta.validation.constraints.*;

public class AuthDtos {
    public record RegisterRequest(
            @NotBlank String username,
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    public record RegisterResponse(Long userId, String status) {}

    public record LoginRequest(
            @NotBlank String login,
            @NotBlank String password
    ) {}

    public record TokenResponse(String token, String status) {}

    public record RefreshRequest(@NotBlank @Pattern(regexp = "^[a-fA-F0-9]{32,128}$") String token) {}

    public record LogoutRequest(@NotBlank @Pattern(regexp = "^[a-fA-F0-9]{32,128}$") String token) {}

    public record ChangePasswordRequest(
            @NotBlank @Pattern(regexp = "^[a-fA-F0-9]{32,128}$") String token,
            @NotBlank String currentPassword,
            @NotBlank String newPassword
    ) {}

    public record ResetRequest(@NotBlank String identity) {}

    public record ResetRequestResponse(String status, String resetToken) {}

    public record ResetConfirmRequest(
            @NotBlank @Pattern(regexp = "^[a-fA-F0-9]{32,128}$") String resetToken,
            @NotBlank String newPassword
    ) {}

    public record StatusResponse(String status) {}

    public record RecoveryEncryptRequest(@NotBlank String recoverySecret) {}
    public record RecoveryEncryptResponse(String encryptedValue) {}
}
