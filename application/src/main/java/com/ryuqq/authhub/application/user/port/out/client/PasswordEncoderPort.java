package com.ryuqq.authhub.application.user.port.out.client;

/**
 * PasswordHasher - 비밀번호 해싱 인터페이스
 *
 * <p>비밀번호 해싱 및 검증을 위한 인터페이스입니다. Application 레이어에서 사용하며, 구현체는 BCrypt 등을 사용합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public interface PasswordEncoderPort {

    /**
     * 비밀번호 해싱
     *
     * @param rawPassword Plain text 비밀번호
     * @return 해시된 비밀번호
     */
    String hash(String rawPassword);

    /**
     * 비밀번호 검증
     *
     * @param rawPassword Plain text 비밀번호
     * @param hashedPassword 해시된 비밀번호
     * @return 일치하면 true
     */
    boolean matches(String rawPassword, String hashedPassword);
}
