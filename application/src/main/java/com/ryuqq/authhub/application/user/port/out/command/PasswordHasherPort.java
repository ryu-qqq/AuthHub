package com.ryuqq.authhub.application.user.port.out.command;

/**
 * PasswordHasherPort - 비밀번호 해싱 포트
 *
 * <p>비밀번호 해싱 및 검증을 위한 아웃바운드 포트입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public interface PasswordHasherPort {

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
