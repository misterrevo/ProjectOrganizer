package com.revo.authservice.domain.port;

public interface EncoderPort {
    String encodePassword(String rawPassword);
    boolean passwordMatches(String rawPassword, String encodedPassword);
}
