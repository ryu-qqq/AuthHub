package com.ryuqq.authhub.application.auth.port.out.query;

import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.Optional;

/**
 * RefreshTokenQueryPort - RefreshToken 조회 포트 (Query)
 *
 * <p>RefreshToken을 조회하는 읽기 전용 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>조회 메서드만 제공 (findByUserId, existsByUserId)
 *   <li>저장/수정/삭제 메서드 금지 (PersistencePort로 분리)
 *   <li>Value Object 파라미터 (UserId)
 *   <li>Optional 반환 (단건 조회 시 null 방지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RefreshTokenQueryPort {

    /**
     * UserId로 RefreshToken 조회
     *
     * @param userId 사용자 ID (Value Object)
     * @return RefreshToken 문자열 (Optional)
     */
    Optional<String> findByUserId(UserId userId);

    /**
     * UserId로 RefreshToken 존재 여부 확인
     *
     * @param userId 사용자 ID (Value Object)
     * @return 존재 여부
     */
    boolean existsByUserId(UserId userId);

    /**
     * RefreshToken으로 UserId 조회
     *
     * @param refreshToken RefreshToken 문자열
     * @return UserId (Optional)
     */
    Optional<UserId> findUserIdByToken(String refreshToken);
}
