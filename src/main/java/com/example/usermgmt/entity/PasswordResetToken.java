package com.example.usermgmt.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    @Column(length = 64)
    private String token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private boolean used = false;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public Instant getCreatedAt() { return createdAt; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}
