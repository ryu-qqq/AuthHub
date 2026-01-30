package com.ryuqq.authhub.adapter.out.client.security.password;

import com.ryuqq.authhub.application.user.port.out.client.PasswordEncoderClient;
import com.ryuqq.authhub.domain.user.vo.HashedPassword;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * BCryptPasswordClient - BCrypt 비밀번호 해싱 Client
 *
 * <p>Spring Security의 BCryptPasswordEncoder를 사용합니다.
 *
 * <p><strong>BCrypt 특징:</strong>
 *
 * <ul>
 *   <li>솔트 자동 생성 (매번 다른 해시값)
 *   <li>Work Factor 조절 가능 (기본값 10)
 *   <li>레인보우 테이블 공격 방지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class BCryptPasswordClient implements PasswordEncoderClient {

    private static final int BCRYPT_STRENGTH = 10;

    private final BCryptPasswordEncoder passwordEncoder;

    public BCryptPasswordClient() {
        this.passwordEncoder = new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    @Override
    public String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, HashedPassword hashedPassword) {
        if (rawPassword == null || hashedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, hashedPassword.value());
    }
}
