package com.ryuqq.authhub.application.user.port.out.client;

import com.ryuqq.authhub.domain.user.vo.HashedPassword;

/**
 * PasswordEncoderClient - 비밀번호 해싱 클라이언트
 *
 * <p>비밀번호 해싱 및 검증을 위한 외부 어댑터 Client입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>비밀번호 평문 저장 금지
 *   <li>BCrypt 등 보안 해시 알고리즘 사용
 *   <li>솔트 자동 생성 (동일 입력 → 다른 해시값)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface PasswordEncoderClient {

    /**
     * 비밀번호 해싱
     *
     * @param rawPassword 평문 비밀번호
     * @return 해싱된 비밀번호 문자열
     */
    String hash(String rawPassword);

    /**
     * 비밀번호 일치 여부 확인
     *
     * @param rawPassword 평문 비밀번호
     * @param hashedPassword 해싱된 비밀번호 VO
     * @return 일치하면 true
     */
    boolean matches(String rawPassword, HashedPassword hashedPassword);
}
