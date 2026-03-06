package com.example.usermgmt.service;

import com.example.usermgmt.dto.AdminDtos;
import com.example.usermgmt.entity.AppUser;
import com.example.usermgmt.entity.PasswordPolicy;
import com.example.usermgmt.exception.ApiException;
import com.example.usermgmt.repository.PasswordPolicyRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasswordPolicyService {
    private final PasswordPolicyRepository repository;
    private final PasswordEncoder passwordEncoder;

    public PasswordPolicyService(PasswordPolicyRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public PasswordPolicy getActive() {
        return repository.findById(1L).orElseGet(() -> repository.save(new PasswordPolicy()));
    }

    public AdminDtos.PasswordPolicyResponse getPolicyResponse() {
        PasswordPolicy p = getActive();
        return new AdminDtos.PasswordPolicyResponse(p.getMinLength(), p.isRequireUppercase(), p.isRequireLowercase(),
                p.isRequireDigit(), p.isRequireSpecial(), p.getHistoryDepth());
    }

    public AdminDtos.PasswordPolicyResponse update(AdminDtos.PasswordPolicyRequest req) {
        PasswordPolicy p = getActive();
        p.setMinLength(req.minLength());
        p.setRequireUppercase(req.requireUppercase());
        p.setRequireLowercase(req.requireLowercase());
        p.setRequireDigit(req.requireDigit());
        p.setRequireSpecial(req.requireSpecial());
        p.setHistoryDepth(req.historyDepth());
        repository.save(p);
        return getPolicyResponse();
    }

    public void validatePasswordAgainstPolicy(String password, AppUser user) {
        PasswordPolicy policy = getActive();
        if (password.length() < policy.getMinLength()) {
            throw new ApiException("VALIDATION_FAILED", "Password does not meet policy requirements");
        }
        if (policy.isRequireUppercase() && password.chars().noneMatch(Character::isUpperCase)) fail();
        if (policy.isRequireLowercase() && password.chars().noneMatch(Character::isLowerCase)) fail();
        if (policy.isRequireDigit() && password.chars().noneMatch(Character::isDigit)) fail();
        if (policy.isRequireSpecial() && password.chars().allMatch(Character::isLetterOrDigit)) fail();

        if (user != null && policy.getHistoryDepth() > 0) {
            List<String> history = user.getPasswordHistory();
            int start = Math.max(0, history.size() - policy.getHistoryDepth());
            for (String oldHash : history.subList(start, history.size())) {
                if (passwordEncoder.matches(password, oldHash)) {
                    throw new ApiException("VALIDATION_FAILED", "Password was used recently");
                }
            }
        }
    }

    private void fail() {
        throw new ApiException("VALIDATION_FAILED", "Password does not meet policy requirements");
    }
}
