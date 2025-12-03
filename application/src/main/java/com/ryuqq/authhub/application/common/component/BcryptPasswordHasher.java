package com.ryuqq.authhub.application.common.component;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * BcryptPasswordHasher - BCrypt 기반 비밀번호 해싱 구현체
 *
 * <p>BCrypt 알고리즘으로 비밀번호를 해싱합니다. Application 레이어에 위치하여 외부 의존성 없이 사용 가능합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class BcryptPasswordHasher implements PasswordHasher {

    private final BCryptPasswordEncoder passwordEncoder;

    public BcryptPasswordHasher() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
