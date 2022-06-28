package com.revo.authservice.domain.port;

public interface EncoderPort {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
