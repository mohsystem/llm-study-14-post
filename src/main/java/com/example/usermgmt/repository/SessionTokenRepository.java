package com.example.usermgmt.repository;

import com.example.usermgmt.entity.SessionToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionTokenRepository extends JpaRepository<SessionToken, String> {
    Optional<SessionToken> findByTokenAndActiveTrue(String token);
}
