package com.example.usermgmt.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class TokenGenerator {
    private static final SecureRandom RAND = new SecureRandom();

    public String tokenHex(int bytes) {
        byte[] data = new byte[bytes];
        RAND.nextBytes(data);
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
