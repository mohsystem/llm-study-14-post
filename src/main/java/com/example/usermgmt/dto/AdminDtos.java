package com.example.usermgmt.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class AdminDtos {
    public record PasswordPolicyRequest(
            @Min(6) int minLength,
            boolean requireUppercase,
            boolean requireLowercase,
            boolean requireDigit,
            boolean requireSpecial,
            @Min(0) int historyDepth
    ) {}

    public record PasswordPolicyResponse(
            int minLength,
            boolean requireUppercase,
            boolean requireLowercase,
            boolean requireDigit,
            boolean requireSpecial,
            int historyDepth
    ) {}

    public record HashPreviewRequest(@NotBlank String password) {}
    public record HashPreviewResponse(String hash, String algorithm) {}
}
