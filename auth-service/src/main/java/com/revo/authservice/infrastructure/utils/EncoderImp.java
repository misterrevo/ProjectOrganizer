package com.revo.authservice.infrastructure.utils;

import com.revo.authservice.domain.port.Encoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
class EncoderImp implements Encoder {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    EncoderImp() {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String encodePassword(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    @Override
    public boolean passwordMatches(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
