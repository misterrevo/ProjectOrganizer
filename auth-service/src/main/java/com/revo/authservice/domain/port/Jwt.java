package com.revo.authservice.domain.port;

public interface Jwt {
    String createTokenFromUsername(String username);
    String getSubjectFromToken(String token);
}
