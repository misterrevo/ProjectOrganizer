package com.revo.authservice.domain.port;

public interface Encoder {
    String encodePassword(String rawPassword);
    boolean passwordMatches(String rawPassword, String encodedPassword);
}
