package com.ryuqq.authhub.application.auth.port.out.command;

import com.ryuqq.authhub.domain.user.identifier.UserId;

/**
 * RefreshTokenPersistencePort - RefreshToken 영속화 포트 (Command)
 *
 * <p>RefreshToken을 영속화하는 Command 전용 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 * <ul>
 *   <li>persist() 메서드로 토큰 저장/갱신</li>
 *   <li>deleteByUserId() 메서드로 토큰 삭제 (로그아웃 시)</li>
 *   <li>Value Object 파라미터 (UserId)</li>
 *   <li>조회 메서드 금지 (QueryPort로 분리)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RefreshTokenPersistencePort {

    /**
     * RefreshToken 저장 또는 갱신
     *
     * <p>동일한 UserId에 대해 기존 토큰이 있으면 갱신, 없으면 저장
     *
     * @param userId 사용자 ID (Value Object)
     * @param token RefreshToken 문자열
     */
    void persist(UserId userId, String token);

    /**
     * UserId로 RefreshToken 삭제
     *
     * <p>로그아웃 또는 보안 목적으로 토큰 무효화 시 사용
     *
     * @param userId 사용자 ID (Value Object)
     */
    void deleteByUserId(UserId userId);
}
