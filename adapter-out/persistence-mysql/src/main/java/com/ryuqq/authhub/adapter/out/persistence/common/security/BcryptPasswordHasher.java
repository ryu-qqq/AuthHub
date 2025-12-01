package com.ryuqq.authhub.adapter.out.persistence.common.security;

import com.ryuqq.authhub.application.common.port.out.security.PasswordHasherPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * BcryptPasswordHasher - BCrypt 기반 비밀번호 해싱 구현체
 *
 * <p>PasswordHasherPort를 구현하여 BCrypt 알고리즘으로 비밀번호를 해싱합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class BcryptPasswordHasher implements PasswordHasherPort {

    private final BCryptPasswordEncoder passwordEncoder;

    public BcryptPasswordHasher() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * 비밀번호 해싱
     *
     * @param rawPassword Plain text 비밀번호
     * @return BCrypt 해시된 비밀번호
     */
    @Override
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 비밀번호 검증
     *
     * @param rawPassword Plain text 비밀번호
     * @param hashedPassword BCrypt 해시된 비밀번호
     * @return 일치하면 true
     */
    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
