package com.ryuqq.setof.adapter.out.security.password;

import com.ryuqq.setof.application.member.port.out.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * BCrypt Password Encoder Adapter
 *
 * <p>Spring Security의 BCryptPasswordEncoder를 사용하여 PasswordEncoderPort를 구현합니다.
 *
 * <p><strong>BCrypt 특징:</strong>
 * <ul>
 *   <li>솔트 자동 생성 (매번 다른 해시값)</li>
 *   <li>Work Factor 조절 가능 (기본값 10)</li>
 *   <li>레인보우 테이블 공격 방지</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private static final int BCRYPT_STRENGTH = 10;

    private final BCryptPasswordEncoder passwordEncoder;

    public BCryptPasswordEncoderAdapter() {
        this.passwordEncoder = new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    @Override
    public String encode(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
