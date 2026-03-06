package com.example.usermgmt.service;

import com.example.usermgmt.dto.AuthDtos;
import com.example.usermgmt.entity.AppUser;
import com.example.usermgmt.entity.PasswordResetToken;
import com.example.usermgmt.entity.SessionToken;
import com.example.usermgmt.exception.ApiException;
import com.example.usermgmt.repository.AppUserRepository;
import com.example.usermgmt.repository.PasswordResetTokenRepository;
import com.example.usermgmt.repository.SessionTokenRepository;
import com.example.usermgmt.util.AesEncryptionUtil;
import com.example.usermgmt.util.TokenGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {
    private final AppUserRepository userRepository;
    private final SessionTokenRepository sessionTokenRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicyService passwordPolicyService;
    private final TokenGenerator tokenGenerator;
    private final AesEncryptionUtil aesEncryptionUtil;

    public AuthService(AppUserRepository userRepository, SessionTokenRepository sessionTokenRepository,
                       PasswordResetTokenRepository resetTokenRepository, PasswordEncoder passwordEncoder,
                       PasswordPolicyService passwordPolicyService, TokenGenerator tokenGenerator,
                       AesEncryptionUtil aesEncryptionUtil) {
        this.userRepository = userRepository;
        this.sessionTokenRepository = sessionTokenRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicyService = passwordPolicyService;
        this.tokenGenerator = tokenGenerator;
        this.aesEncryptionUtil = aesEncryptionUtil;
    }

    @Transactional
    public AuthDtos.RegisterResponse register(AuthDtos.RegisterRequest req) {
        if (userRepository.existsByUsernameIgnoreCase(req.username()) || userRepository.existsByEmailIgnoreCase(req.email())) {
            throw new ApiException("DUPLICATE_ACCOUNT", "Username or email already exists");
        }
        passwordPolicyService.validatePasswordAgainstPolicy(req.password(), null);
        AppUser user = new AppUser();
        user.setUsername(req.username());
        user.setEmail(req.email());
        String hash = passwordEncoder.encode(req.password());
        user.setPasswordHash(hash);
        user.getPasswordHistory().add(hash);
        AppUser saved = userRepository.save(user);
        return new AuthDtos.RegisterResponse(saved.getId(), "REGISTERED");
    }

    @Transactional
    public AuthDtos.TokenResponse login(AuthDtos.LoginRequest req) {
        AppUser user = findByIdentity(req.login())
                .orElseThrow(() -> new ApiException("UNAUTHORIZED", "Invalid credentials"));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new ApiException("UNAUTHORIZED", "Invalid credentials");
        }
        SessionToken token = new SessionToken();
        token.setToken(tokenGenerator.tokenHex(24));
        token.setUser(user);
        sessionTokenRepository.save(token);
        return new AuthDtos.TokenResponse(token.getToken(), "AUTHENTICATED");
    }

    @Transactional
    public AuthDtos.TokenResponse refresh(AuthDtos.RefreshRequest req) {
        SessionToken token = sessionTokenRepository.findByTokenAndActiveTrue(req.token())
                .orElseThrow(() -> new ApiException("UNAUTHORIZED", "Invalid session token"));
        token.setActive(false);
        SessionToken newToken = new SessionToken();
        newToken.setToken(tokenGenerator.tokenHex(24));
        newToken.setUser(token.getUser());
        sessionTokenRepository.save(newToken);
        return new AuthDtos.TokenResponse(newToken.getToken(), "REFRESHED");
    }

    @Transactional
    public AuthDtos.StatusResponse logout(AuthDtos.LogoutRequest req) {
        SessionToken token = sessionTokenRepository.findByTokenAndActiveTrue(req.token())
                .orElseThrow(() -> new ApiException("UNAUTHORIZED", "Invalid session token"));
        token.setActive(false);
        return new AuthDtos.StatusResponse("LOGGED_OUT");
    }

    @Transactional
    public AuthDtos.StatusResponse changePassword(AuthDtos.ChangePasswordRequest req) {
        SessionToken token = sessionTokenRepository.findByTokenAndActiveTrue(req.token())
                .orElseThrow(() -> new ApiException("UNAUTHORIZED", "Invalid session token"));
        AppUser user = token.getUser();
        if (!passwordEncoder.matches(req.currentPassword(), user.getPasswordHash())) {
            throw new ApiException("UNAUTHORIZED", "Current password does not match");
        }
        passwordPolicyService.validatePasswordAgainstPolicy(req.newPassword(), user);
        String newHash = passwordEncoder.encode(req.newPassword());
        user.setPasswordHash(newHash);
        user.getPasswordHistory().add(newHash);
        userRepository.save(user);
        return new AuthDtos.StatusResponse("PASSWORD_CHANGED");
    }

    @Transactional
    public AuthDtos.ResetRequestResponse resetRequest(AuthDtos.ResetRequest req) {
        AppUser user = findByIdentity(req.identity())
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Account not found"));
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(tokenGenerator.tokenHex(24));
        token.setUser(user);
        resetTokenRepository.save(token);
        return new AuthDtos.ResetRequestResponse("RESET_REQUESTED", token.getToken());
    }

    @Transactional
    public AuthDtos.StatusResponse resetConfirm(AuthDtos.ResetConfirmRequest req) {
        PasswordResetToken token = resetTokenRepository.findByTokenAndUsedFalse(req.resetToken())
                .orElseThrow(() -> new ApiException("UNAUTHORIZED", "Invalid reset token"));
        AppUser user = token.getUser();
        passwordPolicyService.validatePasswordAgainstPolicy(req.newPassword(), user);
        String newHash = passwordEncoder.encode(req.newPassword());
        user.setPasswordHash(newHash);
        user.getPasswordHistory().add(newHash);
        userRepository.save(user);
        token.setUsed(true);
        return new AuthDtos.StatusResponse("PASSWORD_RESET");
    }

    public AuthDtos.RecoveryEncryptResponse encryptRecovery(AuthDtos.RecoveryEncryptRequest req) {
        return new AuthDtos.RecoveryEncryptResponse(aesEncryptionUtil.encrypt(req.recoverySecret()));
    }

    private Optional<AppUser> findByIdentity(String identity) {
        if (identity.contains("@")) {
            return userRepository.findByEmailIgnoreCase(identity);
        }
        return userRepository.findByUsernameIgnoreCase(identity);
    }
}
